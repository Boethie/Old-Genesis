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

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.PositionTextureVertex;
import net.minecraft.client.model.TexturedQuad;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nonnull;

public class ModelPlane extends CustomModelElement {
    protected TexturedQuad quadUp;
    protected TexturedQuad quadDown;

    public ModelPlane(ModelRenderer part, float u, float v, EnumFacing.Axis facingAxis, float x, float y, float z, float w, float h) {
        super(part);

        Vec3d start = new Vec3d(x, y, z);
        Vec3d localW;
        Vec3d localH;

        float texW = part.textureWidth;
        float texH = part.textureHeight;

        float uW = w / texW;
        float uH = h / texH;

        switch (facingAxis) {
            case X:
                localW = new Vec3d(0, 0, w);
                localH = new Vec3d(0, h, 0);
                break;
            case Y:
                localW = new Vec3d(h, 0, 0);
                localH = new Vec3d(0, 0, w);

                uW = h / texW;
                uH = w / texH;
                break;
            case Z:
                localW = new Vec3d(w, 0, 0);
                localH = new Vec3d(0, h, 0);
                break;
            default:
                throw new IllegalArgumentException(facingAxis + " is not a valid axis for a ModelPlane.");
        }

        float uStart = u / texW;
        float uEnd = u / texW + uW;
        float vStart = v / texH;
        float vEnd = v / texH + uH;

        PositionTextureVertex vertBL;
        PositionTextureVertex vertTL;
        PositionTextureVertex vertTR;
        PositionTextureVertex vertBR;

        vertBL = new PositionTextureVertex(start, uStart, vStart);
        vertTL = new PositionTextureVertex(start.add(localH), uStart, vEnd);
        vertTR = new PositionTextureVertex(start.add(localW).add(localH), uEnd, vEnd);
        vertBR = new PositionTextureVertex(start.add(localW), uEnd, vStart);

        quadUp = new TexturedQuad(new PositionTextureVertex[]{vertBR, vertTR, vertTL, vertBL});

        uStart += uW;
        uEnd += uW;
        vertBL = new PositionTextureVertex(start.add(localW), uStart, vStart);
        vertTL = new PositionTextureVertex(start.add(localW).add(localH), uStart, vEnd);
        vertTR = new PositionTextureVertex(start.add(localH), uEnd, vEnd);
        vertBR = new PositionTextureVertex(start, uEnd, vStart);

        quadDown = new TexturedQuad(new PositionTextureVertex[]{vertBR, vertTR, vertTL, vertBL});
    }

    public ModelPlane(EntityPart part, EnumFacing.Axis facingAxis, float x, float y, float z, float w, float h) {
        this(part, part.getTextureOffsetX(), part.getTextureOffsetY(), facingAxis, x, y, z, w, h);
    }

    @Override
    public void render(@Nonnull BufferBuilder buffer, float scale) {
        GlStateManager.enableCull();
        quadUp.draw(buffer, scale);
        quadDown.draw(buffer, scale);
        GlStateManager.disableCull();
    }
}
