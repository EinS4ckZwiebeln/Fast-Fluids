package com.esz.mixin;

import net.minecraft.block.AbstractBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AbstractBlock.Settings.class)
public abstract class AbstractBlockSettingsMixin {

    // Paper: 0911-Improve-inlining-for-some-hot-BlockBehavior-and-Flui.patch
    @Shadow
    private boolean isAir;
    @Shadow
    private boolean burnable;

    public final boolean isAir() {
        return this.isAir;
    }

    public final boolean isBurnable() {
        return this.burnable;
    }
}
