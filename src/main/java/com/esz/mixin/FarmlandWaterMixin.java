package com.esz.mixin;

import net.minecraft.block.FarmlandBlock;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(FarmlandBlock.class)
public abstract class FarmlandWaterMixin {

    // Paper: 0703-Optimise-BlockSoil-nearby-water-lookup.patch
    private static boolean isWaterNearby(WorldView world, BlockPos pos) {
        int posX = pos.getX();
        int posY = pos.getY();
        int posZ = pos.getZ();

        for (int dz = -4; dz <= 4; ++dz) {
            int z = dz + posZ;
            for (int dx = -4; dx <= 4; ++dx) {
                int x = posX + dx;
                for (int dy = 0; dy <= 1; ++dy) {
                    int y = dy + posY;
                    Chunk chunk = world.getChunk(x >> 4, z >> 4);
                    FluidState fluid = chunk.getBlockState(new BlockPos(x, y, z)).getFluidState();

                    if (fluid.isIn(FluidTags.WATER)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
