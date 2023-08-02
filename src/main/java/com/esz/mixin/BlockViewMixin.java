package com.esz.mixin;

import com.esz.IWorld;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(BlockView.class)
public interface BlockViewMixin {

    @Shadow
    BlockHitResult raycastBlock(Vec3d start, Vec3d end, BlockPos pos, VoxelShape shape, BlockState state);

    // Abstract method error if shared to client
    @Environment(EnvType.SERVER)
    default BlockHitResult raycast(RaycastContext context) {
        return BlockView.raycast(context.getStart(), context.getEnd(), context, (innerContext, pos) -> {
            // Paper: 0282-Prevent-rayTrace-from-loading-chunks.patch
            // Prevent ray casting from loading chunks
            BlockState blockState = ((IWorld) this).getBlockStateIfLoaded(pos);
            if (blockState == null) {
                Vec3d vec3 = context.getStart().subtract(context.getEnd());
                return BlockHitResult.createMissed(context.getEnd(), Direction.getFacing(vec3.x, vec3.y, vec3.z), BlockPos.ofFloored(context.getEnd()));
            }
            // Paper: 0698-Don-t-lookup-fluid-state-when-raytracing.patch
            // Look up fluid state from the block state instead of from the world
            FluidState fluidState = blockState.getFluidState();
            Vec3d vec3d = innerContext.getStart();
            Vec3d vec3d2 = innerContext.getEnd();
            VoxelShape voxelShape = innerContext.getBlockShape(blockState, (BlockView) this, (BlockPos) pos);
            BlockHitResult blockHitResult = this.raycastBlock(vec3d, vec3d2, (BlockPos) pos, voxelShape, blockState);
            VoxelShape voxelShape2 = innerContext.getFluidShape(fluidState, (BlockView) this, (BlockPos) pos);
            BlockHitResult blockHitResult2 = voxelShape2.raycast(vec3d, vec3d2, (BlockPos) pos);
            double d = blockHitResult == null ? Double.MAX_VALUE : innerContext.getStart().squaredDistanceTo(blockHitResult.getPos());
            double e = blockHitResult2 == null ? Double.MAX_VALUE : innerContext.getStart().squaredDistanceTo(blockHitResult2.getPos());
            return d <= e ? blockHitResult : blockHitResult2;
        }, innerContext -> {
            Vec3d vec3d = innerContext.getStart().subtract(innerContext.getEnd());
            return BlockHitResult.createMissed(innerContext.getEnd(), Direction.getFacing(vec3d.x, vec3d.y, vec3d.z), BlockPos.ofFloored(innerContext.getEnd()));
        });
    }
}
