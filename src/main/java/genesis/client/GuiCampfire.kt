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

package genesis.client

import genesis.GenesisMod
import genesis.block.tile.campfire.ContainerCampfire
import genesis.block.tile.campfire.TileEntityCampfire
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.client.renderer.BufferBuilder
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.inventory.IInventory
import net.minecraft.util.ResourceLocation

class GuiCampfire(playerInv: InventoryPlayer, private val campfire: IInventory)
    : GuiContainer(ContainerCampfire(playerInv, campfire)) {
    init {
        ySize = 195
    }

    companion object {
        @JvmField val BACKGROUND = ResourceLocation(GenesisMod.MOD_ID, "textures/gui/container/campfire.png")
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        this.drawDefaultBackground()
        super.drawScreen(mouseX, mouseY, partialTicks)
        this.renderHoveredToolTip(mouseX, mouseY)
    }

    override fun drawGuiContainerBackgroundLayer(partialTicks: Float, mouseX: Int, mouseY: Int) {
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
        this.mc.textureManager.bindTexture(BACKGROUND)
        val x = (this.width - this.xSize) / 2
        val y = (this.height - this.ySize) / 2

        val tessellator = Tessellator.getInstance()
        val buffer = tessellator.buffer

        buffer.begin(7, DefaultVertexFormats.POSITION_TEX);

        this.drawTexturedModalRectBatched(buffer, x, y, 0, 0, this.xSize, this.ySize)

        if (TileEntityCampfire.isWet(this.campfire)) {
            val k = this.getWetLeftScaled(13)
            this.drawTexturedModalRectBatched(buffer, x + 48, y + 63 + 12 - k, 190, 12 - k, 14, k + 1)
        }
        else if (TileEntityCampfire.isBurning(this.campfire)) {
            val k = this.getBurnLeftScaled(13)
            this.drawTexturedModalRectBatched(buffer, x + 48, y + 63 + 12 - k, 176, 12 - k, 14, k + 1)
        }

        val l = this.getCookProgressScaled(24)
        this.drawTexturedModalRectBatched(buffer, x + 99, y + 49, 176, 14, l, 16)

        if (TileEntityCampfire.hasCookingPot(campfire)) {
            this.drawTexturedModalRectBatched(buffer, x + 22, y + 17, 176, 30, 18, 18)
            this.drawTexturedModalRectBatched(buffer, x + 46, y + 17, 176, 30, 18, 18)
            this.drawTexturedModalRectBatched(buffer, x + 70, y + 17, 176, 30, 18, 18)
        }

        tessellator.draw()
    }

    private fun getCookProgressScaled(pixels: Int): Int {
        val i = this.campfire.getField(3)
        return if (i != 0) i * pixels / TileEntityCampfire.maxCookTime else 0
    }

    private fun getBurnLeftScaled(pixels: Int): Int {
        var i = this.campfire.getField(1)

        if (i == 0) {
            i = 200
        }

        return this.campfire.getField(0) * pixels / i
    }

    private fun getWetLeftScaled(pixels: Int) = this.campfire.getField(2) * pixels / TileEntityCampfire.maxWetTime

    fun drawTexturedModalRectBatched(builder: BufferBuilder, x: Int, y: Int, textureX: Int, textureY: Int, width: Int, height: Int) {
        builder.pos((x + 0).toDouble(), (y + height).toDouble(), this.zLevel.toDouble()).tex(((textureX + 0).toFloat() * 0.00390625f).toDouble(), ((textureY + height).toFloat() * 0.00390625f).toDouble()).endVertex()
        builder.pos((x + width).toDouble(), (y + height).toDouble(), this.zLevel.toDouble()).tex(((textureX + width).toFloat() * 0.00390625f).toDouble(), ((textureY + height).toFloat() * 0.00390625f).toDouble()).endVertex()
        builder.pos((x + width).toDouble(), (y + 0).toDouble(), this.zLevel.toDouble()).tex(((textureX + width).toFloat() * 0.00390625f).toDouble(), ((textureY + 0).toFloat() * 0.00390625f).toDouble()).endVertex()
        builder.pos((x + 0).toDouble(), (y + 0).toDouble(), this.zLevel.toDouble()).tex(((textureX + 0).toFloat() * 0.00390625f).toDouble(), ((textureY + 0).toFloat() * 0.00390625f).toDouble()).endVertex()
    }
}