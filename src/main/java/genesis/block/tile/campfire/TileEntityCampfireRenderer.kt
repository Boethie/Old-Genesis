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

package genesis.block.tile.campfire

import genesis.block.BlockCampfire
import genesis.util.render.BlockAsEntityPart
import genesis.util.render.ItemAsEntityPart
import genesis.util.render.ModelHelpers
import net.minecraft.client.Minecraft
import net.minecraft.client.model.ModelBase
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.item.ItemStack
import net.minecraft.util.math.MathHelper
import org.lwjgl.opengl.GL11
import java.util.*

class TileEntityCampfireRenderer : TileEntitySpecialRenderer<TileEntityCampfire>() {
    companion object {

        @JvmField
        val FIRE = ModelResourceLocation("genesis:campfire_fire")
        @JvmField
        val STICK = ModelResourceLocation("genesis:campfire_stick")
        @JvmField
        val COOKING_POT = ModelResourceLocation("genesis:campfire_cooking_pot")
        @JvmField
        val COOKING_ITEM = ModelResourceLocation("genesis:campfire_cooking_item")
        @JvmField
        val FUEL = ModelResourceLocation("genesis:campfire_fuel_item")

        lateinit var INSTANCE: TileEntityCampfireRenderer

        fun hasCookingModel(stack: ItemStack) = ModelHelpers.getStringIDInSetForStack(stack, INSTANCE.cookingItemModels) != null

        private fun lerp(a: Float, b: Float, pct: Float): Float {
            return b * pct + a * (1.0f - pct)
        }
    }

    val model = ModelCampfire()
    private val fireModels: MutableSet<String>
    private val fuelModels: MutableSet<String>
    private val cookingItemModels: MutableSet<String>

    init {
        INSTANCE = this

        // Get defined variants of the fire model.
        fireModels = HashSet()
        var key = "fire"

        for (str in ModelHelpers.getBlockstatesVariants(FIRE).keys) {
            if (!str.startsWith(key + "=")) {
                throw RuntimeException("Invalid property name in " + FUEL.toString() + " blockstates json. The property name must be \"fire\".")
            }

            fireModels.add(str.substring(key.length + 1))
        }

        // Force loading of actual fire models so that attempting to render it doesn't crash the game.
        fireModels.add("covered")
        fireModels.add("uncovered")
        ModelHelpers.forceModelLoading(key, fireModels, FIRE)

        // Force loading of fuel models.
        fuelModels = HashSet()
        key = "item"

        for (str in ModelHelpers.getBlockstatesVariants(FUEL).keys) {
            if (!str.startsWith(key + "=")) {
                throw RuntimeException("Invalid property name in " + FUEL.toString() + " blockstates json.")
            }

            fuelModels.add(str.substring(key.length + 1))
        }

        ModelHelpers.forceModelLoading(key, fuelModels, FUEL)

        // Force loading of cooking item models.
        cookingItemModels = HashSet()

        for (str in ModelHelpers.getBlockstatesVariants(COOKING_ITEM).keys) {
            if (!str.startsWith(key + "=")) {
                throw RuntimeException("Invalid property name in " + FUEL.toString() + " blockstates json.")
            }

            cookingItemModels.add(str.substring(key.length + 1))
        }

        ModelHelpers.forceModelLoading(key, cookingItemModels, COOKING_ITEM)

        ModelHelpers.forceModelLoading(STICK)
        ModelHelpers.forceModelLoading(COOKING_POT)
    }

