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

import genesis.init.GenesisCreativeTabs
import genesis.util.Harvest
import net.minecraft.block.Block
import net.minecraft.block.BlockFence
import net.minecraft.block.SoundType
import net.minecraft.block.material.MapColor
import net.minecraft.block.material.Material
import net.minecraft.block.properties.PropertyEnum
import net.minecraft.block.state.BlockFaceShape
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.Entity
import net.minecraft.util.*
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World

//This doesn't extend BlockFence because it makes it even hackier to try and prevent normal fences from connecting with wattle fences
//This doesn't use a variant property because nothing else in the mod does, nor the vanilla fences
@Suppress("OverridingDeprecatedMember")
class BlockWattleFence : BlockGenesis(Material.WOOD, MapColor.WOOD, SoundType.WOOD) {

    companion object {
        //The names after SIDE indicate where the fence is connecting, therefor what parts of the model are to be removed
        enum class EnumConnectState : IStringSerializable {
            NONE, SIDE, SIDE_BOTTOM, SIDE_TOP, SIDE_TOP_BOTTOM;

            override fun getName() = name.toLowerCase()
        }

        private val NORTH = PropertyEnum.create("north", EnumConnectState::class.java)
        private val EAST = PropertyEnum.create("east", EnumConnectState::class.java)
        private val SOUTH = PropertyEnum.create("south", EnumConnectState::class.java)
        private val WEST = PropertyEnum.create("west", EnumConnectState::class.java)

        private val SELECTION_BOXES = arrayOf(
                AxisAlignedBB(0.375, 0.0, 0.375, 0.625, 1.0, 0.625),    //Pillar
                AxisAlignedBB(0.375, 0.0, 0.0, 0.625, 1.0, 0.375),      //North
                AxisAlignedBB(0.625, 0.0, 0.375, 1.0, 1.0, 0.625),      //East
                AxisAlignedBB(0.375, 0.0, 0.625, 0.625, 1.0, 1.0),      //South
                AxisAlignedBB(0.0, 0.0, 0.375, 0.375, 1.0, 0.625))      //West
    }

    init {
        setHardness(2F)
        setResistance(5F)
        setCreativeTab(GenesisCreativeTabs.DECORATIONS)
        defaultState = this.blockState.baseState.withProperty(NORTH, EnumConnectState.NONE).withProperty(EAST, EnumConnectState.NONE).withProperty(SOUTH, EnumConnectState.NONE).withProperty(WEST, EnumConnectState.NONE)
        setHarvestLevel(Harvest.CLASS_AXE, Harvest.LEVEL_WOOD)
    }

