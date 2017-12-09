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
package genesis.block

import genesis.init.GenesisBlocks
import genesis.util.BoundingBoxes
import genesis.util.Harvest
import genesis.util.WorldFlags
import net.minecraft.block.Block
import net.minecraft.block.SoundType
import net.minecraft.block.material.MapColor
import net.minecraft.block.material.Material
import net.minecraft.block.state.BlockFaceShape
import net.minecraft.block.state.IBlockState
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.util.*

open class BlockHumusPathBase : BlockGenesis(Material.GROUND, MapColor.DIRT, SoundType.GROUND) {
    init {
        setLightOpacity(255)
        setHarvestLevel(Harvest.CLASS_SHOVEL, Harvest.LEVEL_WOOD)
    }

    @SideOnly(Side.CLIENT)
    override fun shouldSideBeRendered(blockState: IBlockState, blockAccess: IBlockAccess, pos: BlockPos, side: EnumFacing): Boolean {
        return when (side) {
            EnumFacing.UP -> true
            EnumFacing.NORTH, EnumFacing.SOUTH, EnumFacing.WEST, EnumFacing.EAST -> {
                val sideState = blockAccess.getBlockState(pos.offset(side))
                val sideBlock = sideState.block
                !sideState.isOpaqueCube && sideBlock !is BlockHumusPathBase
            }
            else -> super.shouldSideBeRendered(blockState, blockAccess, pos, side)
        }
    }

    override fun neighborChanged(state: IBlockState, world: World, pos: BlockPos, block: Block, fromPos: BlockPos) {
        super.neighborChanged(state, world, pos, block, fromPos)
        updateBlockState(world, pos)
    }

    override fun onBlockAdded(world: World, pos: BlockPos, state: IBlockState) {
        super.onBlockAdded(world, pos, state)
        updateBlockState(world, pos)
    }

    private fun updateBlockState(world: World, pos: BlockPos) {
        if (world.getBlockState(pos.up()).material.isSolid) {
            turnToHumus(world, pos)
        }
    }

    protected fun turnToHumus(world: World, pos: BlockPos) {
        world.setBlockState(pos, GenesisBlocks.HUMUS.defaultState, WorldFlags.UPDATE_BLOCK_AND_CLIENT)

        val airBB = BoundingBoxes.PATH_AIR.offset(pos)
        for (entity in world.getEntitiesWithinAABBExcludingEntity(null, airBB)) {
            val yOffset = Math.min(airBB.maxY - airBB.minY, airBB.maxY - entity.entityBoundingBox.minY)
            entity.setPositionAndUpdate(entity.posX, entity.posY + yOffset + 0.001, entity.posZ)
        }
    }

    override fun getBoundingBox(state: IBlockState, source: IBlockAccess, pos: BlockPos) = BoundingBoxes.PATH

    override fun isOpaqueCube(state: IBlockState) = false

    override fun isFullCube(state: IBlockState) = false

    override fun getItemDropped(state: IBlockState, rand: Random, fortune: Int) = Item.getItemFromBlock(GenesisBlocks.HUMUS)

    override fun getItem(world: World, pos: BlockPos, state: IBlockState) = ItemStack(this)

    override fun getBlockFaceShape(world: IBlockAccess, state: IBlockState, pos: BlockPos, face: EnumFacing): BlockFaceShape {
        return when (face) {
            EnumFacing.DOWN -> BlockFaceShape.SOLID
            else -> BlockFaceShape.UNDEFINED
        }
    }
}