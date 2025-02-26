package com.petrolpark.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.petrolpark.PetrolparkConfig;
import com.petrolpark.contamination.ItemContamination;
import com.petrolpark.item.decay.IDecayingItem;
import com.petrolpark.recipe.contamination.IHandleContaminationMyself;

import net.minecraft.core.RegistryAccess;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.ShapedRecipe;

@Mixin(ShapedRecipe.class)
public abstract class ShapedRecipeMixin implements IHandleContaminationMyself<CraftingContainer> {

    @Inject(
        method = "Lnet/minecraft/world/item/crafting/ShapedRecipe;assemble(Lnet/minecraft/world/inventory/CraftingContainer;Lnet/minecraft/core/RegistryAccess;)Lnet/minecraft/world/item/ItemStack;",
        at = @At("RETURN"),
        cancellable = true
    )
    public void inAssemble(CraftingContainer container, RegistryAccess access, CallbackInfoReturnable<ItemStack> cir) {
        IDecayingItem.startDecay(cir.getReturnValue());
        if (PetrolparkConfig.SERVER.shapedCraftingPropagatesContaminants.get()) ItemContamination.perpetuateSingle(container.getItems().stream(), cir.getReturnValue());
    };

    @Override
    public boolean contaminationHandled(CraftingContainer container, RegistryAccess registryAccess) {
        return PetrolparkConfig.SERVER.shapedCraftingPropagatesContaminants.get();
    };
    

};
