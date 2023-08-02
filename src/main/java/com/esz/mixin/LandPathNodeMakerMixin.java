package com.esz.mixin;

import net.minecraft.block.*;
import net.minecraft.entity.ai.pathing.LandPathNodeMaker;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LandPathNodeMaker.class)
public abstract class LandPathNodeMakerMixin {

    // Paper: 0478-Reduce-blockpos-allocation-from-pathfinding.patch

    private static BlockState cachedBlockStateFromNeighbors;
    private static BlockState cachedBlockStateCommonNodeType;

    @Redirect(method = "getNodeTypeFromNeighbors", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/BlockView;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;"))
    private static BlockState redirectBlockStateFromNeighbors(BlockView world, BlockPos pos, BlockView world2, BlockPos.Mutable pos2, PathNodeType type) {
        BlockState blockState = world.getBlockState(pos);
        cachedBlockStateFromNeighbors = blockState;
        return blockState;
    }

    @Redirect(method = "getNodeTypeFromNeighbors", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/BlockView;getFluidState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/fluid/FluidState;"))
    private static FluidState redirectFluidStateFromNeighbors(BlockView world, BlockPos pos) {
        return cachedBlockStateFromNeighbors.getFluidState();
    }

    @Redirect(method = "getCommonNodeType", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/BlockView;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;"))
    private static BlockState redirectBlockStateCommonNodeType(BlockView world, BlockPos pos, BlockView world2, BlockPos pos2) {
        BlockState blockState = world.getBlockState(pos);
        cachedBlockStateCommonNodeType = blockState;
        return blockState;
    }

    @Redirect(method = "getCommonNodeType", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/BlockView;getFluidState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/fluid/FluidState;"))
    private static FluidState redirectFluidStateCommonNodeType(BlockView world, BlockPos pos) {
        return cachedBlockStateCommonNodeType.getFluidState();
    }
}
