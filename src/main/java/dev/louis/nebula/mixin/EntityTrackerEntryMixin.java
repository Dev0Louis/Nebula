package dev.louis.nebula.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import dev.louis.nebula.api.spell.holder.SpellEffectHolder;
import dev.louis.nebula.networking.s2c.play.StartSpellEffectPayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.network.EntityTrackerEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(EntityTrackerEntry.class)
public class EntityTrackerEntryMixin {
    @Shadow @Final private Entity entity;

    @Inject(
            method = "startTracking",
            at = @At(value = "NEW", target = "(Ljava/lang/Iterable;)Lnet/minecraft/network/packet/s2c/play/BundleS2CPacket;")
    )
    public void sendManaPackets(ServerPlayerEntity player, CallbackInfo ci, @Local List<Packet<? super ClientPlayPacketListener>> list) {
        if (this.entity instanceof SpellEffectHolder holder) {
            holder.getSpellEffects().forEach(spellEffect -> {
                var packet = ServerPlayNetworking.createS2CPacket(new StartSpellEffectPayload(entity.getId(), spellEffect));
                list.add(packet);
            });

        }
    }
}
