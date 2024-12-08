package dev.louis.nebulo;

import com.mojang.logging.LogUtils;
import dev.louis.nebula.api.spell.SpellSource;
import dev.louis.nebula.api.event.SpellCastEvent;
import dev.louis.nebulo.client.SpellCreator;
import dev.louis.nebulo.networking.CastSpellPayload;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.text.Text;
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

        SpellCastEvent.BEFORE.register((spellSource, spell) -> {
            if (spellSource.getWorld().getBlockState(spellSource.getBlockPos().down(1)).getBlock().equals(Blocks.BEDROCK)) {
                if (spellSource.getCaster() instanceof LivingEntity entity) {
                    entity.sendMessage(Text.of("Can't cast spells on Bedrock :>"));
                }
                return false;
            }
            return true;
        });
    }

    private static void receiveSpellCast(CastSpellPayload castSpellPayload, ServerPlayNetworking.Context context) {
        var spellCreator = SpellCreator.REGISTRY.get(castSpellPayload.spellId());

        SpellSource.of(context.player().getServerWorld(), context.player()).castSpell(spellCreator.create(context.player()));
    }
}
