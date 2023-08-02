package com.esz.mixin;

import com.esz.FastFluids;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.ArrayList;

@Mixin(SpongeBlock.class)
public abstract class SpongeBlockMixin {

    // NOTE: Refactor this using @Redirect
    // As of now I have no idea how to redirect a method call that is encapsulated in a lambda function

    @Shadow
    private static Direction[] field_43257;

    private boolean absorbWater(World world, BlockPos pos) {
        return BlockPos.iterateRecursively(pos, 6, 65, (currentPos, queuer) -> {
            for (Direction direction : field_43257) {
                queuer.accept(currentPos.offset(direction));
            }
        }, currentPos -> {
            FluidDrainable fluidDrainable;
            if (currentPos.equals(pos)) {
                return true;
            }
            BlockState blockState = world.getBlockState((BlockPos) currentPos);
            // Look up fluid state from the block state instead of from the world
            FluidState fluidState = blockState.getFluidState();
            if (!fluidState.isIn(FluidTags.WATER)) {
                return false;
            }
            Block block = blockState.getBlock();
            if (block instanceof FluidDrainable && !(fluidDrainable = (FluidDrainable) ((Object) block)).tryDrainFluid(world, (BlockPos) currentPos, blockState).isEmpty()) {
                return true;
            }
            if (blockState.getBlock() instanceof FluidBlock) {
                world.setBlockState((BlockPos) currentPos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
            } else if (blockState.isOf(Blocks.KELP) || blockState.isOf(Blocks.KELP_PLANT) || blockState.isOf(Blocks.SEAGRASS) || blockState.isOf(Blocks.TALL_SEAGRASS)) {
                BlockEntity blockEntity = blockState.hasBlockEntity() ? world.getBlockEntity((BlockPos) currentPos) : null;
                SpongeBlock.dropStacks(blockState, world, currentPos, blockEntity);
                world.setBlockState((BlockPos) currentPos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
            } else {
                return false;
            }
            return true;
        }) > 1;
    }
}
