package dev.louis.nebulo;

import dev.louis.nebulo.block.entity.ManaExtractorBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class NebuloBlockEntities {
    public static final BlockEntityType<ManaExtractorBlockEntity> MANA_EXTRACTOR =
            register("mana_extractor", FabricBlockEntityTypeBuilder.create(ManaExtractorBlockEntity::new, NebuloBlocks.MANA_EXTRACTOR));

    private static <T extends BlockEntity> BlockEntityType<T> register(String id, FabricBlockEntityTypeBuilder<T> type) {
        return Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of(Nebulo.MOD_ID, id), type.build());
    }
    public static void init() {

    }
}
