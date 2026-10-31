
package com.gunner76th.createrealisticlighting.registry;

import com.gunner76th.createrealisticlighting.CreateRealisticLighting;
import com.gunner76th.createrealisticlighting.menu.LampMenu;

import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModMenus {

    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(
                    net.minecraft.core.registries.Registries.MENU,
                    CreateRealisticLighting.MOD_ID
            );

    public static final DeferredHolder<MenuType<?>, MenuType<LampMenu>> LAMP =
            MENUS.register(
                    "lamp",
                    () -> IMenuTypeExtension.create(LampMenu::new)
            );

    private ModMenus() {}
}
