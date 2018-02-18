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
package genesis.init

import genesis.GenesisMod
import genesis.combo.variant.CropInfo
import genesis.combo.variant.EnumOre
import genesis.item.ItemCeramicBowl
import genesis.item.ItemGenesis
import genesis.item.ItemGenesisSeeds
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.init.Items
import net.minecraft.item.Item
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.event.ModelRegistryEvent
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.minecraftforge.registries.IForgeRegistry
import java.util.*

@Mod.EventBusSubscriber(modid = GenesisMod.MOD_ID)
object GenesisItems {

    // ore drops
    val ZIRCON: Item by name("zircon")
    val GARNET: Item by name("garnet")
    val SIDERITE: Item by name("siderite")
    val TOURMALINE: Item by name("tourmaline")
    val AQUAMARINE: Item by name("aquamarine")
    val PYRITE: Item by name("pyrite")
    val AZURITE: Item by name("azurite")
    val ZINGIBEROPSIS_RHIZOME: Item by name("zingiberopsis_rhizome")

    // other
    val RED_CLAY_BALL: Item by name("red_clay_ball")

    val CERAMIC_BOWL: Item by name("ceramic_bowl")
    val CERAMIC_BOWL_WATER: Item by name("ceramic_bowl_water")

    private val ITEMS = LinkedHashSet<Item>()

    @SubscribeEvent
    @JvmStatic fun registerItems(event: RegistryEvent.Register<Item>) {
        val registry = event.registry

        ITEMS.clear()

        // ore drops
        for (oreType in EnumOre.values()) {
            val oreName = oreType.toString().toLowerCase(Locale.ENGLISH)
            registerItem(registry, ItemGenesis(), oreName)
        }

        // other
        registerItem(registry, ItemGenesis(), "red_clay_ball")
        registerItem(registry, ItemGenesisSeeds(CropInfo.ZINGIBEROPSIS, 2, 1.4f), "zingiberopsis_rhizome")
        registerItem(registry, ItemCeramicBowl(), "ceramic_bowl")
        registerItem(registry, ItemCeramicBowl(), "ceramic_bowl_water")
    }

    private fun registerItem(registry: IForgeRegistry<Item>, item: Item, name: String) {
        item.setRegistryName(GenesisMod.MOD_ID, name)
        item.unlocalizedName = GenesisMod.MOD_ID + "." + name
        registry.register(item)
        ITEMS.add(item)
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    @JvmStatic fun registerModels(event: ModelRegistryEvent) {
        for (item in ITEMS) {
            ModelLoader.setCustomModelResourceLocation(item, 0, ModelResourceLocation(item.registryName!!, "inventory"))
        }
    }

    private fun name(name: String) : Lazy<Item> = lazy { Item.REGISTRY.getObject(ResourceLocation(GenesisMod.MOD_ID, name)) ?: Items.AIR }

}
