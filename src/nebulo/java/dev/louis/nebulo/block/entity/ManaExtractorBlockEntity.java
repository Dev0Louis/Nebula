package dev.louis.nebulo.block.entity;

import dev.louis.nebula.api.mana.pool.ManaPool;
import dev.louis.nebulo.NebuloBlockEntities;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

public class ManaExtractorBlockEntity extends BlockEntity {
    public ManaPool manaPool = ManaPool.createSimple(0, Integer.MAX_VALUE);

    public ManaExtractorBlockEntity(BlockPos pos, BlockState state) {
        super(NebuloBlockEntities.MANA_EXTRACTOR, pos, state);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return this.createComponentlessNbt(registryLookup);
    }

    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }


    public void tick(World world, BlockPos pos, BlockState state) {
        world.getOtherEntities(null, new Box(pos).expand(6)).stream().filter(LivingEntity.class::isInstance).map(LivingEntity.class::cast).forEach(entity -> {
            try(Transaction transaction = Transaction.openOuter()) {
                var requestedMana = 0.1f;
                var extraction = entity.getManaManager().extractMana(requestedMana, transaction);
                var hasInserted = manaPool.insertMana(extraction, transaction) > 0;
                if (hasInserted) {
                    this.markDirty();
                    this.world.updateListeners(this.getPos(), this.getCachedState(), this.getCachedState(), Block.NOTIFY_ALL);
                    transaction.commit();
                }

            }
        });
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        manaPool.writeNbt(nbt);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        manaPool.readNbt(nbt);
    }
}
