package org.d1p4k.nebula.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Identifier;
import org.d1p4k.nebula.api.NebulaPlayer;
import org.d1p4k.nebula.knowledge.SpellKnowledge;
import org.d1p4k.nebula.mana.Mana;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(PlayerEntity.class)
public abstract class PlayerMixin implements NebulaPlayer {

    @Shadow public abstract void remove(Entity.RemovalReason reason);

    //Mana Start
    public Mana mana = new Mana();;



    @Inject(method = "writeCustomDataToNbt", locals = LocalCapture.CAPTURE_FAILSOFT, at = @At("TAIL"))
    public void addManaToNbtMixin(NbtCompound nbt, CallbackInfo ci) {
        nbt.putInt("Mana", mana.get());
    }

    @Inject(method = "readCustomDataFromNbt", locals = LocalCapture.CAPTURE_FAILSOFT, at = @At("TAIL"))
    public void addManaFromNbtMixin(NbtCompound nbt, CallbackInfo ci, NbtList nbtList) {
        this.setMana(nbt.getInt("Mana"));
    }

    @Override
    public int getMana() {
        return mana.get();
    }

    @Override
    public void setMana(int mana) {
        this.mana.set(mana);
    }

    public Mana getManaManager() {
        return this.mana;
    }

    public void setManaManager(Mana mana) {
        this.mana = mana;
    }

// Mana End




// Knowledge Start



    private final SpellKnowledge spellKnowledge = new SpellKnowledge((PlayerEntity) (Object) this);

    @Inject(method = "writeCustomDataToNbt", locals = LocalCapture.CAPTURE_FAILSOFT, at = @At(value = "TAIL"))
    public void addKnowledgeToNbtMixin(NbtCompound nbt, CallbackInfo ci) {
        nbt.put("NebulaSpells", this.spellKnowledge.writeNbt(new NbtList()));
    }

    @Inject(method = "readCustomDataFromNbt", locals = LocalCapture.CAPTURE_FAILSOFT, at = @At("TAIL"))
    public void addKnowledgeFromNbtMixin(NbtCompound nbt, CallbackInfo ci, NbtList nbtList) {
        NbtElement nbtStuff = nbt.get("NebulaSpells");
        if(nbtStuff != null) {
            spellKnowledge.readNbt((NbtList) nbtStuff);
        }
    }
    @Override
    public SpellKnowledge getSpellKnowledge() {
        return this.spellKnowledge;
    }

    @Override
    public List<Identifier> getCastableSpells() {
        return spellKnowledge.getCastableSpells();
    }

    @Override
    public void setCastableSpells(List<Identifier> castableSpells) {
        this.spellKnowledge.setCastableSpells(castableSpells);
    }

// Knowledge End

}
