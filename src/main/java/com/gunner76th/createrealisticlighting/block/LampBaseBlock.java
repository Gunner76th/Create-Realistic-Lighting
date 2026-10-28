package com.gunner76th.createrealisticlighting.block;

import com.gunner76th.createrealisticlighting.blockentity.LampBaseBlockEntity;
import com.gunner76th.createrealisticlighting.registry.ModBlockEntities;
import com.gunner76th.createrealisticlighting.registry.ModItems;
import com.mojang.serialization.MapCodec;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.patryk3211.powergrid.electricity.base.IDecoratedTerminal;
import org.patryk3211.powergrid.electricity.base.IElectric;
import org.patryk3211.powergrid.electricity.base.ITerminalPlacement;
import org.patryk3211.powergrid.electricity.base.TerminalBoundingBox;

public class LampBaseBlock extends BaseEntityBlock
    implements IElectric {

  // One terminal at the center of each side of the mounting plate.
  // 0 = live in (west), 1 = neutral in (south),
  // 2 = live out (east), 3 = neutral out (north).
  // Thus each corner exposes one input and one output.
  @Override
  public int terminalCount() {
    return 4;
  }

  @Override
  public ITerminalPlacement terminal(BlockState state, int index) {
    if (index < 0 || index >= terminalCount()) return null;

    Direction facing = state.hasProperty(IndustrialFixtureBlock.FACING)
        ? state.getValue(IndustrialFixtureBlock.FACING) : Direction.UP;

    // Pad centers match the model geometry. Bounds are in 1/16-block units.
    // The hitboxes extend slightly outward to make wire placement practical.
    double x1, z1, x2, z2;
    switch (index) {
      case 0 -> { x1 = 2.0; z1 = 6.0; x2 = 5.0; z2 = 10.0; } // west, live in
      case 1 -> { x1 = 6.0; z1 = 11.0; x2 = 10.0; z2 = 14.0; } // south, neutral in
      case 2 -> { x1 = 11.0; z1 = 6.0; x2 = 14.0; z2 = 10.0; } // east, live out
      case 3 -> { x1 = 6.0; z1 = 2.0; x2 = 10.0; z2 = 5.0; } // north, neutral out
      default -> { return null; }
    }
    // The square ceiling diffuser reaches close to the inner edges of the pads.
    // Move only this fixture's clickable terminal regions one and a half model pixels
    // outward, onto the visible copper contacts (the model geometry is unchanged).
    if (this instanceof SquareCeilingFixtureBlock) {
      switch (index) {
        case 0 -> { x1 -= 1.5; x2 -= 1.5; } // west
        case 1 -> { z1 += 1.5; z2 += 1.5; } // south
        case 2 -> { x1 += 1.5; x2 += 1.5; } // east
        case 3 -> { z1 -= 1.5; z2 -= 1.5; } // north
        default -> { }
      }
    }
    return switch (facing) {
      case UP -> terminalBox(x1, 1.0, z1, x2, 4.0, z2, index);
      case DOWN -> terminalBox(x1, 12.0, z1, x2, 15.0, z2, index);
      case NORTH -> terminalBox(x1, z1, 12.0, x2, z2, 15.0, index);
      case SOUTH -> terminalBox(x1, z1, 1.0, x2, z2, 4.0, index);
      case EAST -> terminalBox(1.0, z1, x1, 4.0, z2, x2, index);
      case WEST -> terminalBox(12.0, z1, x1, 15.0, z2, x2, index);
    };
  }

  private static TerminalBoundingBox terminalBox(
      double x1, double y1, double z1, double x2, double y2, double z2, int index) {
    int color = (index == 0 || index == 2) ? IDecoratedTerminal.RED : IDecoratedTerminal.BLUE;
    return new TerminalBoundingBox(IDecoratedTerminal.CONNECTOR, x1, y1, z1, x2, y2, z2).withColor(color);
  }

  public static final MapCodec<LampBaseBlock> CODEC = simpleCodec(LampBaseBlock::new);

  @Override
  protected MapCodec<? extends BaseEntityBlock> codec() {
    return CODEC;
  }

  public static final BooleanProperty LIT = BooleanProperty.create("lit");

  public LampBaseBlock(Properties properties) {
    super(properties);
    registerDefaultState(stateDefinition.any().setValue(LIT, false));
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    builder.add(LIT);
  }

  /**
   * Keep the selection outline and physical collision close to the visible fixture
   * instead of Minecraft's default full-block cube. The model occupies roughly
   * 2.5..13.5 pixels across its width/depth and 0..15 pixels away from its mount.
   */
  private static VoxelShape fixtureShape(BlockState state) {
    Direction facing = state.hasProperty(IndustrialFixtureBlock.FACING)
        ? state.getValue(IndustrialFixtureBlock.FACING)
        : Direction.UP;

    return switch (facing) {
      case UP -> Shapes.box(2.5 / 16.0, 0.0, 2.5 / 16.0,
          13.5 / 16.0, 15.0 / 16.0, 13.5 / 16.0);
      case DOWN -> Shapes.box(2.5 / 16.0, 1.0 / 16.0, 2.5 / 16.0,
          13.5 / 16.0, 1.0, 13.5 / 16.0);
      case NORTH -> Shapes.box(2.5 / 16.0, 2.5 / 16.0, 0.0,
          13.5 / 16.0, 13.5 / 16.0, 15.0 / 16.0);
      case SOUTH -> Shapes.box(2.5 / 16.0, 2.5 / 16.0, 1.0 / 16.0,
          13.5 / 16.0, 13.5 / 16.0, 1.0);
      case EAST -> Shapes.box(1.0 / 16.0, 2.5 / 16.0, 2.5 / 16.0,
          1.0, 13.5 / 16.0, 13.5 / 16.0);
      case WEST -> Shapes.box(0.0, 2.5 / 16.0, 2.5 / 16.0,
          15.0 / 16.0, 13.5 / 16.0, 13.5 / 16.0);
    };
  }

  @Override
  public VoxelShape getShape(
      BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
    return fixtureShape(state);
  }

  @Override
  protected RenderShape getRenderShape(BlockState state) {
    return RenderShape.MODEL;
  }

  @Override
  public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return ModBlockEntities.createFixture(pos, state);
  }

  @Nullable
  @Override
  public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
      Level level, BlockState state, BlockEntityType<T> type) {
    return createTickerHelper(
        type, ModBlockEntities.LAMP_BASE.get(),
        (world, blockPos, blockState, entity) -> entity.tick());
  }

  @Override
  protected ItemInteractionResult useItemOn(
      ItemStack held,
      BlockState state,
      Level level,
      BlockPos pos,
      Player player,
      InteractionHand hand,
      BlockHitResult hit) {
    if (hand != InteractionHand.MAIN_HAND) {
      return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    if (!(level.getBlockEntity(pos) instanceof LampBaseBlockEntity lamp)) {
      return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    // Let Create handle wrench rotation and sneak-wrench removal.
    if (held.getItem() instanceof com.simibubi.create.content.equipment.wrench.WrenchItem) {
      return ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
    }

    if (player.isShiftKeyDown()) {
      removeInstalledBulb(level, player, lamp);
      return ItemInteractionResult.sidedSuccess(level.isClientSide);
    }

    if (isBulb(held)) {
      if (lamp.getBulb().isEmpty()) {
        if (!level.isClientSide) {
          ItemStack installed = held.copyWithCount(1);
          lamp.setBulb(installed);

          if (!player.getAbilities().instabuild) {
            held.shrink(1);
          }
        }
      }
      return ItemInteractionResult.sidedSuccess(level.isClientSide);
    }

    return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
  }

  @Override
  protected InteractionResult useWithoutItem(
      BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
    if (!(level.getBlockEntity(pos) instanceof LampBaseBlockEntity lamp)) {
      return InteractionResult.PASS;
    }

    if (player.isShiftKeyDown()) {
      removeInstalledBulb(level, player, lamp);
      return InteractionResult.sidedSuccess(level.isClientSide);
    }

    if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
      serverPlayer.openMenu(lamp, pos);
    }

    return InteractionResult.sidedSuccess(level.isClientSide);
  }

  private static boolean isBulb(ItemStack stack) {
    return stack.is(ModItems.INCANDESCENT_BULB.get())
        || stack.is(ModItems.FLUORESCENT_BULB.get())
        || stack.is(ModItems.LED_BULB.get())
        || stack.is(ModItems.GROW_BULB.get());
  }

  private static void removeInstalledBulb(Level level, Player player, LampBaseBlockEntity lamp) {
    if (level.isClientSide || lamp.getBulb().isEmpty()) {
      return;
    }

    ItemStack removed = lamp.removeBulb();

    if (!player.getInventory().add(removed)) {
      player.drop(removed, false);
    }
  }

  @Override
  protected void neighborChanged(
      BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean moving) {
    super.neighborChanged(state, level, pos, block, fromPos, moving);

    if (!level.isClientSide && level.getBlockEntity(pos) instanceof LampBaseBlockEntity lamp) {
      lamp.refreshLighting();
    }
  }

  @Override
  protected void onRemove(
      BlockState state, Level level, BlockPos pos, BlockState newState, boolean moving) {
    if (!state.is(newState.getBlock()) && !level.isClientSide) {
      BlockEntity blockEntity = level.getBlockEntity(pos);

      if (blockEntity instanceof LampBaseBlockEntity lamp) {

        // Remove extended lighting nodes.
        lamp.clearLedNodes();

        // The loot system handles the installed bulb drop.
        // Do not drop it again during block removal.
      }
    }

    super.onRemove(state, level, pos, newState, moving);
  }

  @Override
  protected List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
    List<ItemStack> drops = super.getDrops(state, builder);

    BlockEntity blockEntity =
        builder.getOptionalParameter(
            net.minecraft.world.level.storage.loot.parameters.LootContextParams.BLOCK_ENTITY);

    if (blockEntity instanceof LampBaseBlockEntity lamp) {
      ItemStack bulbDrop = lamp.getBulbDrop();

      if (!bulbDrop.isEmpty()) {
        drops.add(bulbDrop);
      }
    }

    return drops;
  }
}