    override fun canPlaceTorchOnTop(state: IBlockState, world: IBlockAccess, pos: BlockPos) = true

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer(this, NORTH, EAST, SOUTH, WEST)
    }

    private fun getSideState(world: IBlockAccess, pos: BlockPos, side: EnumFacing, above: Boolean, below: Boolean): EnumConnectState {
        var sideState = EnumConnectState.NONE
        if (canFenceConnectTo(world, pos, side)) {
            val sidePos = pos.offset(side)

            //We only ever want to connect when there is at least one fence above or below us
            val fencesAbove = above || world.getBlockState(sidePos.up()).block is BlockWattleFence
            val fencesBelow = below || world.getBlockState(sidePos.down()).block is BlockWattleFence

            //This is ugly, I know
            val connectTop = fencesAbove && canConnectTo(world, sidePos.up(), side.opposite) && (above || canConnectTo(world, pos.up(), EnumFacing.DOWN))
            val connectBottom = fencesBelow && canConnectTo(world, sidePos.down(), side.opposite) && (below || canConnectTo(world, pos.down(), EnumFacing.UP))

            sideState = when {
                connectTop && connectBottom -> EnumConnectState.SIDE_TOP_BOTTOM
                connectTop -> EnumConnectState.SIDE_TOP
                connectBottom -> EnumConnectState.SIDE_BOTTOM
                else -> EnumConnectState.SIDE
            }
        }

        return sideState
    }

    override fun getActualState(state: IBlockState, world: IBlockAccess, pos: BlockPos): IBlockState {
        val above = world.getBlockState(pos.up()).block is BlockWattleFence
        val below = world.getBlockState(pos.down()).block is BlockWattleFence

        val north = getSideState(world, pos, EnumFacing.NORTH, above, below)
        val east = getSideState(world, pos, EnumFacing.EAST, above, below)
        val south = getSideState(world, pos, EnumFacing.SOUTH, above, below)
        val west = getSideState(world, pos, EnumFacing.WEST, above, below)
        return state.withProperty(NORTH, north)
                .withProperty(EAST, east)
                .withProperty(SOUTH, south)
                .withProperty(WEST, west)
    }

    override fun getMetaFromState(state: IBlockState) = 0

    override fun getBoundingBox(state: IBlockState, world: IBlockAccess, pos: BlockPos): AxisAlignedBB {
        val state = state.getActualState(world, pos)
        var box = SELECTION_BOXES[0]
        // This joins the bounding boxes for each side that has a connection
        if (state.getValue(NORTH) != EnumConnectState.NONE) box = box.union(SELECTION_BOXES[1])
        if (state.getValue(EAST) != EnumConnectState.NONE)  box = box.union(SELECTION_BOXES[2])
        if (state.getValue(SOUTH) != EnumConnectState.NONE) box = box.union(SELECTION_BOXES[3])
        if (state.getValue(WEST) != EnumConnectState.NONE)  box = box.union(SELECTION_BOXES[4])

        return box
    }

    override fun addCollisionBoxToList(state: IBlockState, worldIn: World, pos: BlockPos, entityBox: AxisAlignedBB, collidingBoxes: List<AxisAlignedBB>, entityIn: Entity?, isActualState: Boolean) {
        val state = if (isActualState) state else state.getActualState(worldIn, pos)

        Block.addCollisionBoxToList(pos, entityBox, collidingBoxes, BlockFence.PILLAR_AABB)

        if (state.getValue(NORTH) != EnumConnectState.NONE) Block.addCollisionBoxToList(pos, entityBox, collidingBoxes, BlockFence.NORTH_AABB)
        if (state.getValue(EAST) != EnumConnectState.NONE)  Block.addCollisionBoxToList(pos, entityBox, collidingBoxes, BlockFence.EAST_AABB)
        if (state.getValue(SOUTH) != EnumConnectState.NONE) Block.addCollisionBoxToList(pos, entityBox, collidingBoxes, BlockFence.SOUTH_AABB)
        if (state.getValue(WEST) != EnumConnectState.NONE)  Block.addCollisionBoxToList(pos, entityBox, collidingBoxes, BlockFence.WEST_AABB)

    }

    override fun withMirror(state: IBlockState, mirrorIn: Mirror): IBlockState {
        return when (mirrorIn) {
            Mirror.LEFT_RIGHT -> state.withProperty(NORTH, state.getValue(SOUTH)).withProperty(SOUTH, state.getValue(NORTH))
            Mirror.FRONT_BACK -> state.withProperty(EAST, state.getValue(WEST)).withProperty(WEST, state.getValue(EAST))
            else -> state
        }
    }

    override fun withRotation(state: IBlockState, rot: Rotation): IBlockState {
        return when (rot) {
            Rotation.CLOCKWISE_180 -> state.withProperty(NORTH, state.getValue(SOUTH)).withProperty(EAST, state.getValue(WEST)).withProperty(SOUTH, state.getValue(NORTH)).withProperty(WEST, state.getValue(EAST))
            Rotation.COUNTERCLOCKWISE_90 -> state.withProperty(NORTH, state.getValue(EAST)).withProperty(EAST, state.getValue(SOUTH)).withProperty(SOUTH, state.getValue(WEST)).withProperty(WEST, state.getValue(NORTH))
            Rotation.CLOCKWISE_90 -> state.withProperty(NORTH, state.getValue(WEST)).withProperty(EAST, state.getValue(NORTH)).withProperty(SOUTH, state.getValue(EAST)).withProperty(WEST, state.getValue(SOUTH))
            else -> state
        }
    }

    override fun getBlockLayer() = BlockRenderLayer.CUTOUT_MIPPED

    override fun isOpaqueCube(state: IBlockState) = false

    override fun isFullCube(state: IBlockState) = false

    override fun isPassable(worldIn: IBlockAccess, pos: BlockPos) = false

    override fun shouldSideBeRendered(blockState: IBlockState, world: IBlockAccess, pos: BlockPos, side: EnumFacing): Boolean {
        return world.getBlockState(pos.offset(side)).block !is BlockWattleFence && super.shouldSideBeRendered(blockState, world, pos, side)
    }

    override fun getBlockFaceShape(worldIn: IBlockAccess, state: IBlockState, pos: BlockPos, face: EnumFacing): BlockFaceShape {
        return if (worldIn.getBlockState(pos.offset(face)).block is BlockFence) BlockFaceShape.UNDEFINED
        else if (face != EnumFacing.UP && face != EnumFacing.DOWN) BlockFaceShape.MIDDLE_POLE
        else BlockFaceShape.CENTER
    }

    override fun canBeConnectedTo(world: IBlockAccess, pos: BlockPos, facing: EnumFacing): Boolean {
        return world.getBlockState(pos.offset(facing)).block is BlockWattleFence
    }

    private fun canConnectTo(worldIn: IBlockAccess, pos: BlockPos, facing: EnumFacing): Boolean {
        val state = worldIn.getBlockState(pos)

        val faceShape = state.getBlockFaceShape(worldIn, pos, facing)
        return faceShape == BlockFaceShape.SOLID || state.block is BlockWattleFence
    }

    private fun canFenceConnectTo(world: IBlockAccess, pos: BlockPos, facing: EnumFacing): Boolean {
        val other = pos.offset(facing)
        val block = world.getBlockState(other).block
        return block.canBeConnectedTo(world, other, facing.opposite) && block !is BlockFence || canConnectTo(world, other, facing.opposite)
    }
}