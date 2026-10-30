package com.gunner76th.createrealisticlighting.blockentity;

import com.gunner76th.createrealisticlighting.block.IndustrialFixtureBlock;
import com.gunner76th.createrealisticlighting.block.LampBaseBlock;
import com.gunner76th.createrealisticlighting.logic.LedExtendedLighting;
import com.gunner76th.createrealisticlighting.menu.LampMenu;
import com.gunner76th.createrealisticlighting.registry.ModBlockEntities;
import com.gunner76th.createrealisticlighting.registry.ModItems;
import java.util.HashSet;
import java.util.List;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import org.patryk3211.powergrid.electricity.base.ElectricBlockEntity;
import org.patryk3211.powergrid.electricity.base.IElectricEntity.CircuitBuilder;
import org.patryk3211.powergrid.electricity.sim.SwitchedWire;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class LampBaseBlockEntity extends ElectricBlockEntity implements MenuProvider {
  // 0 = On, 1 = Off, 2 = Redstone
  private int operatingMode = 0;

  public int getOperatingMode() {
    return operatingMode;
  }

  public void setOperatingMode(int mode) {
    if (mode < 0 || mode > 2) return;
    operatingMode = mode;
    updateLitState();
    setChanged();
    sync();
  }

  public boolean isRedstonePowered() {
    return level != null && level.hasNeighborSignal(worldPosition);
  }

  private boolean enabled() {
    return operatingMode == 0 || (operatingMode == 2 && isRedstonePowered());
  }

  // Electrical readings are sampled from the Power Grid circuit, not stored FE.
  public int getVoltageTenths() { return (int) Math.round(Math.min(32767, voltage * 10)); }
  public int getPowerTenths() { return (int) Math.round(Math.min(32767, powerWatts * 10)); }
  public int getCurrentMilliamps() { return (int) Math.round(Math.min(32767, currentAmps * 1000)); }

  public long getAgeTicks() {
    return ageTicks;
  }

  public int getEnergyCost() { return (int) Math.round(nominalPower()); }

  public long getLifespanTicks() {
    return lifespan();
  }

  @Override
  public Component getDisplayName() {
    return getBlockState().getBlock().getName();
  }

  @Override
  public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
    return new LampMenu(id, inventory, this);
  }

  private ItemStack bulb = ItemStack.EMPTY;
  private long ageTicks = 0;
  private transient SwitchedWire loadWire;
  private double voltage;
  private double currentAmps;
  private double powerWatts;
  private static final double MINIMUM_VOLTAGE = 60.0;
  private static final double SHUTDOWN_VOLTAGE = 100.0;
  private static final double RECOVERY_VOLTAGE = 105.0;
  private static final double NOMINAL_VOLTAGE = 120.0;

  private double nominalPower() {
    // Power Grid may call buildCircuit() from the superclass constructor,
    // before this class's instance fields (including bulb) are initialized.
    if (bulb == null || bulb.isEmpty()) return 0;
    if (bulb.is(ModItems.INCANDESCENT_BULB.get())) return 60;
    if (bulb.is(ModItems.FLUORESCENT_BULB.get())) return 30;
    if (bulb.is(ModItems.LED_BULB.get())) return 15;
    if (bulb.is(ModItems.GROW_BULB.get())) return 45;
    return 0;
  }

  private boolean canDrawPower() {
    // Safe during superclass construction and while the fixture has no bulb.
    return bulb != null && enabled() && !bulb.isEmpty() && ageTicks < lifespan();
  }

  private void updateLoad() {
    if (loadWire == null) return;
    double watts = nominalPower();
    loadWire.setResistance(watts > 0 ? NOMINAL_VOLTAGE * NOMINAL_VOLTAGE / watts : 1_000_000);
    loadWire.setState(canDrawPower() && watts > 0);
  }

  @Override
  public void buildCircuit(CircuitBuilder builder) {
    // Terminal order: live-in, neutral-in, live-out, neutral-out.
    // Unswitched low-resistance bus conductors maintain downstream supply
    // even when the bulb is removed, burned out, or switched off.
    builder.setTerminalCount(4);
    builder.connect(0.001f, builder.terminalNode(0), builder.terminalNode(2));
    builder.connect(0.001f, builder.terminalNode(1), builder.terminalNode(3));
    loadWire = builder.connectSwitch(240.0f, builder.terminalNode(0), builder.terminalNode(1), false);
    updateLoad();
  }

  @Override
  public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
    super.addBehaviours(behaviours);
  }

  @Override
  public void electricalTick() {
    updateLoad();
    voltage = loadWire != null ? Math.abs(loadWire.potentialDifference()) : 0;
    currentAmps = loadWire != null ? Math.abs(loadWire.current()) : 0;
    powerWatts = loadWire != null ? Math.abs(loadWire.power()) : 0;
    runLightingTick();
  }

  private boolean voltageEligible = false;
  private boolean lastLit;
  private final Set<BlockPos> ledNodes = new HashSet<>();

  /** Reconcile periodically to handle changed obstacles and node replacement. */
  private void updateLedNodes(boolean active) {
    if (level == null || level.isClientSide || (!active && ledNodes.isEmpty())) return;
    Set<BlockPos> desired =
        active ? LedExtendedLighting.desired(level, worldPosition, getBlockState()) : Set.of();
    for (BlockPos old : new HashSet<>(ledNodes)) {
      if (desired.contains(old)) continue;
      if (!level.hasChunkAt(old)) continue; // Retry after that chunk loads.
      LedExtendedLighting.remove(level, worldPosition, Set.of(old));
      ledNodes.remove(old);
    }
    if (active) {
      LedExtendedLighting.add(level, worldPosition, getBlockState(), desired);
      ledNodes.addAll(desired);
    }
    setChanged();
  }

  public void clearLedNodes() {
    updateLedNodes(false);
  }

  public boolean ownsLedNode(BlockPos pos) {
    return ledNodes.contains(pos);
  }

  public boolean isExtendedLedActive() {
    return enabled()
        && bulb.is(ModItems.LED_BULB.get())
        && ageTicks < lifespan()
        && isVoltageEligible();
  }

  public LampBaseBlockEntity(BlockPos pos, BlockState state) {
    super(ModBlockEntities.LAMP_BASE.get(), pos, state);
  }

  public ItemStack getBulb() {
    return bulb;
  }

  private static final String BULB_AGE_KEY = "RealisticLightingAgeTicks";

  public void setBulb(ItemStack stack) {
    bulb = stack.copyWithCount(1);

    if (bulb.isEmpty()) {
      ageTicks = 0;
    } else {
      CustomData data = bulb.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);

      ageTicks = Mth.clamp(data.copyTag().getLong(BULB_AGE_KEY), 0L, lifespan());
    }

    updateLitState();
    setChanged();
    sync();
  }

  public ItemStack getBulbDrop() {
    ItemStack result = bulb.copy();

    if (!result.isEmpty()) {
      CompoundTag data =
          result.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();

      data.putLong(BULB_AGE_KEY, ageTicks);

      result.set(DataComponents.CUSTOM_DATA, CustomData.of(data));
    }

    return result;
  }

  public ItemStack removeBulb() {
    ItemStack result = getBulbDrop();

    bulb = ItemStack.EMPTY;
    ageTicks = 0;

    updateLitState();
    setChanged();
    sync();

    return result;
  }

  private long lifespan() {
    if (bulb.is(ModItems.INCANDESCENT_BULB.get())) return 30L * 24_000L;
    if (bulb.is(ModItems.FLUORESCENT_BULB.get()) || bulb.is(ModItems.GROW_BULB.get()))
      return 90L * 24_000L;
    if (bulb.is(ModItems.LED_BULB.get())) return 300L * 24_000L;
    return 0;
  }

  private boolean isVoltageEligible() {
    return bulb.is(ModItems.INCANDESCENT_BULB.get())
        ? voltage >= MINIMUM_VOLTAGE
        : voltageEligible;
  }

  private boolean hasSufficientVoltage() {
    if (bulb.is(ModItems.INCANDESCENT_BULB.get())) {
      return voltage >= MINIMUM_VOLTAGE;
    }

    if (voltage < SHUTDOWN_VOLTAGE) {
      voltageEligible = false;
    } else if (voltage >= RECOVERY_VOLTAGE) {
      voltageEligible = true;
    }

    return voltageEligible;
  }
  private int desiredLightLevel(boolean lit) {
    if (!lit) return 0;
    if (!bulb.is(ModItems.INCANDESCENT_BULB.get())) return 15;
    double normalized = Mth.clamp((voltage - MINIMUM_VOLTAGE) /
        (NOMINAL_VOLTAGE - MINIMUM_VOLTAGE), 0.0, 1.0);
    return Mth.clamp((int) Math.round(1.0 + 14.0 * Math.pow(normalized, 1.5)), 1, 15);
  }

  private void runLightingTick() {
    if (level == null || level.isClientSide) return;
    boolean sufficientVoltage = hasSufficientVoltage();
    boolean lit = canDrawPower() && sufficientVoltage;
    if (lit) {
      ageTicks++;
      if (bulb.is(ModItems.GROW_BULB.get()) && level.getGameTime() % 20 == 0)
        com.gunner76th.createrealisticlighting.logic.GrowLampGrowth.apply(level, worldPosition);
    }
    int desiredLightLevel = desiredLightLevel(lit);
    if (lastLit != lit || getBlockState().getValue(LampBaseBlock.LIGHT_LEVEL) != desiredLightLevel) {
      lastLit = lit;
      updateLitState();
      sync();
    }
    if (level.getGameTime() % 40 == 0)
      updateLedNodes(lit && bulb.is(ModItems.LED_BULB.get()));
    if (lit || level.getGameTime() % 100 == 0) setChanged();
  }

  public void refreshLighting() {
    updateLitState();
    setChanged();
    sync();
  }

  private void updateLitState() {
    if (level == null || level.isClientSide) return;
    boolean shouldLight =
        enabled()
            && !bulb.isEmpty()
            && ageTicks < lifespan()
            && isVoltageEligible();
    BlockState current = getBlockState();
    if (current.hasProperty(IndustrialFixtureBlock.BULB_KIND)) {
      int kind =
          bulb.is(ModItems.INCANDESCENT_BULB.get())
              ? 1
              : bulb.is(ModItems.FLUORESCENT_BULB.get())
                  ? 2
                  : bulb.is(ModItems.LED_BULB.get())
                      ? 3
                      : bulb.is(ModItems.GROW_BULB.get()) ? 4 : 0;
      if (current.getValue(IndustrialFixtureBlock.BULB_KIND) != kind) {
        current = current.setValue(IndustrialFixtureBlock.BULB_KIND, kind);
        level.setBlock(worldPosition, current, 3);
      }
    }
    int lightLevel = desiredLightLevel(shouldLight);
    if (current.hasProperty(LampBaseBlock.LIT)
        && (current.getValue(LampBaseBlock.LIT) != shouldLight
            || current.getValue(LampBaseBlock.LIGHT_LEVEL) != lightLevel)) {
      level.setBlock(worldPosition,
          current.setValue(LampBaseBlock.LIT, shouldLight)
              .setValue(LampBaseBlock.LIGHT_LEVEL, lightLevel), 3);
    }
    lastLit = shouldLight;
    updateLedNodes(shouldLight && bulb.is(ModItems.LED_BULB.get()));
  }

  private void sync() {
    if (level != null && !level.isClientSide)
      level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
  }

  @Override
  protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
    super.write(tag, registries, clientPacket);

    if (!bulb.isEmpty()) {
      tag.put("Bulb", bulb.save(registries));
    }

    tag.putLong("AgeTicks", ageTicks);
    tag.putInt("OperatingMode", operatingMode);

    long[] nodes = ledNodes.stream().mapToLong(BlockPos::asLong).toArray();

    tag.putLongArray("LedNodes", nodes);
  }

  @Override
  protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
    super.read(tag, registries, clientPacket);

    bulb =
        tag.contains("Bulb", Tag.TAG_COMPOUND)
            ? ItemStack.parseOptional(registries, tag.getCompound("Bulb"))
            : ItemStack.EMPTY;

    ageTicks = tag.getLong("AgeTicks");

    operatingMode = Math.max(0, Math.min(2, tag.getInt("OperatingMode")));

    lastLit = false;

    ledNodes.clear();

    for (long packed : tag.getLongArray("LedNodes")) {
      ledNodes.add(BlockPos.of(packed));
    }
  }

  @Override
  public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
    return saveWithoutMetadata(registries);
  }

  @Override
  public ClientboundBlockEntityDataPacket getUpdatePacket() {
    return ClientboundBlockEntityDataPacket.create(this);
  }
}
