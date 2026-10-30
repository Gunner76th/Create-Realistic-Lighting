package com.gunner76th.createrealisticlighting.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

/** Downward exposed-bulb wall sconce, mounted to any horizontal wall. */
public class WallSconceBlock extends IndustrialFixtureBlock {
  public WallSconceBlock(Properties properties) {
    super(properties);
  }

  // Selection and collision are anchored to the round mounting plate, not the
  // outward-projecting arm. The base model faces SOUTH and has its plate at Z=0;
  // the blockstate's Y rotations move that plate to the other three wall faces.
  // A 10x10-pixel target covers the plate, with 3 pixels of wall-normal depth.
  private static final VoxelShape SOUTH = box(3, 3, 0, 13, 13, 3);
  private static final VoxelShape NORTH = box(3, 3, 13, 13, 13, 16);
  private static final VoxelShape WEST = box(13, 3, 3, 16, 13, 13);
  private static final VoxelShape EAST = box(0, 3, 3, 3, 13, 13);


  /** Two terminals on each lateral side of the mounting plate. */
  @Override
  public org.patryk3211.powergrid.electricity.base.ITerminalPlacement terminal(
      BlockState state, int index) {
    if (index < 0 || index >= 4) return null;

    // Model faces south: left pair and right pair, each with red above blue.
    // Hitboxes overlap the visible copper pads on the plate.
    double x1 = (index < 2) ? 2.5 : 10.5;
    double x2 = (index < 2) ? 5.5 : 13.5;
    double y1 = (index % 2 == 0) ? 9.0 : 4.5;
    double y2 = (index % 2 == 0) ? 12.0 : 7.5;
    double z1 = 0.0, z2 = 3.0;
    double a, b, c, d;
    switch (state.getValue(FACING)) {
      case WEST -> { a = 16.0 - z2; b = x1; c = 16.0 - z1; d = x2; }
      case NORTH -> { a = 16.0 - x2; b = 16.0 - z2; c = 16.0 - x1; d = 16.0 - z1; }
      case EAST -> { a = z1; b = 16.0 - x2; c = z2; d = 16.0 - x1; }
      default -> { a = x1; b = z1; c = x2; d = z2; }
    }
    int color = (index == 0 || index == 2)
        ? org.patryk3211.powergrid.electricity.base.IDecoratedTerminal.RED
        : org.patryk3211.powergrid.electricity.base.IDecoratedTerminal.BLUE;
    return new org.patryk3211.powergrid.electricity.base.TerminalBoundingBox(
        org.patryk3211.powergrid.electricity.base.IDecoratedTerminal.CONNECTOR,
        a, y1, b, c, y2, d).withColor(color);
  }

  @Override
  public VoxelShape getShape(
      BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
    return switch (state.getValue(FACING)) {
      case NORTH -> NORTH;
      case EAST -> EAST;
      case WEST -> WEST;
      default -> SOUTH;
    };
  }

  @Override
  public VoxelShape getCollisionShape(
      BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
    return getShape(state, level, pos, context);
  }

  @Nullable
  @Override
  public BlockState getStateForPlacement(BlockPlaceContext context) {
    Direction face = context.getClickedFace();
    return face.getAxis().isHorizontal() ? defaultBlockState().setValue(FACING, face) : null;
  }

  @Override
  public BlockState getRotatedBlockState(BlockState originalState, Direction targetedFace) {
    Direction current = originalState.getValue(FACING);

    Direction next =
        switch (current) {
          case NORTH -> Direction.EAST;
          case EAST -> Direction.SOUTH;
          case SOUTH -> Direction.WEST;
          case WEST -> Direction.NORTH;
          default -> Direction.SOUTH;
        };

    return originalState.setValue(FACING, next);
  }
}
