package com.petrolpark.data.reward;

import com.mojang.serialization.Codec;
import com.petrolpark.PetrolparkRegistries;
import com.petrolpark.data.ForgeRegistryObjectGSONAdapter;
import com.petrolpark.data.loot.PetrolparkGson;
import com.petrolpark.network.GsonSerializableCodecs;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootContextUser;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IReward extends LootContextUser {

    public static final Codec<IReward> CODEC = GsonSerializableCodecs.GSONserializableCodec("reward", IReward.class, PetrolparkGson.get());

    public void reward(LootContext context, float multiplier);

    @OnlyIn(Dist.CLIENT)
    public void render(GuiGraphics graphics);

    @OnlyIn(Dist.CLIENT)
    public Component getName();
    
    public RewardType getType();

    public static ForgeRegistryObjectGSONAdapter<IReward, RewardType> createGsonAdapter() {
        return ForgeRegistryObjectGSONAdapter.builder(PetrolparkRegistries.Keys.REWARD_TYPE, "fixed_payment", "type", IReward::getType).build();
    };
};
