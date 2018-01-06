package genesis.block

import genesis.init.GenesisCreativeTabs
import genesis.util.Harvest
import net.minecraft.block.*
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

@Suppress("OverridingDeprecatedMember")
class BlockWattleFence : BlockGenesis(Material.WOOD, MapColor.WOOD, SoundType.WOOD) {

    companion object {
        //The names after SIDE indicate where the fence is connecting, therefor what parts of the model are to be removed
        enum class EnumConnectState : IStringSerializable {
            NONE, SIDE, SIDE_BOTTOM, SIDE_TOP, SIDE_TOP_BOTTOM;

            override fun getName(): String {
                return name.toLowerCase()
            }
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
        defaultState = blockState.baseState.withProperty(NORTH, EnumConnectState.NONE).withProperty(EAST, EnumConnectState.NONE).withProperty(SOUTH, EnumConnectState.NONE).withProperty(WEST, EnumConnectState.NONE)
        setHarvestLevel(Harvest.CLASS_AXE, Harvest.LEVEL_WOOD)
    }

    private fun canConnectTo(worldIn: IBlockAccess, pos: BlockPos, facing: EnumFacing): Boolean {
        val state = worldIn.getBlockState(pos)

        if (state.block is BlockFence || state.block is BlockWall) return false

        val faceShape = state.getBlockFaceShape(worldIn, pos, facing)
        val flag = faceShape == BlockFaceShape.MIDDLE_POLE && (state.material === this.blockMaterial || state.block is BlockFenceGate)
        return faceShape == BlockFaceShape.SOLID || flag
    }

    override fun addCollisionBoxToList(state: IBlockState, worldIn: World, pos: BlockPos, entityBox: AxisAlignedBB, collidingBoxes: List<AxisAlignedBB>, entityIn: Entity?, isActualState: Boolean) {
        val state = if (isActualState) state else state.getActualState(worldIn, pos)

        Block.addCollisionBoxToList(pos, entityBox, collidingBoxes, BlockFence.PILLAR_AABB)

        if (state.getValue(NORTH) != EnumConnectState.NONE) Block.addCollisionBoxToList(pos, entityBox, collidingBoxes, BlockFence.NORTH_AABB)
        if (state.getValue(EAST) != EnumConnectState.NONE)  Block.addCollisionBoxToList(pos, entityBox, collidingBoxes, BlockFence.EAST_AABB)
        if (state.getValue(SOUTH) != EnumConnectState.NONE) Block.addCollisionBoxToList(pos, entityBox, collidingBoxes, BlockFence.SOUTH_AABB)
        if (state.getValue(WEST) != EnumConnectState.NONE)  Block.addCollisionBoxToList(pos, entityBox, collidingBoxes, BlockFence.WEST_AABB)

    }

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
        val above = shouldConnectVertical(world, pos.up())
        val below = shouldConnectVertical(world, pos.down())

        return state.withProperty(NORTH, getSideState(world, pos, EnumFacing.NORTH, above, below))
                .withProperty(EAST, getSideState(world, pos, EnumFacing.EAST, above, below))
                .withProperty(SOUTH, getSideState(world, pos, EnumFacing.SOUTH, above, below))
                .withProperty(WEST, getSideState(world, pos, EnumFacing.WEST, above, below))
    }

    private fun shouldConnectVertical(world: IBlockAccess, pos: BlockPos): Boolean {
        val state = world.getBlockState(pos)
        return state.block is BlockWattleFence
    }

    override fun getMetaFromState(state: IBlockState): Int {
        return 0
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

    override fun getBlockLayer(): BlockRenderLayer {
        return BlockRenderLayer.CUTOUT_MIPPED
    }

    override fun isOpaqueCube(state: IBlockState): Boolean {
        return false
    }

    override fun isFullCube(state: IBlockState): Boolean {
        return false
    }

    override fun isPassable(worldIn: IBlockAccess, pos: BlockPos): Boolean {
        return false
    }

    override fun shouldSideBeRendered(blockState: IBlockState, world: IBlockAccess, pos: BlockPos, side: EnumFacing): Boolean {
        return world.getBlockState(pos.offset(side)).block !is BlockWattleFence && super.shouldSideBeRendered(blockState, world, pos, side)
    }

    override fun getBlockFaceShape(worldIn: IBlockAccess, state: IBlockState, pos: BlockPos, face: EnumFacing): BlockFaceShape {
        if (worldIn.getBlockState(pos.offset(face)).block is BlockFence) return BlockFaceShape.UNDEFINED //I hate it, but I think it's the only way to stop normal fences from trying to connect
        return if (face != EnumFacing.UP && face != EnumFacing.DOWN) BlockFaceShape.MIDDLE_POLE else BlockFaceShape.CENTER
    }

    override fun canBeConnectedTo(world: IBlockAccess, pos: BlockPos, facing: EnumFacing): Boolean {
        val connector = world.getBlockState(pos.offset(facing)).block
        return connector is BlockWattleFence || connector is BlockFenceGate
    }

    private fun canFenceConnectTo(world: IBlockAccess, pos: BlockPos, facing: EnumFacing): Boolean {
        val other = pos.offset(facing)
        val block = world.getBlockState(other).block
        return block.canBeConnectedTo(world, other, facing.opposite) || canConnectTo(world, other, facing.opposite)
    }
}