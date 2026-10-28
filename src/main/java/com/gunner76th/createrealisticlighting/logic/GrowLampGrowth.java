package com.gunner76th.createrealisticlighting.logic;

import com.gunner76th.createrealisticlighting.block.IndustrialFixtureBlock;
import com.gunner76th.createrealisticlighting.block.LampBaseBlock;
import com.gunner76th.createrealisticlighting.blockentity.LampBaseBlockEntity;
import com.gunner76th.createrealisticlighting.registry.ModItems;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.NetherWartBlock;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;

/** Server-side directional grow bulb bonus, with diminishing returns in overlapping coverage. */
public final class GrowLampGrowth {
    private static final int RANGE = 5;
    private static final int OVERHEAD_HALF_WIDTH = 2; // 5x5 crop footprint
    private GrowLampGrowth() {}

    public static void apply(Level level, BlockPos lamp) {
        if (!(level instanceof ServerLevel server)) return;
        BlockState lampState = server.getBlockState(lamp);
        if (!lampState.hasProperty(IndustrialFixtureBlock.FACING)) return;
        Direction facing = lampState.getValue(IndustrialFixtureBlock.FACING);
        List<BlockPos> eligible = new ArrayList<>();
        for (int dx = -RANGE; dx <= RANGE; dx++) {
            for (int dy = -RANGE; dy <= RANGE; dy++) {
                for (int dz = -RANGE; dz <= RANGE; dz++) {
                    BlockPos crop = lamp.offset(dx, dy, dz);
                    if (!inBeam(lamp, crop, facing) || !server.hasChunkAt(crop)) continue;
                    BlockState state = server.getBlockState(crop);
                    if (isGrowingCrop(state) && unobstructed(server, lamp, crop, facing))
                        eligible.add(crop);
                }
            }
        }
        if (eligible.isEmpty()) return;
        BlockPos target = eligible.get(server.random.nextInt(eligible.size()));
        int coverage = countActiveLamps(server, target);
        if (coverage == 0) return;
        // Preserve the existing diminishing-returns cap on overlapping lamps.
        double acceptance = 2.0 * (1.0 - Math.pow(0.5, coverage)) / coverage;
        if (server.random.nextDouble() >= acceptance) return;
        BlockState state = server.getBlockState(target);
        if (isGrowingCrop(state)) state.randomTick(server, target, server.random);
    }

    private static boolean isGrowingCrop(BlockState state) {
        return state.isRandomlyTicking() && (state.getBlock() instanceof CropBlock
                || state.getBlock() instanceof StemBlock || state.getBlock() instanceof NetherWartBlock);
    }

    private static boolean inBeam(BlockPos lamp, BlockPos crop, Direction facing) {
        int dx = crop.getX() - lamp.getX();
        int dy = crop.getY() - lamp.getY();
        int dz = crop.getZ() - lamp.getZ();
        int horizontalSquared = dx * dx + dz * dz;
        if (facing == Direction.UP) {
            // A floor-mounted fixture reaches adjacent farmland, as well as crops
            // slightly above it; do not accelerate crops deep below the floor.
            return dy >= -1 && dy <= 2 && horizontalSquared <= RANGE * RANGE;
        }
        if (facing == Direction.DOWN) {
            // A downward-facing fixture mounted five blocks above the crop layer
            // covers exactly a centered 5x5 footprint, including the corners.
            // Shorter mounting heights retain that footprint if unobstructed.
            int depth = -dy;
            return depth >= 1 && depth <= RANGE
                    && Math.abs(dx) <= OVERHEAD_HALF_WIDTH
                    && Math.abs(dz) <= OVERHEAD_HALF_WIDTH;
        }
        int forward = dx * facing.getStepX() + dz * facing.getStepZ();
        int sideways = dx * facing.getStepZ() - dz * facing.getStepX();
        return forward >= 1 && forward <= RANGE
                && sideways * sideways + dy * dy <= forward * forward;
    }

    private static boolean unobstructed(ServerLevel level, BlockPos lamp, BlockPos crop, Direction facing) {
        Vec3 from = Vec3.atCenterOf(lamp).add(
                facing.getStepX() * 0.65, facing.getStepY() * 0.65, facing.getStepZ() * 0.65);
        Vec3 to = Vec3.atCenterOf(crop);
        BlockHitResult hit = level.clip(new ClipContext(
                from, to, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, CollisionContext.empty()));
        return hit.getType() == HitResult.Type.MISS || hit.getBlockPos().equals(crop);
    }

    private static int countActiveLamps(ServerLevel level, BlockPos crop) {
        int count = 0;
        for (int dx = -RANGE; dx <= RANGE; dx++) {
            for (int dy = -RANGE; dy <= RANGE; dy++) {
                for (int dz = -RANGE; dz <= RANGE; dz++) {
                    BlockPos p = crop.offset(dx, dy, dz);
                    if (!level.hasChunkAt(p)) continue;
                    BlockState state = level.getBlockState(p);
                    if (!state.hasProperty(LampBaseBlock.LIT) || !state.getValue(LampBaseBlock.LIT)
                            || !state.hasProperty(IndustrialFixtureBlock.FACING)) continue;
                    Direction facing = state.getValue(IndustrialFixtureBlock.FACING);
                    if (!inBeam(p, crop, facing) || !unobstructed(level, p, crop, facing)) continue;
                    if (level.getBlockEntity(p) instanceof LampBaseBlockEntity fixture
                            && fixture.getBulb().is(ModItems.GROW_BULB.get())) count++;
                }
            }
        }
        return count;
    }
}
