package com.petrolpark.mixin.compat.create;

import java.util.List;

import org.checkerframework.common.aliasing.qual.Unique;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.petrolpark.PetrolparkConfig;
import com.petrolpark.contamination.IContamination;
import com.petrolpark.contamination.ItemContamination;
import com.petrolpark.item.decay.IDecayingItem;
import com.simibubi.create.content.kinetics.saw.SawBlockEntity;
import com.simibubi.create.content.processing.recipe.ProcessingInventory;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;

@Mixin(SawBlockEntity.class)
public class SawBlockEntityMixin {

    @Shadow
    ProcessingInventory inventory;
    
    @Unique
    ItemStack lastItemProcessed;

    @Inject(
        method = "Lcom/simibubi/create/content/kinetics/saw/SawBlockEntity;applyRecipe()V",
        at = @At("HEAD"),
        remap = false
    )
    public void inApplyRecipeStart(CallbackInfo ci) {
        lastItemProcessed = inventory.getStackInSlot(0);
    };

    @Inject(
        method = "Lcom/simibubi/create/content/kinetics/saw/SawBlockEntity;applyRecipe()V",
        at = @At("RETURN"),
        locals = LocalCapture.CAPTURE_FAILSOFT,
        remap = false
    )
    public void inApplyRecipeEnd(CallbackInfo ci, List<? extends Recipe<?>> recipes) {
        if (recipes.isEmpty()) return;
        IContamination<?, ?> inputContamination = ItemContamination.get(lastItemProcessed);
        for (int slot = 0; slot < inventory.getSlots(); slot++) {
            ItemStack stack = inventory.getStackInSlot(slot);
            IDecayingItem.startDecay(stack);
            if (PetrolparkConfig.SERVER.createCuttingRecipesPropagateContaminants.get()) ItemContamination.get(stack).contaminateAll(inputContamination.streamAllContaminants());
        };
    };
};
