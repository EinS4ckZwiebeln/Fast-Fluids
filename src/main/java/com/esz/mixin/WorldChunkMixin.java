package com.esz.mixin;

import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.registry.Registry;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.UpgradeData;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.gen.chunk.BlendingData;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(WorldChunk.class)
public abstract class WorldChunkMixin extends Chunk {

    public WorldChunkMixin(ChunkPos pos, UpgradeData upgradeData, HeightLimitView heightLimitView, Registry<Biome> biomeRegistry, long inhabitedTime, @Nullable ChunkSection[] sectionArray, @Nullable BlendingData blendingData) {
        super(pos, upgradeData, heightLimitView, biomeRegistry, inhabitedTime, sectionArray, blendingData);
    }

    // Paper: 0356-Optimise-Chunk-getFluid.patch
    public FluidState getFluidState(int x, int y, int z) {
        int sectionIndex = this.getSectionIndex(y);
        if (sectionIndex >= 0 && sectionIndex < this.sectionArray.length) {
            ChunkSection chunkSection = this.sectionArray[sectionIndex];
            if (!chunkSection.isEmpty()) {
                return chunkSection.getFluidState(x & 15, y & 15, z & 15);
            }
        }
        return Fluids.EMPTY.getDefaultState();
    }
}
