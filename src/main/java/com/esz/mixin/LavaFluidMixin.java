package com.esz.mixin;

import com.esz.IWorld;
import net.minecraft.block.*;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.LavaFluid;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(LavaFluid.class)
public abstract class LavaFluidMixin extends FlowableFluid {

    // Optimize if block pos is viable for burning
    private boolean hasBurnableBlock(WorldView world, BlockPos pos) {
        BlockState blockState = ((IWorld) world).getBlockStateIfLoadedAndInBounds(pos);
        if (blockState == null) return false;
        return blockState.isBurnable();
    }

    // Optimize search for burnable block neighbours
    private boolean canLightFire(WorldView world, BlockPos pos) {
        // Cache original block pos
        int posX = pos.getX();
        int posY = pos.getY();
        int posZ = pos.getZ();
        // Generic for loop is faster
        for (int x = -1; x <= 1; x += 2) {
            if (!this.hasBurnableBlock(world, new BlockPos(posX + x, posY, posZ))) continue;
            return true;
        }
        for (int z = -1; z <= 1; z += 2) {
            if (!this.hasBurnableBlock(world, new BlockPos(posX, posY, posZ + z))) continue;
            return true;
        }
        // Check top block first, seems more likely on average
        for (int y = 1; y >= -1; y -= 2) {
            if (!this.hasBurnableBlock(world, new BlockPos(posX, posY + y, posZ))) continue;
            return true;
        }
        return false;
    }
}
