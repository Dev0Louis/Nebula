package dev.louis.nebulo;

import dev.louis.nebulo.block.ManaExtractorBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

public class NebuloBlocks {
    public static final Block MANA_EXTRACTOR =
            register("mana_extractor", AbstractBlock.Settings.create().nonOpaque().dropsNothing().hardness(-1));

    public static Block register(String id, AbstractBlock.Settings settings) {
        return Blocks.register(RegistryKey.of(Registries.BLOCK.getKey(), Identifier.of(Nebulo.MOD_ID, id)), settings);
    }

    public static void init() {

    }
}
