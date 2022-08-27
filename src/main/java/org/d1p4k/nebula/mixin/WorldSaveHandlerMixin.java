package org.d1p4k.nebula.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.util.Util;
import net.minecraft.world.WorldSaveHandler;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.io.File;

@Mixin(WorldSaveHandler.class)
public class WorldSaveHandlerMixin {

    @Shadow @Final private File playerDataDir;

    @Shadow @Final private static Logger LOGGER;

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void savePlayerData(PlayerEntity player) {
        try {
            NbtCompound nbtCompound = player.writeNbt(new NbtCompound());
            File file = File.createTempFile(player.getUuidAsString() + "-", ".dat", this.playerDataDir);
            NbtIo.writeCompressed(nbtCompound, file);
            File file2 = new File(this.playerDataDir, player.getUuidAsString() + ".dat");
            File file3 = new File(this.playerDataDir, player.getUuidAsString() + ".dat_old");
            Util.backupAndReplace(file2, file, file3);
        }
        catch (Exception exception) {
            LOGGER.warn("Failed to save player data for {}", player.getName().getString());
            exception.printStackTrace();
        }
    }
}
