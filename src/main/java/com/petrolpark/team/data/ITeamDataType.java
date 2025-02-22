package com.petrolpark.team.data;

import javax.annotation.Nonnull;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;

public interface ITeamDataType<T> {

    @Nonnull
    public T getBlankInstance();

    public boolean isBlank(T data);

    public T load(Level level, CompoundTag tag);

    public CompoundTag save(Level level, T data);
    
};
