package com.gunner76th.createrealisticlighting.registry;

import com.gunner76th.createrealisticlighting.CreateRealisticLighting;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import java.util.List;
import java.util.Locale;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredItem;

public final class ModItems {
    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(CreateRealisticLighting.MOD_ID);

private static Item.Properties properties(String name) {
    return new Item.Properties();
}

    public static final DeferredItem<Item> INCANDESCENT_BULB =
            ITEMS.register("incandescent_bulb",
                    () -> new LifespanBulbItem(properties("incandescent_bulb"), 30));

    public static final DeferredItem<Item> FLUORESCENT_BULB =
            ITEMS.register("fluorescent_bulb",
                    () -> new LifespanBulbItem(properties("fluorescent_bulb"), 90));

    public static final DeferredItem<Item> GROW_BULB =
            ITEMS.register("grow_bulb",
                    () -> new LifespanBulbItem(properties("grow_bulb"), 90));

    public static final DeferredItem<Item> LED_BULB =
            ITEMS.register("led_bulb",
                    () -> new LifespanBulbItem(properties("led_bulb"), 300));

    public static final DeferredItem<BlockItem> INDUSTRIAL_FIXTURE_ITEM =
            ITEMS.register("industrial_fixture",
                    () -> new BlockItem(
                            ModBlocks.INDUSTRIAL_FIXTURE.get(),
                            properties("industrial_fixture")));

    public static final DeferredItem<BlockItem> SQUARE_CEILING_FIXTURE_ITEM =
            ITEMS.register("square_ceiling_fixture",
                    () -> new BlockItem(
                            ModBlocks.SQUARE_CEILING_FIXTURE.get(),
                            properties("square_ceiling_fixture")));

    public static final DeferredItem<BlockItem> WALL_SCONCE_ITEM =
            ITEMS.register("wall_sconce",
                    () -> new BlockItem(
                            ModBlocks.WALL_SCONCE.get(),
                            properties("wall_sconce")));

    /** Each item retains its individual wear in CUSTOM_DATA when removed from a fixture. */
    private static final class LifespanBulbItem extends Item {
        private static final String WEAR_KEY = "RealisticLightingAccumulatedWear";
        private final int ratedDays;

        private LifespanBulbItem(Properties properties, int ratedDays) {
            super(properties);
            this.ratedDays = ratedDays;
        }

        @Override
        public void appendHoverText(ItemStack stack, TooltipContext context,
                                    List<Component> tooltip, TooltipFlag flag) {
            super.appendHoverText(stack, context, tooltip, flag);
            double totalTicks = ratedDays * 24000.0;
            CustomData customData = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
            double wear = customData.copyTag().getDouble(WEAR_KEY);
            if (!Double.isFinite(wear)) wear = 0.0;
            wear = Math.max(0.0, Math.min(totalTicks, wear));
            double remainingDays = (totalTicks - wear) / 24000.0;
            double percent = 100.0 * (totalTicks - wear) / totalTicks;
            String condition = percent <= 0 ? "Burned Out"
                    : percent <= 20 ? "Near Burnout"
                    : percent <= 50 ? "Aging" : "Healthy";
            ChatFormatting color = percent <= 20 ? ChatFormatting.RED
                    : percent <= 50 ? ChatFormatting.YELLOW : ChatFormatting.GREEN;
            tooltip.add(Component.literal(String.format(Locale.ROOT,
                    "Life Remaining: %.1f / %.1f days", remainingDays, (double) ratedDays))
                    .withStyle(ChatFormatting.GRAY));
            tooltip.add(Component.literal(String.format(Locale.ROOT,
                    "Condition: %.1f%% — %s", percent, condition)).withStyle(color));
        }
    }

    private ModItems() {}
}
