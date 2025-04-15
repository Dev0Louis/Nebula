package dev.louis.nebulo;

import com.mojang.logging.LogUtils;
import dev.louis.nebula.api.mana.manager.ManaManagerHolder;
import dev.louis.nebula.api.event.MagicUseAllowed;
import dev.louis.nebulo.client.SpellCreator;
import dev.louis.nebulo.mana.LapisManaSource;
import dev.louis.nebulo.networking.CastSpellPayload;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import org.slf4j.Logger;

public class Nebulo implements ModInitializer {
    public static final String MOD_ID = "nebulo";
    public static final Logger LOGGER = LogUtils.getLogger();

    @Override
    public void onInitialize() {
        PayloadTypeRegistry.playC2S().register(CastSpellPayload.ID, CastSpellPayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(CastSpellPayload.ID, Nebulo::receiveSpellCast);

        NebuloSpellEffects.init();
        NebuloBlocks.init();
        NebuloBlockEntities.init();
        NebuloItems.init();
        SpellCreator.init();
        LOGGER.info("Nebulo has been initialized.");

        AttackBlockCallback.EVENT.register((playerEntity, world, hand, blockPos, direction) -> {
            if (!world.isClient()) {
                if (world.getBlockState(blockPos).isOf(Blocks.REDSTONE_BLOCK)) {
                    var manager = ManaManagerHolder.getManaManager((ServerPlayerEntity) playerEntity);
                    manager.getManaPool(LapisManaSource.TYPE)
                            .filter(pool -> pool instanceof LapisManaSource)
                            .map(pool -> (LapisManaSource) pool)
                            .ifPresent(LapisManaSource::toggleEnabled);
                }
            }
            return ActionResult.PASS;
        });

        MagicUseAllowed.EVENT.register((magicUser, world, pos) -> {
            if (world.getBlockState(BlockPos.ofFloored(pos).down(1)).getBlock().equals(Blocks.BEDROCK)) {
                if (magicUser instanceof PlayerEntity entity) {
                    entity.sendMessage(Text.of("Can't cast spells on Bedrock :>"), false);
                }
                return false;
            }
            return true;
        });
    }

    private static void receiveSpellCast(CastSpellPayload castSpellPayload, ServerPlayNetworking.Context context) {

    }
}
