/*
 * This file is part of Genesis Mod, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2017 Boethie
 * Copyright (c) 2017 contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package genesis.init;

import genesis.GenesisMod;
import genesis.combo.variant.EnumOre;
import genesis.item.ItemGenesis;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

@Mod.EventBusSubscriber(modid = GenesisMod.MOD_ID)
@GameRegistry.ObjectHolder(GenesisMod.MOD_ID)
public class GenesisItems {

    // ore drops
    public static final Item ZIRCON = null;
    public static final Item GARNET = null;
    public static final Item SIDERITE = null;
    public static final Item TOURMALINE = null;
    public static final Item AQUAMARINE = null;
    public static final Item PYRITE = null;
    public static final Item AZURITE = null;

    // other
    public static final Item RED_CLAY_BALL = null;

    private static final Set<Item> ITEMS = new LinkedHashSet<>();

    @SubscribeEvent
    public static void registerItems(final RegistryEvent.Register<Item> event) {
        final IForgeRegistry<Item> registry = event.getRegistry();

        ITEMS.clear();

        // ore drops
        for (final EnumOre oreType : EnumOre.values()) {
            final String oreName = oreType.toString().toLowerCase(Locale.ENGLISH);
            registerItem(registry, new ItemGenesis(), oreName);
        }

        // other
        registerItem(registry, new ItemGenesis(), "red_clay_ball");
    }

    private static void registerItem(final IForgeRegistry<Item> registry, final Item item, final String name) {
        item.setRegistryName(GenesisMod.MOD_ID, name);
        item.setUnlocalizedName(GenesisMod.MOD_ID + "." + name);
        registry.register(item);
        ITEMS.add(item);
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void registerModels(final ModelRegistryEvent event) {
        for (final Item item : ITEMS) {
            ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
        }
    }
}
