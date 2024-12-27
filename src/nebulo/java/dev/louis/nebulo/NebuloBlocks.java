package dev.louis.nebulo;

import dev.louis.nebulo.block.ManaExtractorBlock;
import dev.louis.nebulo.block.entity.ManaExtractorBlockEntity;
import dev.louis.nebulo.client.block.entity.renderer.ManaExtractorBlockEntityRenderer;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

import java.util.function.Function;

public class NebuloBlocks {
    public static final Block MANA_EXTRACTOR =
            register(
                    "mana_extractor",
                    ManaExtractorBlock::new,
                    AbstractBlock.Settings.create().nonOpaque().dropsNothing().hardness(-1)
            );

    public static Block register(String id, AbstractBlock.Settings settings) {
        return Blocks.register(RegistryKey.of(Registries.BLOCK.getKey(), Identifier.of(Nebulo.MOD_ID, id)), settings);
    }

    private static Block register(String id, Function<AbstractBlock.Settings, Block> factory, AbstractBlock.Settings settings) {
        return Blocks.register(RegistryKey.of(Registries.BLOCK.getKey(), Identifier.of(Nebulo.MOD_ID, id)), factory, settings);
    }

    public static void init() {

    }
}
