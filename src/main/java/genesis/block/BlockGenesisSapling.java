package genesis.block;

import genesis.creativetab.GenesisCreativeTabs;
import genesis.combo.variant.EnumTree;
import genesis.world.gen.feature.WorldGenAbstractGenesisTree;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.IGrowable;
import net.minecraft.block.SoundType;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import java.util.Random;

public class BlockGenesisSapling extends BlockBush implements IGrowable {

    public static final PropertyInteger STAGE = PropertyInteger.create("stage", 0, 1);
    protected static final AxisAlignedBB SAPLING_AABB = new AxisAlignedBB(0.1D, 0.0D, 0.1D, 0.9D, 0.8D, 0.9D);
    private final EnumTree treeType;

    public BlockGenesisSapling(EnumTree treeType) {
        super();
        this.treeType = treeType;
        this.setDefaultState(this.blockState.getBaseState().withProperty(STAGE, 0));
        this.setHardness(0.0F);
        this.setSoundType(SoundType.PLANT);
        this.setCreativeTab(GenesisCreativeTabs.DECORATIONS);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return SAPLING_AABB;
    }

    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random random) {
        if (!world.isRemote) {
            this.checkAndDropBlock(world, pos, state);
            if (world.getLightFromNeighbors(pos.up()) >= 9 && random.nextInt(7) == 0) {
                this.grow(world, pos, state, random);
            }
        }
    }

    public void grow(World world, BlockPos pos, IBlockState state, Random rand) {
        if (state.getValue(STAGE) == 0) {
            world.setBlockState(pos, state.cycleProperty(STAGE), 4);
        } else {
            this.generateTree(world, pos, state, rand);
        }
    }

    public void generateTree(World world, BlockPos pos, IBlockState state, Random rand) {
        if (!net.minecraftforge.event.terraingen.TerrainGen.saplingGrowTree(world, rand, pos)) {
            return;
        }
        WorldGenAbstractGenesisTree worldgenerator = this.treeType.getTreeGenerator();
        if (worldgenerator != null) {
            boolean largeTree = false;
            int x = 0;
            int z = 0;
            IBlockState air = Blocks.AIR.getDefaultState();

            if (largeTree) {
                world.setBlockState(pos.add(x, 0, z), air, 4);
                world.setBlockState(pos.add(x + 1, 0, z), air, 4);
                world.setBlockState(pos.add(x, 0, z + 1), air, 4);
                world.setBlockState(pos.add(x + 1, 0, z + 1), air, 4);
            } else {
                world.setBlockState(pos, air, 4);
            }

            if (!worldgenerator.generate(world, rand, pos.add(x, 0, z))) {
                if (largeTree) {
                    world.setBlockState(pos.add(x, 0, z), state, 4);
                    world.setBlockState(pos.add(x + 1, 0, z), state, 4);
                    world.setBlockState(pos.add(x, 0, z + 1), state, 4);
                    world.setBlockState(pos.add(x + 1, 0, z + 1), state, 4);
                } else {
                    world.setBlockState(pos, state, 4);
                }
            }

        }
    }

    @Override
    public int damageDropped(IBlockState state) {
        return 0;
    }

    @Override
    public boolean canGrow(World world, BlockPos pos, IBlockState state, boolean isClient) {
        return true;
    }

    @Override
    public boolean canUseBonemeal(World world, Random rand, BlockPos pos, IBlockState state) {
        return (double) world.rand.nextFloat() < 0.45D;
    }

    @Override
    public void grow(World world, Random rand, BlockPos pos, IBlockState state) {
        this.grow(world, pos, state, rand);
    }

    @SuppressWarnings("deprecation")
    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(STAGE, (meta & 8) >> 3);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        int i = 0;
        i = i | (state.getValue(STAGE) << 3);
        return i;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, STAGE);
    }

}
