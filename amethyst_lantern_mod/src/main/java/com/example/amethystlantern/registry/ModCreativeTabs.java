package com.example.amethystlantern.registry;

import com.example.amethystlantern.AmethystLanternMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeTabs {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, AmethystLanternMod.MOD_ID);

    public static final RegistryObject<CreativeModeTab> AMETHYST_LANTERN_TAB =
            CREATIVE_MODE_TABS.register("amethyst_lantern_tab", () ->
                    CreativeModeTab.builder()
                            .title(Component.translatable("itemGroup.amethystlantern"))
                            .icon(() -> new ItemStack(ModItems.AMETHYST_LANTERN.get()))
                            .displayItems((parameters, output) -> {
                                output.accept(ModItems.AMETHYST_LANTERN.get());
                                output.accept(ModItems.BIZARRE_BRAIN.get());
                            })
                            .build()
            );
}
