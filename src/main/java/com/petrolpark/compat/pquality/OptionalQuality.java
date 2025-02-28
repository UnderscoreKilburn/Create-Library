package com.petrolpark.compat.pquality;

import com.petrolpark.compat.CompatMods;

import net.minecraft.world.item.ItemStack;

public class OptionalQuality {
  
    public static final int multiply(ItemStack stack, int base) {
        if (CompatMods.PQUALITY.isLoaded()) {
            return base; //TODO
        } else {
            return base;
        }
    };
};
