package dev.louis.nebulo.block;

import com.mojang.serialization.MapCodec;
import dev.louis.nebulo.NebuloBlockEntities;
import dev.louis.nebulo.block.entity.ManaExtractorBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ManaExtractorBlock extends BlockWithEntity {
    public static final MapCodec<ManaExtractorBlock> CODEC = createCodec(ManaExtractorBlock::new);

    public ManaExtractorBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ManaExtractorBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return
                world.isClient() ? null : validateTicker(
                        type,
                        NebuloBlockEntities.MANA_EXTRACTOR,
                        ((world1, pos1, state1, blockEntity) -> blockEntity.tick(world1, pos1, state1))
                );
    }
}
