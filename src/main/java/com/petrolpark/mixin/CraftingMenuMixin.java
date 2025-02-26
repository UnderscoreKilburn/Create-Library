package com.petrolpark.mixin;

import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.petrolpark.PetrolparkConfig;
import com.petrolpark.contamination.ItemContamination;
import com.petrolpark.item.decay.IDecayingItem;
import com.petrolpark.recipe.contamination.IHandleContaminationMyself;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.level.Level;

@Mixin(CraftingMenu.class)
public class CraftingMenuMixin {
    
    @Inject(
        method = "Lnet/minecraft/world/inventory/CraftingMenu;slotChangedCraftingGrid(Lnet/minecraft/world/inventory/AbstractContainerMenu;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/inventory/CraftingContainer;Lnet/minecraft/world/inventory/ResultContainer;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/inventory/ResultContainer;setItem(ILnet/minecraft/world/item/ItemStack;)V"
        ),
        locals = LocalCapture.CAPTURE_FAILSOFT
    )
    @SuppressWarnings("unchecked")
    private static void inSlotChangedCraftingGrid(AbstractContainerMenu menu, Level level, Player player, CraftingContainer container, ResultContainer result, CallbackInfo ci, ServerPlayer serverplayer, ItemStack itemstack, Optional<CraftingRecipe> optional) {
        if (!itemstack.isEmpty()) {
            IDecayingItem.startDecay(itemstack);
            if (PetrolparkConfig.SERVER.craftingTablePropagatesContaminants.get() && optional.map(recipe -> {
                if (recipe instanceof IHandleContaminationMyself contamHandled) {
                    return !contamHandled.contaminationHandled(container, level.registryAccess());
                } else return true;
            }).orElse(true)) {
                ItemContamination.perpetuateSingle(container.getItems().stream(), itemstack);
            };
        };
    };
};
