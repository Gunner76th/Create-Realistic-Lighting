package com.gunner76th.createrealisticlighting.logic;

import com.gunner76th.createrealisticlighting.block.IndustrialFixtureBlock;
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

/** Fixture-centered, obstruction-aware LED lighting with surface-biased samples. */
public final class LedExtendedLighting {
    private static final int MAX_RADIUS = 16;
    private static final int SURFACE_STEP = 2;
    private static final int VOLUME_STEP = 4;
    private static final int MAX_SURFACE_DROP = 16;

    private LedExtendedLighting() {}

    public static Set<BlockPos> desired(Level level, BlockPos fixture, BlockState fixtureState) {
        Set<BlockPos> result = new HashSet<>();
        if (!fixtureState.hasProperty(IndustrialFixtureBlock.FACING)) return result;
        Direction facing = fixtureState.getValue(IndustrialFixtureBlock.FACING);

        // Surface-aware samples: a fine, origin-centered lattice above exposed floors.
        // Reject any point outside the spherical range or hidden behind an obstruction.
        for (int dx = -MAX_RADIUS; dx <= MAX_RADIUS; dx += SURFACE_STEP) {
            for (int dz = -MAX_RADIUS; dz <= MAX_RADIUS; dz += SURFACE_STEP) {
                if (dx * dx + dz * dz > MAX_RADIUS * MAX_RADIUS) continue;
                BlockPos surfaceAir = findVisibleSurfaceAir(level, fixture, dx, dz, facing);
                if (surfaceAir != null && withinRadius(fixture, surfaceAir)) {
                    result.add(surfaceAir.immutable());
                }
            }
        }

        // Coarser volume samples illuminate the air above and around the fixture.
        // These use the same origin for every orientation; no directional cone.
        for (int dx = -MAX_RADIUS; dx <= MAX_RADIUS; dx += VOLUME_STEP) {
            for (int dy = -MAX_RADIUS; dy <= MAX_RADIUS; dy += VOLUME_STEP) {
                for (int dz = -MAX_RADIUS; dz <= MAX_RADIUS; dz += VOLUME_STEP) {
                    BlockPos target = fixture.offset(dx, dy, dz);
                    if (target.equals(fixture) || !withinRadius(fixture, target)) continue;
                    if (canPlaceNode(level, target) && clearRay(level, fixture, target, facing)) {
                        result.add(target.immutable());
                    }
                }
            }
        }
        return result;
    }

    private static BlockPos findVisibleSurfaceAir(Level level, BlockPos fixture,
                                                   int dx, int dz, Direction facing) {
        int x = fixture.getX() + dx;
        int z = fixture.getZ() + dz;
        // Check the fixture's own elevation first, then descend to find a floor.
        for (int y = fixture.getY() - 1; y >= fixture.getY() - MAX_SURFACE_DROP; y--) {
            BlockPos ground = new BlockPos(x, y, z);
            BlockPos above = ground.above();
            if (!level.isInWorldBounds(ground) || !level.isInWorldBounds(above)
                    || !level.hasChunkAt(ground) || !level.hasChunkAt(above)) return null;
            if (!level.getBlockState(ground).isCollisionShapeFullBlock(level, ground)) continue;
            if (!canPlaceNode(level, above)) continue;
            // Do not search through a roof for a lower floor when the first viable
            // surface is blocked from the bulb.
            return clearRay(level, fixture, above, facing) ? above : null;
        }
        return null;
    }

    private static boolean withinRadius(BlockPos fixture, BlockPos pos) {
        return pos.distSqr(fixture) <= (long) MAX_RADIUS * MAX_RADIUS;
    }

    private static boolean canPlaceNode(Level level, BlockPos pos) {
        if (!level.isInWorldBounds(pos) || !level.hasChunkAt(pos)) return false;
        BlockState state = level.getBlockState(pos);
        return state.isAir() || state.is(ModBlocks.LED_LIGHT_NODE.get());
    }

    private static boolean clearRay(Level level, BlockPos fixture,
                                    BlockPos target, Direction facing) {
        Vec3 from = Vec3.atCenterOf(fixture).add(
                facing.getStepX() * 0.65,
                facing.getStepY() * 0.65,
                facing.getStepZ() * 0.65);
        Vec3 to = Vec3.atCenterOf(target);
        BlockHitResult hit = level.clip(new ClipContext(from, to,
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                net.minecraft.world.phys.shapes.CollisionContext.empty()));
        return hit.getType() == HitResult.Type.MISS || hit.getBlockPos().equals(target);
    }

    /** Radial 15-to-1 falloff over the configured 16-block sampling radius. */
    public static int strength(BlockPos fixture, BlockPos node, BlockState state) {
        double distance = Math.sqrt(node.distSqr(fixture));
        return Math.max(1, Math.min(15,
                15 - (int) Math.round(14.0 * Math.min(distance, MAX_RADIUS) / MAX_RADIUS)));
    }

    /**
     * Conservative shadow-boundary experiment: sample visibility around each node.
     * A node on the edge of an occluded region can otherwise cast vanilla block
     * light several blocks into that region. Only attenuate when multiple nearby
     * horizontal sample points are hidden from the fixture.
     */
    private static int shadowAdjustedStrength(Level level, BlockPos fixture,
                                              BlockPos node, BlockState fixtureState) {
        int base = strength(fixture, node, fixtureState);
        if (base <= 2 || !fixtureState.hasProperty(IndustrialFixtureBlock.FACING)) return base;
        Direction facing = fixtureState.getValue(IndustrialFixtureBlock.FACING);
        int occluded = 0;
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            BlockPos sample = node.relative(direction, 2);
            if (!level.isInWorldBounds(sample) || !level.hasChunkAt(sample)) continue;
            if (!clearRay(level, fixture, sample, facing)) occluded++;
        }
        // Do not penalize a node for a single blocked probe (e.g. one pillar).
        // Keep the adjustment small to protect the smooth illuminated side.
        int reduction = occluded >= 3 ? 2 : occluded == 2 ? 1 : 0;
        return Math.max(1, base - reduction);
    }

    public static void remove(Level level, BlockPos fixture, Set<BlockPos> positions) {
        for (BlockPos p : positions) {
            if (!level.hasChunkAt(p)) continue;
            if (level.getBlockEntity(p) instanceof LedLightNodeBlockEntity node)
                node.removeOwner(fixture);
        }
    }

    public static void add(Level level, BlockPos fixture, BlockState fixtureState,
                           Set<BlockPos> positions) {
        for (BlockPos p : positions) {
            if (!level.hasChunkAt(p)) continue;
            BlockState at = level.getBlockState(p);
            if (at.isAir()) level.setBlock(p, ModBlocks.LED_LIGHT_NODE.get().defaultBlockState(), 3);
            if (level.getBlockEntity(p) instanceof LedLightNodeBlockEntity node)
                node.addOwner(fixture, shadowAdjustedStrength(level, fixture, p, fixtureState));
        }
    }
}
