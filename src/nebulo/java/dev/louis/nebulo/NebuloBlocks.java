package dev.louis.nebulo;

import dev.louis.nebulo.block.ManaExtractorBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class NebuloBlocks {
    public static final Block MANA_EXTRACTOR =
            register("mana_extractor", new ManaExtractorBlock(AbstractBlock.Settings.create().nonOpaque().dropsNothing().hardness(-1)));

    public static Block register(String id, Block block) {
        return Registry.register(Registries.BLOCK, Identifier.of(Nebulo.MOD_ID, id), block);
    }

    public static void init() {

    }
}
