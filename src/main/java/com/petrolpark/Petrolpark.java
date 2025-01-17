package com.petrolpark;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;
import com.petrolpark.badge.Badges;
import com.petrolpark.compat.CompatMods;
import com.petrolpark.compat.create.Create;
import com.petrolpark.compat.curios.Curios;
import com.petrolpark.compat.jei.category.ITickableCategory;
import com.petrolpark.itemdecay.DecayingItemHandler;
import com.petrolpark.network.PetrolparkMessages;
import com.petrolpark.recipe.IPetrolparkRecipeTypes;
import com.petrolpark.registrate.PetrolparkRegistrate;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Petrolpark.MOD_ID)
public class Petrolpark {

    public static final String MOD_ID = "petrolpark";

    public static final Logger LOGGER = LogUtils.getLogger();

    public static final PetrolparkRegistrate REGISTRATE = new PetrolparkRegistrate(MOD_ID);
    public static final PetrolparkRegistrate DESTROY_REGISTRATE = CompatMods.DESTROY.registrate();

    public static ResourceLocation asResource(String path) {
        return new ResourceLocation(MOD_ID, path);
    };

    public static final ThreadLocal<DecayingItemHandler> DECAYING_ITEM_HANDLER = ThreadLocal.withInitial(() -> DecayingItemHandler.DUMMY);

    static {
        PetrolparkItemDisplayContexts.register();
    };

    public Petrolpark() {
        //ModLoadingContext modLoadingContext = ModLoadingContext.get();
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;

        REGISTRATE.registerEventListeners(modEventBus);
        DESTROY_REGISTRATE.registerEventListeners(modEventBus);

        // Registration
        PetrolparkRegistries.register();
        Badges.register();
        IPetrolparkRecipeTypes.register(modEventBus);

        // Client
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> PetrolparkClient.clientCtor(modEventBus, forgeEventBus));

        // Register ourselves for server and other game events we are interested in
        forgeEventBus.register(this);
    
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::init);

        // Compat
        if (CompatMods.JEI.isLoading()) forgeEventBus.register(ITickableCategory.ClientEvents.class);
        CompatMods.CREATE.executeIfInstalled(() -> () -> Create.ctor(modEventBus, forgeEventBus));
        CompatMods.CURIOS.executeIfInstalled(() -> () -> Curios.ctor(modEventBus, forgeEventBus));
    };

    private void init(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            PetrolparkMessages.register();
        });
    };

};
