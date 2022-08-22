package org.d1p4k.nebula.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Identifier;
import org.d1p4k.nebula.knowledge.SpellKnowledge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.HashSet;
import java.util.Set;

@Mixin(PlayerEntity.class)
public class PlayerMixin {

//Mana Start
    private int Mana;

    @Inject(method = "<init>", at = @At("RETURN"))
    public void manaMixin(CallbackInfo ci) {
        Mana = 0;
    }

    @Inject(method = "writeCustomDataToNbt", locals = LocalCapture.CAPTURE_FAILSOFT , at = @At("TAIL"))
    public void addManaToNbtMixin(NbtCompound nbt, CallbackInfo ci) {
        nbt.putInt("Mana", Mana);
    }

    public int getMana() {
        return Mana;
    }

    public void setMana(int mana) {
        Mana = mana;
    }

// Mana End

// Knowledge Start
    private SpellKnowledge spellKnowledge = new SpellKnowledge((PlayerEntity) (Object) this);

    @Inject(method = "writeCustomDataToNbt", locals = LocalCapture.CAPTURE_FAILSOFT, at = @At("TAIL"))
    public void addKnowledgeToNbtMixin(NbtCompound nbt, CallbackInfo ci) {
        nbt.put("SpellKnowledge", this.spellKnowledge.writeNbt(new NbtList()));
    }

    @Inject(method = "readCustomDataFromNbt", locals = LocalCapture.CAPTURE_FAILSOFT, at = @At("TAIL"))
    public void addKnowledgeFromNbtMixin(NbtCompound nbt, CallbackInfo ci, NbtList nbtList) {
        spellKnowledge = spellKnowledge.readNbt(new NbtList());
    }

// Knowledge End

}
