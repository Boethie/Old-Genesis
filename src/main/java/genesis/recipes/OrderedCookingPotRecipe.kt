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

package genesis.recipes

import com.google.common.collect.Maps
import com.google.common.collect.Sets
import com.google.gson.JsonObject
import com.google.gson.JsonSyntaxException
import genesis.util.shrinkAll
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.Ingredient
import net.minecraft.util.JsonUtils
import net.minecraft.util.NonNullList
import net.minecraft.world.World
import net.minecraftforge.common.crafting.CraftingHelper
import net.minecraftforge.common.crafting.JsonContext
import net.minecraftforge.registries.IForgeRegistryEntry

class OrderedCookingPotRecipe(private val inputs: NonNullList<Ingredient> = NonNullList.create(),
                              private val width: Int = 1,
                              private val consumePot: Boolean = false,
                              override val output: ItemStack = ItemStack.EMPTY)
    : ICookingPotRecipe, IForgeRegistryEntry.Impl<ICookingPotRecipe>() {

    override fun matches(pot: CookingPotInventory, world: World): Boolean {
        return (0..(3 - width)).any { checkIndex(pot, it) }
    }

    private fun checkIndex(pot: CookingPotInventory, i: Int): Boolean {
        for (x in 0 until 3) {
            val subX = x - i

            val target = if (subX in 0 until width) inputs[subX]
            else Ingredient.EMPTY

            if (!target.apply(getSlotChecked(pot, x))) return false
        }

        return true
    }

    private fun getSlotChecked(pot: CookingPotInventory, slot: Int): ItemStack = if (slot < 0 || slot > 2) ItemStack.EMPTY else pot.ingredients[slot]

    override fun getCookingResult(pot: CookingPotInventory, world: World): ItemStack = output.copy()

    override fun consumeIngredients(pot: CookingPotInventory, world: World) {
        pot.ingredients.shrinkAll(1)
        pot.cookingPotItem.shrink(1)
    }

    companion object {
        @JvmStatic fun factory(context: JsonContext, json: JsonObject): OrderedCookingPotRecipe {
            //val group = JsonUtils.getString(json, "group", "")
            //if (!group.isEmpty() && group.indexOf(':') == -1)
            //    group = context.getModId() + ":" + group;

            val ingMap = Maps.newHashMap<Char, Ingredient>()

            for ((key, value) in JsonUtils.getJsonObject(json, "key").entrySet()) {
                if (key.length != 1) throw JsonSyntaxException("Invalid key entry: '$key' is an invalid symbol (must be 1 character only).")
                if (" " == key) throw JsonSyntaxException("Invalid key entry: ' ' is a reserved symbol.")

                ingMap[key.toCharArray()[0]] = CraftingHelper.getIngredient(value, context)
            }

            ingMap[' '] = Ingredient.EMPTY

            val consumePot = JsonUtils.getBoolean(json, "consume_pot", true) //heh
            val pattern = JsonUtils.getString(json, "pattern")

            val width = pattern.length

            if (width > 3) throw JsonSyntaxException("Cooking recipe pattern is too long. Has '$width' elements when max is 3")

            val input = NonNullList.withSize(width, Ingredient.EMPTY)

            val keys = Sets.newHashSet(ingMap.keys)
            keys.remove(' ')

            for ((i, chr) in pattern.withIndex()) {
                val ing = ingMap[chr] ?: throw JsonSyntaxException("Pattern references symbol '$chr' but it's not defined in the key")
                input[i] = ing
                keys.remove(chr)
            }

            if (!keys.isEmpty()) throw JsonSyntaxException("Key defines symbols that aren't used in pattern: " + keys)

            val result = CraftingHelper.getItemStack(JsonUtils.getJsonObject(json, "result"), context)
            return OrderedCookingPotRecipe(input, width, consumePot, result)
        }
    }
}