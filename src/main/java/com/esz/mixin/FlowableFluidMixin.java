package com.esz.mixin;

import com.esz.FastFluids;
import com.esz.IWorld;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Mixin;
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

    // Optimize search for source block neighbours
    private int countNeighboringSources(WorldView world, BlockPos pos) {
        int i = 0;
        int relX = pos.getX() & 0xF;
        int relZ = pos.getZ() & 0xF;
        if (relX > 0 && relX < 15 && relZ > 0 && relZ < 15) {
            Chunk chunk = ((IWorld) world).getChunkIfLoaded(pos);
            if (chunk == null) return i;
            for (Direction direction : Direction.Type.HORIZONTAL) {
                // No more than three source blocks are relevant in any scenario
                if (i >= 3) return i;
                BlockPos blockPos = pos.offset(direction);
                if (chunk.getBlockState(blockPos).getFluidState().isStill()) i++;
            }
        } else {
            for (Direction direction : Direction.Type.HORIZONTAL) {
                // No more than three source blocks are relevant in any scenario
                if (i >= 3) return i;
                BlockPos blockPos = pos.offset(direction);
                if (world.getBlockState(blockPos).getFluidState().isStill()) i++;
            }
        }
        return i;
    }
}
