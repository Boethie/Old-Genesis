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

package genesis.util.render

import com.google.common.collect.ImmutableMap
import net.minecraft.block.Block
import net.minecraft.block.material.EnumPushReaction
import net.minecraft.block.material.MapColor
import net.minecraft.block.material.Material
import net.minecraft.block.properties.IProperty
import net.minecraft.block.state.BlockFaceShape
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.EnumBlockRenderType
import net.minecraft.util.EnumFacing
import net.minecraft.util.Mirror
import net.minecraft.util.Rotation
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.RayTraceResult
import net.minecraft.util.math.Vec3d
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World

class FakeBlockState(private val actualState: IBlockState) : IBlockState {

    override fun getPropertyKeys(): Collection<IProperty<*>> = actualState.propertyKeys

    override fun <T : Comparable<T>> getValue(iProperty: IProperty<T>): T = actualState.getValue(iProperty)

    override fun <T : Comparable<T>, V : T> withProperty(iProperty: IProperty<T>, v: V): IBlockState = actualState.withProperty(iProperty, v)

    override fun <T : Comparable<T>> cycleProperty(iProperty: IProperty<T>): IBlockState = actualState.cycleProperty(iProperty)

    override fun getProperties(): ImmutableMap<IProperty<*>, Comparable<*>> = actualState.properties

    override fun getBlock(): Block = actualState.block

    override fun onBlockEventReceived(world: World, blockPos: BlockPos, i: Int, i1: Int): Boolean = actualState.onBlockEventReceived(world, blockPos, i, i1)

    override fun neighborChanged(world: World, blockPos: BlockPos, block: Block, blockPos1: BlockPos) {
        actualState.neighborChanged(world, blockPos, block, blockPos1)
    }

    override fun getMaterial(): Material = actualState.material

    override fun isFullBlock(): Boolean = actualState.isFullBlock

    override fun canEntitySpawn(entity: Entity): Boolean = actualState.canEntitySpawn(entity)

    override fun getLightOpacity(): Int = actualState.lightOpacity

    override fun getLightOpacity(iBlockAccess: IBlockAccess, blockPos: BlockPos): Int = actualState.getLightOpacity(iBlockAccess, blockPos)

    override fun getLightValue(): Int = actualState.lightValue

    override fun getLightValue(iBlockAccess: IBlockAccess, blockPos: BlockPos): Int = actualState.getLightValue(iBlockAccess, blockPos)

    override fun isTranslucent(): Boolean = actualState.isTranslucent

    override fun useNeighborBrightness(): Boolean = actualState.useNeighborBrightness()

    override fun getMapColor(iBlockAccess: IBlockAccess, blockPos: BlockPos): MapColor = actualState.getMapColor(iBlockAccess, blockPos)

    override fun withRotation(rotation: Rotation): IBlockState = actualState.withRotation(rotation)

    override fun withMirror(mirror: Mirror): IBlockState = actualState.withMirror(mirror)

    override fun isFullCube(): Boolean = actualState.isFullCube

    override fun hasCustomBreakingProgress(): Boolean = actualState.hasCustomBreakingProgress()

    override fun getRenderType(): EnumBlockRenderType = actualState.renderType

    override fun getPackedLightmapCoords(iBlockAccess: IBlockAccess, blockPos: BlockPos): Int = actualState.getPackedLightmapCoords(iBlockAccess, blockPos)

    override fun getAmbientOcclusionLightValue(): Float = actualState.ambientOcclusionLightValue

    override fun isBlockNormalCube(): Boolean = actualState.isBlockNormalCube

    override fun isNormalCube(): Boolean = actualState.isNormalCube

    override fun canProvidePower(): Boolean = actualState.canProvidePower()

    override fun getWeakPower(iBlockAccess: IBlockAccess, blockPos: BlockPos, enumFacing: EnumFacing): Int = actualState.getWeakPower(iBlockAccess, blockPos, enumFacing)

    override fun hasComparatorInputOverride(): Boolean = actualState.hasComparatorInputOverride()

    override fun getComparatorInputOverride(world: World, blockPos: BlockPos): Int = actualState.getComparatorInputOverride(world, blockPos)

    override fun getBlockHardness(world: World, blockPos: BlockPos): Float = actualState.getBlockHardness(world, blockPos)

    override fun getPlayerRelativeBlockHardness(entityPlayer: EntityPlayer, world: World, blockPos: BlockPos): Float = actualState.getPlayerRelativeBlockHardness(entityPlayer, world, blockPos)

    override fun getStrongPower(iBlockAccess: IBlockAccess, blockPos: BlockPos, enumFacing: EnumFacing): Int = actualState.getStrongPower(iBlockAccess, blockPos, enumFacing)

    override fun getMobilityFlag(): EnumPushReaction = actualState.mobilityFlag

    override fun getActualState(iBlockAccess: IBlockAccess, blockPos: BlockPos): IBlockState = actualState.getActualState(iBlockAccess, blockPos)

    override fun getSelectedBoundingBox(world: World, blockPos: BlockPos): AxisAlignedBB = actualState.getSelectedBoundingBox(world, blockPos)

    override fun shouldSideBeRendered(iBlockAccess: IBlockAccess, blockPos: BlockPos, enumFacing: EnumFacing): Boolean = actualState.shouldSideBeRendered(iBlockAccess, blockPos, enumFacing)

    override fun isOpaqueCube(): Boolean = actualState.isOpaqueCube

    override fun getCollisionBoundingBox(iBlockAccess: IBlockAccess, blockPos: BlockPos): AxisAlignedBB? = actualState.getCollisionBoundingBox(iBlockAccess, blockPos)

    override fun addCollisionBoxToList(world: World, blockPos: BlockPos, axisAlignedBB: AxisAlignedBB, list: List<AxisAlignedBB>, entity: Entity?, b: Boolean) {
        actualState.addCollisionBoxToList(world, blockPos, axisAlignedBB, list, entity, b)
    }

    override fun getBoundingBox(iBlockAccess: IBlockAccess, blockPos: BlockPos): AxisAlignedBB = actualState.getBoundingBox(iBlockAccess, blockPos)

    override fun collisionRayTrace(world: World, blockPos: BlockPos, vec3d: Vec3d, vec3d1: Vec3d): RayTraceResult = actualState.collisionRayTrace(world, blockPos, vec3d, vec3d1)

    override fun isTopSolid(): Boolean = actualState.isTopSolid

    override fun doesSideBlockRendering(iBlockAccess: IBlockAccess, blockPos: BlockPos, enumFacing: EnumFacing): Boolean = actualState.doesSideBlockRendering(iBlockAccess, blockPos, enumFacing)

    override fun isSideSolid(iBlockAccess: IBlockAccess, blockPos: BlockPos, enumFacing: EnumFacing): Boolean = actualState.isSideSolid(iBlockAccess, blockPos, enumFacing)

    override fun doesSideBlockChestOpening(iBlockAccess: IBlockAccess, blockPos: BlockPos, enumFacing: EnumFacing): Boolean = actualState.doesSideBlockChestOpening(iBlockAccess, blockPos, enumFacing)

    override fun getOffset(iBlockAccess: IBlockAccess, blockPos: BlockPos): Vec3d = actualState.getOffset(iBlockAccess, blockPos)

    override fun causesSuffocation(): Boolean = actualState.causesSuffocation()

    override fun getBlockFaceShape(iBlockAccess: IBlockAccess, blockPos: BlockPos, enumFacing: EnumFacing): BlockFaceShape = actualState.getBlockFaceShape(iBlockAccess, blockPos, enumFacing)
}
