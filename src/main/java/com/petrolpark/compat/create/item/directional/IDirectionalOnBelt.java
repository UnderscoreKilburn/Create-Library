package com.petrolpark.compat.create.item.directional;

import javax.annotation.Nullable;

import com.petrolpark.RequiresCreate;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;

import net.minecraft.core.Direction;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Rotation;

@RequiresCreate
public interface IDirectionalOnBelt {

    /**
     * When Items are first put on Depots, Belts, etc. they are not directional.
     * This function should take the (non-directional) TransportedItemStack generated and turn it into a directional one.
     * That DirectionalTransportedItemStack can optionally override {@link }
     * @param transportedItemStack
     */
    public default DirectionalTransportedItemStack makeDirectionalTransportedItemStack(TransportedItemStack transportedItemStack) {
        return DirectionalTransportedItemStack.copyFully(transportedItemStack);
    };

    /**
     * Get the rotation an Item Stack should have when placed on a Belt, Depot, etc.
     * @param stack This may be mutated in this method
     * @return A rotation from north. May be null, in which case it will be ignored
     */
    @Nullable
    public default Rotation rotationForPlacement(ItemStack stack) {
        if (stack.getOrCreateTag().contains("RotationWhileFlying", Tag.TAG_INT)) {
            Rotation rotation = Rotation.values()[stack.getOrCreateTag().getInt("RotationWhileFlying")];
            stack.removeTagKey("RotationWhileFlying");
            return rotation;
        };
        return Rotation.NONE;
    };

    /**
     * This function is enacted on Items just before they are thrown by a Weighted Ejector.
     * @param stack
     * @param launchDirection
     */
    public default void launch(DirectionalTransportedItemStack stack, Direction launchDirection) {
        stack.stack.getOrCreateTag().putInt("RotationWhileFlying", stack.getRotation().ordinal());
    };
};
