package com.esz.mixin;

import com.esz.IWorld;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.chunk.WorldChunk;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(World.class)
public abstract class WorldMixin implements WorldAccess, IWorld {

    // Paper: 0071-Add-World-Util-Methods.patch

    public final boolean isLoadedAndInBounds(BlockPos pos) {
        return this.getWorldBorder().contains(pos) && this.getChunkIfLoaded(pos.getX() >> 4, pos.getZ() >> 4) != null;
    }

    public WorldChunk getChunkIfLoaded(int x, int z) {
        return this.getChunkManager().getWorldChunk(x, z);
    }

    @Nullable
    public final WorldChunk getChunkIfLoaded(BlockPos pos) {
        return this.getChunkManager().getWorldChunk(pos.getX() >> 4, pos.getZ() >> 4);
    }

    @Nullable
    public final BlockState getBlockStateIfLoadedAndInBounds(BlockPos pos) {
        return this.getWorldBorder().contains(pos) ? this.getBlockState(pos) : null;
    }
}
