package genesis.combo.variant;

import genesis.init.GenesisBlocks;
import genesis.world.gen.feature.WorldGenAbstractGenesisTree;
import genesis.world.gen.feature.WorldGenTreeAraucarioxylon;
import genesis.world.gen.feature.WorldGenTreeDryophyllum;
import genesis.world.gen.feature.WorldGenTreeDryophyllum.DryophyllumVariant;
import genesis.world.gen.feature.WorldGenTreeGinkgo;
import net.minecraft.block.Block;

import java.util.Random;

public enum EnumTree {

    ARAUCARIOXYLON(GenesisBlocks.ARAUCARIOXYLON_SAPLING),
    DRYOPHYLLUM(GenesisBlocks.DRYOPHYLLUM_SAPLING),
    FICUS(GenesisBlocks.FICUS_SAPLING),
    GINKGO(GenesisBlocks.GINKGO_SAPLING),
    METASEQUOIA(GenesisBlocks.METASEQUOIA_SAPLING);

    private static final Random rand = new Random();
    private final Block sapling;

    private EnumTree(Block sapling) {
        this.sapling = sapling;
    }

    public Block getSapling() {
        return this.sapling;
    }

    public boolean canGrowLargeTree() {
        return false;
    }

    public WorldGenAbstractGenesisTree getTreeGenerator() {
        switch (this) {
            case ARAUCARIOXYLON:
                return new WorldGenTreeAraucarioxylon(25, 30);
            case DRYOPHYLLUM:
                return (rand.nextInt(6) != 0 ?
                        new WorldGenTreeDryophyllum(DryophyllumVariant.TYPE_1, 11, 15) :
                        new WorldGenTreeDryophyllum(DryophyllumVariant.TYPE_2, 13, 19)
                );
            case GINKGO:
                return new WorldGenTreeGinkgo(10, 13);
        }
        return null;
    }

}
