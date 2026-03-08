package com.example.amethystlantern;

import com.example.amethystlantern.registry.ModCreativeTabs;
import com.example.amethystlantern.registry.ModItems;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(AmethystLanternMod.MOD_ID)
public class AmethystLanternMod {

    public static final String MOD_ID = "amethystlantern";
    public static final Logger LOGGER = LogManager.getLogger();

    public AmethystLanternMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register items
        ModItems.ITEMS.register(modEventBus);

        // Register creative tab
        ModCreativeTabs.CREATIVE_MODE_TABS.register(modEventBus);

        // Register ourselves for server and other game events
        MinecraftForge.EVENT_BUS.register(this);
    }
}
