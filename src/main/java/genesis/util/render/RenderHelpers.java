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

package genesis.util.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult;
import org.lwjgl.opengl.GL11;

public class RenderHelpers {
    public static void renderEntityBounds(Entity entity, float partialTick) {
        Minecraft mc = Minecraft.getMinecraft();

        if (!mc.gameSettings.hideGUI) {
            // Render bounding box around entity if it is being looked at.
            RayTraceResult lookingAt = mc.objectMouseOver;

            if (lookingAt != null && lookingAt.typeOfHit == RayTraceResult.Type.ENTITY && lookingAt.entityHit == entity) {
                GL11.glPushAttrib(GL11.GL_CURRENT_BIT);

                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                GlStateManager.color(0, 0, 0, 0.4F);
                GL11.glLineWidth(2);
                GlStateManager.disableTexture2D();
                GlStateManager.depthMask(false);
                float expand = entity.getCollisionBorderSize() + 0.002F;

                double offX = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTick;
                double offY = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTick;
                double offZ = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTick;
                RenderGlobal.drawSelectionBoundingBox(entity
                        .getEntityBoundingBox()
                        .expand(expand, expand, expand)
                        .offset(-offX, -offY, -offZ), 1.0F, 1.0F, 1.0F, 1.0F);

                GlStateManager.depthMask(true);
                GlStateManager.enableTexture2D();
                GlStateManager.disableBlend();

                GL11.glPopAttrib();
            }
        }
    }

    public static void renderEntityBounds(Entity entity, double x, double y, double z, float partialTick) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        renderEntityBounds(entity, partialTick);
        GlStateManager.popMatrix();
    }

    public static void drawTextureWithTessellator(int x, int y, int zLevel, int sizeX, int sizeY, ResourceLocation texture, float alpha) {
        GlStateManager.pushMatrix();

        Tessellator tess = Tessellator.getInstance();

        Minecraft.getMinecraft().renderEngine.bindTexture(texture);

        BufferBuilder vb = tess.getBuffer();

        vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

        vb.pos(x, y + sizeY, 1).tex(0, 1).endVertex();

        vb.pos(x + sizeX, y + sizeY, 1).tex(1, 1).endVertex();

        vb.pos(x + sizeX, y, 1).tex(1, 0).endVertex();

        vb.pos(x, y, 1).tex(0, 0).endVertex();

        tess.draw();

        GlStateManager.popMatrix();
    }
}
