package com.example.amethystlantern.registry;

import com.example.amethystlantern.AmethystLanternMod;
import com.example.amethystlantern.item.AmethystLanternItem;
import com.example.amethystlantern.item.BizarreBrainItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, AmethystLanternMod.MOD_ID);

    public static final RegistryObject<Item> AMETHYST_LANTERN =
            ITEMS.register("amethyst_lantern",
                    () -> new AmethystLanternItem(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> BIZARRE_BRAIN =
            ITEMS.register("bizarre_brain",
                    () -> new BizarreBrainItem(
                            new Item.Properties()
                                .stacksTo(1)
                                .rarity(Rarity.EPIC)   // Tên hiển thị màu tím đậm
                    ));
}
