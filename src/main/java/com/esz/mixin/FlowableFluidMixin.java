package com.esz.mixin;

import com.esz.FastFluids;
import net.minecraft.block.*;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(FlowableFluid.class)
public abstract class FlowableFluidMixin extends Fluid {

    private BlockState cachedBlockState;

    @Redirect(method = "flowToSides", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;"))
    public BlockState redirectBlockStateFlow(World world, BlockPos pos, World world2, BlockPos pos2, FluidState fluid, BlockState state) {
        BlockState blockState = world.getBlockState(pos);
        cachedBlockState = blockState;
        return blockState;
    }

    @Redirect(method = "flowToSides", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getFluidState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/fluid/FluidState;"))
    public FluidState redirectFluidStateFlow(World world, BlockPos pos) {
        return cachedBlockState.getFluidState();
    }

    // Helper function for countNeighboringSources method
    private boolean isSourceBlock(WorldView world, BlockPos blockPos) {
        BlockState blockState = world.getBlockState(blockPos);
        // Retrieve fluid state from block state
        if (blockState.getFluidState().isStill()) {
            return true;
        }
        return false;
    }

    // Optimize search for source block neighbours
    private int countNeighboringSources(WorldView world, BlockPos pos) {
        int i = 0;
        // Cache original block pos
        int posX = pos.getX();
        int posY = pos.getY();
        int posZ = pos.getZ();
        // Generic for loop is faster
        for (int x = -1; x <= 1; x += 2) {
            if (isSourceBlock(world, new BlockPos(posX + x, posY, posZ))) i++;
        }
        for (int z = -1; z <= 1; z += 2) {
            // No more than three source blocks are relevant in any scenario
            if (i >= 3) return i;
            if (isSourceBlock(world, new BlockPos(posX, posY, posZ + z))) i++;
        }
        return i;
    }
}
