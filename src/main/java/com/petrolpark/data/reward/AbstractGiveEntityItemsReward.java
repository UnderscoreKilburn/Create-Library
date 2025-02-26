package com.petrolpark.data.reward;

import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import com.petrolpark.data.IEntityTarget;
import com.petrolpark.util.ItemHelper;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;

public abstract class AbstractGiveEntityItemsReward extends ContextEntityReward {

    public AbstractGiveEntityItemsReward(IEntityTarget target) {
        super(target);
    };

    public abstract Stream<ItemStack> streamStacks(Entity recipient, LootContext context);

    @Override
    public final void reward(Entity entity, LootContext context, float multiplier) {
        ItemHelper.give(entity, streamStacks(entity, context).map(multiplyAmount(multiplier)));
    };

    private UnaryOperator<ItemStack> multiplyAmount(float multiplier) {
        return stack -> stack.copyWithCount(Math.min(stack.getMaxStackSize(), (int)(multiplier * (float)stack.getCount())));
    };
    
};
