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

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import org.lwjgl.opengl.GL11;

public class BlockAsEntityPart extends CustomEntityPart {
    // Values
    private IBlockState state;
    private IBlockAccess world;
    private BlockPos pos;

    private boolean ambientOcclusion = true;
    private boolean noColor;

    private TextureAtlasSprite texture = null;

    // Defaults
    private IBlockState stateDef;
    private IBlockAccess worldDef;
    private BlockPos posDef;

    private boolean ambientOcclusionDef = true;
    private boolean noColorDef;

    private TextureAtlasSprite textureDef = null;

    public BlockAsEntityPart(ModelBase model) {
        super(model);

        rotationPointX += 0.5F;
        rotationPointZ += 0.5F;
        setDefaultState();
    }

    @Override
    public BlockAsEntityPart setDefaultState() {
        super.setDefaultState();

        stateDef = state;
        worldDef = world;
        posDef = pos;

        ambientOcclusionDef = ambientOcclusion;
        noColorDef = noColor;

        textureDef = texture;

        return this;
    }

    @Override
    public void resetState() {
        super.resetState();

        state = stateDef;
        world = worldDef;
        pos = posDef;

        ambientOcclusion = ambientOcclusionDef;
        noColor = noColorDef;

        texture = textureDef;
    }

    public void setModel(IBlockState state, IBlockAccess world, BlockPos pos) {
        this.state = state;
        this.world = world;
        this.pos = pos;
    }

    public void setModel(ModelResourceLocation location, IBlockAccess world, BlockPos pos) {
        this.state = ModelHelpers.INSTANCE.getFakeState(location);
        this.world = world;
        this.pos = pos;
    }

    public void setAmbientOcclusion(boolean ambientOcclusion) {
        this.ambientOcclusion = ambientOcclusion;
    }

    public void setTexture(TextureAtlasSprite texture) {
        this.texture = texture;
    }

    public void noColor(boolean noColor) {
        this.noColor = noColor;
        if (noColor) setAmbientOcclusion(false);
    }

    public void setTextureNoColor(TextureAtlasSprite texture) {
        setTexture(texture);
        noColor(true);
    }

    @Override
    public void render(float pxSize) {
        super.render(pxSize);

        if (!isHidden && showModel) {
            RenderHelper.enableStandardItemLighting();
        }
    }

    @Override
    public void doRender(float pxSize) {
        if (state != null) {
            float scale = pxSize * 16;
            GlStateManager.scale(scale, scale, scale);

            IBakedModel model = ModelHelpers.INSTANCE.getBakedBlockModel(state, world, pos);

            if (texture != null) {
                model = ModelHelpers.INSTANCE.getCubeProjectedBakedModel(state, model, texture, pos);
            }

            Tessellator tess = Tessellator.getInstance();
            BufferBuilder buff = tess.getBuffer();

            GlStateManager.pushMatrix();
            GlStateManager.translate(-pos.getX(), -pos.getY(), -pos.getZ());

            RenderHelper.disableStandardItemLighting();

            buff.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);

            if (noColor) buff.noColor();

            if (ambientOcclusion) ModelHelpers.INSTANCE
                    .getBlockRenderer()
                    .renderModelSmooth(world, model, state, pos, buff, false, MathHelper.getPositionRandom(pos));
            else ModelHelpers.INSTANCE
                    .getBlockRenderer()
                    .renderModelFlat(world, model, state, pos, buff, false, MathHelper.getPositionRandom(pos));

            tess.draw();

            GlStateManager.popMatrix();
        }
    }

    @Override
    public void renderWithRotation(float pxSize) {
        render(pxSize);
    }
}