    override fun render(campfire: TileEntityCampfire, x: Double, y: Double, z: Double, partialTicks: Float, destroyStage: Int, alpha: Float) {
        // Translate to the proper coordinates.
        GlStateManager.pushMatrix()
        GlStateManager.translate(x, y, z)

        // Get data about the block in the world.
        val world = campfire.world
        val pos = campfire.pos
        val state = world.getBlockState(pos)

        if (state.block !is BlockCampfire) return

        val facing = state.getValue(BlockCampfire.FACING)

        // Clear all rotation on the model parts before setting new rotation angles.
        model.fire.resetState()
        model.stick.resetState()
        model.cookingPot.resetState()
        model.stickItem.resetState()
        model.cookingItem.resetState()
        model.fuel.resetState()

        val properties = "facing=" + facing.name2

        model.stick.setModel(ModelHelpers.getLocationWithProperties(STICK, properties), world, pos)

        model.stick.rotateAngleZ = lerp(campfire.rotationLastTick, campfire.rotation, partialTicks)

        model.stick.rotateAngleY += facing.horizontalAngle + 90
        model.cookingItem.rotateAngleY += facing.horizontalAngle + 90

        val burning = campfire.isBurning

        // Set fire model location.
        if (burning) {
            model.fire.setModel(ModelHelpers.getLocationWithProperties(FIRE, "fire=uncovered"), world, pos)
        } else if (campfire.isWet) {
            if (fireModels.contains("wet"))
                model.fire.setModel(ModelHelpers.getLocationWithProperties(FIRE, "fire=wet"), world, pos)
        } else {
            if (fireModels.contains("none"))
                model.fire.setModel(ModelHelpers.getLocationWithProperties(FIRE, "fire=none"), world, pos)
        }

        if (campfire.hasCookingPot) {
            // Show only the cooking pot model.
            model.stickItem.showModel = false
            model.cookingPot.showModel = true

            model.cookingPot.setModel(ModelHelpers.getLocationWithProperties(COOKING_POT, properties), world, pos)
        } else {
            val input = campfire.input

            if (!input.isEmpty && TileEntityCampfire.canBurnItem(input)) {
                ModelHelpers.getStringIDInSetForStack(input, cookingItemModels)?.let { variant ->
                    // Change fire model to a "covered" version so that it doesn't clip through the cooking item.
                    if (burning)
                        model.fire.setModel(ModelHelpers.getLocationWithProperties(FIRE, "fire=covered"), world, pos)

                    model.cookingItem.setModel(ModelHelpers.getLocationWithProperties(COOKING_ITEM, "item=" + variant), world, pos)
                } ?: run {
                    // Show only the impaled item.
                    model.stickItem.showModel = true
                    model.cookingPot.showModel = false

                    // Set the stack to render on the stick.
                    model.stickItem.render = input
                    // Reset item's transformations.
                    model.stickItem.rotateAngleX += 90f

                    if (ModelHelpers.isGeneratedItemModel(input)) {
                        // Offset the item to prevent Z-fighting.
                        model.stickItem.offsetX -= 0.001f

                        // Scale the item to half size but thicker, to actually appear to be impaled.
                        model.stickItem.setScale(0.5f, 0.5f, 1.5f)
                    }
                }
            }
        }

        // Render a fuel model in the campfire (with possibility for custom models for individual items).
        // Will try to fall back to variant "item=generic_fuel" if no model definition is found.
        val fuel = campfire.fuel

        if (!fuel.isEmpty) {
            val itemID = ModelHelpers.getStringIDInSetForStack(fuel, fuelModels, "generic_fuel")

            if (itemID != null) {
                model.fuel.setModel(ModelHelpers.getLocationWithProperties(FUEL, "item=" + itemID), world, pos)
            }
        }

        ModelHelpers.bindAtlasTexture()

        if (destroyStage >= 0) {
            val tess = Tessellator.getInstance()
            val buff = tess.buffer

            RenderHelper.disableStandardItemLighting()
            Minecraft.getMinecraft().entityRenderer.disableLightmap()

            val breakTexture = ModelHelpers.getDestroyBlockIcon(destroyStage)

            buff.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK)
            buff.noColor()
            buff.setTranslation((-pos.x).toDouble(), (-pos.y).toDouble(), (-pos.z).toDouble())

            ModelHelpers
                    .blockRenderer
                    .renderModelFlat(world, ModelHelpers.getCubeProjectedBakedModel(state, ModelHelpers.getBakedBlockModel(state, world, pos), breakTexture, pos), state, pos, buff, true, MathHelper.getPositionRandom(pos))

            buff.setTranslation(0.0, 0.0, 0.0)
            GlStateManager.color(1f, 1f, 1f, 0.0f)
            tess.draw()

            model.fire.setTextureNoColor(breakTexture)
            model.fuel.setTextureNoColor(breakTexture)
            model.cookingItem.setTextureNoColor(breakTexture)
            model.stick.setTextureNoColor(breakTexture)
            model.cookingPot.setTextureNoColor(breakTexture)
        }

        // Render the model.
        model.renderAll()

        if (destroyStage >= 0) {
            RenderHelper.enableStandardItemLighting()
            Minecraft.getMinecraft().entityRenderer.enableLightmap()
        }

        GlStateManager.popMatrix()
    }

    class ModelCampfire : ModelBase() {
        val fire = BlockAsEntityPart(this)
        val fuel = BlockAsEntityPart(this)
        val cookingItem = BlockAsEntityPart(this)

        val stick = BlockAsEntityPart(this)
        val cookingPot = BlockAsEntityPart(this)

        val stickItem = ItemAsEntityPart(this)

        init {
            stick.rotationPointY = 1 + MathHelper.cos(Math.PI.toFloat() / 4) * 0.0625f
            stick.addChild(cookingPot)
            stick.addChild(stickItem)
            stick.setDefaultState()

            stickItem.offsetY = stick.rotationPointY
            stickItem.setDefaultState()

            cookingPot.setDefaultState()

            fire.setAmbientOcclusion(false)
        }

        fun renderAll() {
            RenderHelper.disableStandardItemLighting()
            fire.render(0.0625f)
            RenderHelper.enableStandardItemLighting()

            stick.render(0.0625f)
            fuel.render(0.0625f)
            cookingItem.render(0.0625f)
        }
    }
}
