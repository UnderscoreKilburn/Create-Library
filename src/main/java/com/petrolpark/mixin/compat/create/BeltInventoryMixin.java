package com.petrolpark.mixin.compat.create;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.petrolpark.compat.create.item.directional.DirectionalTransportedItemStack;
import com.petrolpark.compat.create.item.directional.IDirectionalOnBelt;
import com.simibubi.create.content.kinetics.belt.transport.BeltInventory;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;

@Mixin(BeltInventory.class)
public abstract class BeltInventoryMixin {
    
    @Inject(
        method = "Lcom/simibubi/create/content/kinetics/belt/transport/BeltInventory;insert(Lcom/simibubi/create/content/kinetics/belt/transport/TransportedItemStack;)V",
        at = @At("HEAD"),
        cancellable = true,
        remap = false
    )
    public void inInsert(TransportedItemStack stack, CallbackInfo ci) {
        if (!(stack instanceof DirectionalTransportedItemStack) && stack.stack.getItem() instanceof IDirectionalOnBelt directionalItem) {
            invokeInsert(directionalItem.makeDirectionalTransportedItemStack(stack));
            ci.cancel();
        };
    };

    @Invoker(
        value = "insert",
        remap = false
    )
    public abstract void invokeInsert(TransportedItemStack stack);
};
