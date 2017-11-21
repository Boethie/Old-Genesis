package genesis.combo.variant;

import genesis.init.GenesisBlocks;
import net.minecraft.block.Block;

public enum EnumFern {

    DRYOPTERIS(GenesisBlocks.DRYOPTERIS),
    PHLEBOPTERIS(GenesisBlocks.PHLEBOPTERIS),
    TODITES(GenesisBlocks.TODITES);

    private final Block block;

    private EnumFern(Block block) {
        this.block = block;
    }

    public Block getBlock() { return this.block; }
}
