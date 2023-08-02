package com.esz.mixin;

import com.esz.IWorld;
import net.minecraft.block.*;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.WorldView;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.light.ChunkLightProvider;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(SpreadableBlock.class)
public abstract class SpreadableBlockMixin extends SnowyBlock {

    // Paper: 0884-optimized-dirt-and-snow-spreading.patch

    public SpreadableBlockMixin(Settings settings) {
        super(settings);
    }

    private static boolean canSurvive(Chunk chunk, BlockState state, WorldView world, BlockPos pos) {
        BlockPos blockPos = pos.up();
        // Get block state from chunk
        BlockState blockState = chunk.getBlockState(blockPos);
        if (blockState.isOf(Blocks.SNOW) && blockState.get(SnowBlock.LAYERS) == 1) {
            return true;
        }
        if (blockState.getFluidState().getLevel() == 8) {
            return false;
        }
        int i = ChunkLightProvider.getRealisticOpacity(world, state, pos, blockState, blockPos, Direction.UP, blockState.getOpacity(world, blockPos));
        return i < world.getMaxLightLevel();
    }

    private static boolean canSpread(Chunk chunk, BlockState state, WorldView world, BlockPos pos) {
        BlockPos blockPos = pos.up();
        // Get fluid state from chunk
        return canSurvive(chunk, state, world, pos) && !chunk.getFluidState(blockPos).isIn(FluidTags.WATER);
    }

    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        // Get cached chunk if loaded
        Chunk chunk = ((IWorld) world).getChunkIfLoaded(pos);
        if (chunk == null) return;

        if (!canSurvive(chunk, state, world, pos)) {
            world.setBlockState(pos, Blocks.DIRT.getDefaultState());
            return;
        }
        if (world.getLightLevel(pos.up()) >= 9) {
            BlockState blockState = this.getDefaultState();
            for (int i = 0; i < 4; ++i) {
                BlockPos blockPos = pos.add(random.nextInt(3) - 1, random.nextInt(5) - 3, random.nextInt(3) - 1);

                if (pos.getX() == blockPos.getX() && pos.getY() == blockPos.getY() && pos.getZ() == blockPos.getZ())
                    continue;

                Chunk access;
                ChunkPos chunkPos = chunk.getPos();
                if (chunkPos.x == blockPos.getX() >> 4 && chunkPos.z == blockPos.getZ() >> 4) {
                    access = chunk;
                } else {
                    access = world.getChunk(blockPos);
                }

                if (!access.getBlockState(blockPos).isOf(Blocks.DIRT) || !canSpread(access, blockState, world, blockPos))
                    continue;
                world.setBlockState(blockPos, (BlockState) blockState.with(SNOWY, access.getBlockState(blockPos.up()).isOf(Blocks.SNOW)));
            }
        }
    }
}
