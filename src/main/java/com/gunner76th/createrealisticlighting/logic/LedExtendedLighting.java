package com.gunner76th.createrealisticlighting.logic;

import com.gunner76th.createrealisticlighting.block.IndustrialFixtureBlock;
import com.gunner76th.createrealisticlighting.block.WallSconceBlock;
import com.gunner76th.createrealisticlighting.blockentity.LedLightNodeBlockEntity;
import com.gunner76th.createrealisticlighting.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import java.util.HashSet;
import java.util.Set;

/** Adaptive, surface-aware directional LED illumination. Server-side only. */
public final class LedExtendedLighting {
    private static final int[] RADII = {3, 6, 9, 12, 15, 18, 21, 24, 27, 30, 33, 36, 38};
    // Smaller radial steps and a monotonic falloff reduce visible lighting bands.
    private static final int[] LEVELS = {15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3};
    private static final int MIN_ANGULAR_SAMPLES = 16;
    private static final int MAX_SURFACE_DROP = 28;
    // Smooth stepped radial falloff; keep the outer sampling radius at 38 blocks.
    private LedExtendedLighting() {}

    public static Set<BlockPos> desired(Level level, BlockPos fixture, BlockState fixtureState) {
        Set<BlockPos> result = new HashSet<>();
        if (!fixtureState.hasProperty(IndustrialFixtureBlock.FACING)) return result;
        Direction facing = fixtureState.getValue(IndustrialFixtureBlock.FACING);
        boolean sconce = fixtureState.getBlock() instanceof WallSconceBlock;
        for (int i = 0; i < RADII.length; i++) {
            int radius = RADII[i];
            // Keep the spacing between light samples roughly constant as rings expand.
            int samples = Math.max(MIN_ANGULAR_SAMPLES, (int) Math.ceil(2.0 * Math.PI * radius / 3.5));
            for (int sample = 0; sample < samples; sample++) {
                double angle = (sample + (i % 2 == 0 ? 0.0 : 0.5)) * (2.0 * Math.PI / samples);
                int dx = (int)Math.round(Math.cos(angle) * radius);
                int dz = (int)Math.round(Math.sin(angle) * radius);
                BlockPos target;
                if (facing == Direction.DOWN) {
                    target = findVisibleSurfaceAir(level, fixture, dx, dz, facing);
                } else if (facing == Direction.UP) {
                    // Ground-mounted upward fixtures illuminate both the surrounding
                    // walkable surfaces and the space above them. Keep the existing
                    // upward beam, but also place a separate ground-level node.
                    BlockPos upward = fixture.offset(dx, Math.max(1, radius / 2), dz);
                    if (level.isInWorldBounds(upward) && level.hasChunkAt(upward)) {
                        BlockState upwardState = level.getBlockState(upward);
                        if ((upwardState.isAir() || upwardState.is(ModBlocks.LED_LIGHT_NODE.get()))
                                && clearRay(level, fixture, upward, facing)) {
                            result.add(upward.immutable());
                        }
                    }
                    target = findVisibleSurfaceAir(level, fixture, dx, dz, facing);
                } else {
                    // Forward cone: spread sideways and downward from the wall fixture.
                    int forward = radius;
                    int sideways = dx / 2;
                    int drop = Math.max(1, radius / 4);
                    target = fixture.relative(facing, forward).relative(facing.getClockWise(), sideways).below(drop);
                    // Keep the wall fixture beam in the forward hemisphere.
                    if (Math.sin(angle) < 0) continue;
                }
                if (target == null || !level.isInWorldBounds(target) || !level.hasChunkAt(target)) continue;
                BlockState at = level.getBlockState(target);
                if (!at.isAir() && !at.is(ModBlocks.LED_LIGHT_NODE.get())) continue;
                if (!clearRay(level, fixture, target, facing)) continue;
                result.add(target.immutable());
            }
        }
        return result;
    }

    /** Find a visible surface independently in each sampled column. */
    private static BlockPos findVisibleSurfaceAir(
            Level level, BlockPos fixture, int dx, int dz, Direction facing) {
        int x = fixture.getX() + dx;
        int z = fixture.getZ() + dz;
        int highestY = fixture.getY() - 1;
        int lowestY = highestY - MAX_SURFACE_DROP + 1;

        for (int y = highestY; y >= lowestY; y--) {
            BlockPos ground = new BlockPos(x, y, z);
            BlockPos above = ground.above();
            if (!level.isInWorldBounds(ground) || !level.isInWorldBounds(above)
                    || !level.hasChunkAt(ground) || !level.hasChunkAt(above)) return null;

            BlockState surface = level.getBlockState(ground);
            BlockState air = level.getBlockState(above);
            if (!surface.isCollisionShapeFullBlock(level, ground)) continue;
            if (!air.isAir() && !air.is(ModBlocks.LED_LIGHT_NODE.get())) continue;

            // A roof must not allow light nodes to appear on floors below it.
            return clearRay(level, fixture, above, facing) ? above : null;
        }
        return null;
    }

private static boolean clearRay(
        Level level,
        BlockPos fixture,
        BlockPos target,
        Direction facing
) {
    Vec3 from = Vec3.atCenterOf(fixture).add(
            facing.getStepX() * 0.65,
            facing.getStepY() * 0.65,
            facing.getStepZ() * 0.65
    );

    Vec3 to = Vec3.atCenterOf(target);

    // Use the block-only ray tracer. Unlike ClipContext with a
    // null entity, this does not construct an EntityCollisionContext.
    BlockHitResult hit = level.clip(
            new ClipContext(
                    from,
                    to,
                    ClipContext.Block.COLLIDER,
                    ClipContext.Fluid.NONE,
                    net.minecraft.world.phys.shapes.CollisionContext.empty()
            )
    );

    return hit.getType() == HitResult.Type.MISS
            || hit.getBlockPos().equals(target);
}



    public static int strength(BlockPos fixture, BlockPos node, BlockState state) {
        Direction facing = state.getValue(IndustrialFixtureBlock.FACING);
        double distance = facing.getAxis() == Direction.Axis.Y
            ? Math.hypot(node.getX() - fixture.getX(), node.getZ() - fixture.getZ())
            : Math.abs((node.getX() - fixture.getX()) * facing.getStepX() + (node.getZ() - fixture.getZ()) * facing.getStepZ());
        if (distance <= RADII[0]) return LEVELS[0];
        for (int i = 1; i < RADII.length; i++) {
            if (distance <= RADII[i]) {
                double fraction = (distance - RADII[i - 1]) / (RADII[i] - RADII[i - 1]);
                return Math.max(1, Math.min(15,
                    (int) Math.round(LEVELS[i - 1] + fraction * (LEVELS[i] - LEVELS[i - 1]))));
            }
        }
        return LEVELS[LEVELS.length - 1];
    }

    public static void remove(Level level, BlockPos fixture, Set<BlockPos> positions) {
        for (BlockPos p : positions) {
            if (!level.hasChunkAt(p)) continue;
            if (level.getBlockEntity(p) instanceof LedLightNodeBlockEntity node) node.removeOwner(fixture);
        }
    }

    public static void add(Level level, BlockPos fixture, BlockState fixtureState, Set<BlockPos> positions) {
        for (BlockPos p : positions) {
            if (!level.hasChunkAt(p)) continue;
            BlockState at = level.getBlockState(p);
            if (at.isAir()) level.setBlock(p, ModBlocks.LED_LIGHT_NODE.get().defaultBlockState(), 3);
            if (level.getBlockEntity(p) instanceof LedLightNodeBlockEntity node)
                node.addOwner(fixture, strength(fixture, p, fixtureState));
        }
    }
}
