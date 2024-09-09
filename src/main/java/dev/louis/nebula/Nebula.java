package dev.louis.nebula;

import com.mojang.logging.LogUtils;
import dev.louis.nebula.command.NebulaCommand;
import dev.louis.nebula.mana.NebulaManaManager;
import dev.louis.nebula.networking.s2c.play.SyncManaPayload;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@ApiStatus.Internal
public class Nebula implements ModInitializer {
    public static final String MOD_ID = "nebula";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final String MANA_NBT_KEY = "Mana";


    @Override
    public void onInitialize() {
        NebulaCommand.init();
        this.registerPacketReceivers();
        AttachmentRegistry.createPersistent()
        LOGGER.info("Nebula has been initialized.");
    }

    public void registerPacketReceivers() {
        PayloadTypeRegistry.playS2C().register(SyncManaPayload.ID, SyncManaPayload.CODEC);
    }

    /**
     * No full entity has been constructed yet.
     * See {@link dev.louis.nebula.mixin.LivingEntityMixin#lateManaManagerInit(EntityType, World, CallbackInfo)} to see what information is available and what is not.
     * @param livingEntity
     * @return
     */
    public static NebulaManaManager createManaManager(LivingEntity livingEntity) {
        return new NebulaManaManager(livingEntity);
    }
}

