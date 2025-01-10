package com.petrolpark.util;

import java.util.function.Predicate;

import com.petrolpark.itemdecay.IDecayingItem;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.items.IItemHandler;

//TODO remove Create dependence
public class ItemHelper extends com.simibubi.create.foundation.item.ItemHelper {

    public static boolean equalIgnoringTags(ItemStack stack1, ItemStack stack2, String ...ignoredTagKeys) {
        ItemStack trueStack1 = IDecayingItem.checkDecay(stack1);
        ItemStack trueStack2 = IDecayingItem.checkDecay(stack2);
        if (!trueStack1.is(trueStack2.getItem())) return false;
        if (trueStack1.isEmpty()) return trueStack2.isEmpty();
        return NBTHelper.equalIgnoring(trueStack1.getTag(), trueStack2.getTag(), ignoredTagKeys) && trueStack1.areCapsCompatible(trueStack2);
    };

    public static void pop(Level level, Vec3 position, ItemStack stack) {
        if (level.isClientSide()) return;
        ItemEntity entity = new ItemEntity(level, position.x, position.y, position.z, stack);
        entity.setDefaultPickUpDelay();
        level.addFreshEntity(entity);
    };
    
    /**
     * @param inv
     * @param test
     * @param maxCount
     * @return Actual number of actual Items removed
     */
    public static int removeItems(IItemHandler inv, Predicate<ItemStack> test, int maxCount) {
        int removed = 0;
        for (int slot = 0; slot < inv.getSlots(); slot++) {
            if (test.test(inv.getStackInSlot(slot))) removed += inv.extractItem(slot, maxCount - removed, false).getCount();
        };
        return removed;
    };
};
