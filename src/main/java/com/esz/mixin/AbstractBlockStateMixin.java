package com.esz.mixin;

import net.minecraft.block.AbstractBlock;
import net.minecraft.fluid.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AbstractBlock.AbstractBlockState.class)
public abstract class AbstractBlockStateMixin {

    // Paper: 0911-Improve-inlining-for-some-hot-BlockBehavior-and-Flui.patch

    @Shadow
    private FluidState fluidState;

    public final FluidState getFluidState() {
        return this.fluidState;
    }
}
