package com.esz;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;
import net.minecraft.world.chunk.WorldChunk;
import org.jetbrains.annotations.Nullable;

public interface IWorld extends WorldView {

    boolean isLoadedAndInBounds(BlockPos pos);

    WorldChunk getChunkIfLoaded(int x, int z);

    @Nullable WorldChunk getChunkIfLoaded(BlockPos pos);

    BlockState getBlockStateIfLoadedAndInBounds(BlockPos pos);

    BlockState getBlockStateIfLoaded(BlockPos blockPos);
}
