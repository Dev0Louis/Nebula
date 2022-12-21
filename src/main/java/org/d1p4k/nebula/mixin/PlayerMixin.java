package org.d1p4k.nebula.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.d1p4k.nebula.api.NebulaPlayer;
import org.d1p4k.nebula.knowledge.SpellKnowledge;
import org.d1p4k.nebula.mana.Mana;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(PlayerEntity.class)
public abstract class PlayerMixin implements NebulaPlayer {

    //Mana Start
    public Mana manaManager;

    @Inject(method = "<init>", at = @At("TAIL"))
    public void manaInit(World world, BlockPos pos, float yaw, GameProfile gameProfile, CallbackInfo ci) {
        manaManager = new Mana(
                ((PlayerEntity) (Object) this)
        );
    }

    @Inject(method = "writeCustomDataToNbt", locals = LocalCapture.CAPTURE_FAILSOFT, at = @At("TAIL"))
    public void addManaToNbtMixin(NbtCompound nbt, CallbackInfo ci) {
        nbt.putInt("Mana", manaManager.get());
    }

    @Inject(method = "readCustomDataFromNbt", locals = LocalCapture.CAPTURE_FAILSOFT, at = @At("TAIL"))
    public void addManaFromNbtMixin(NbtCompound nbt, CallbackInfo ci, NbtList nbtList) {
        this.getManaManager().set(nbt.getInt("Mana"), false);
    }

    @Override
    public int getMana() {
        return manaManager.get();
    }

    @Override
    public void setMana(int mana) {
        this.manaManager.set(mana);
    }

    @Override
    public Mana getManaManager() {
        return this.manaManager;
    }

    @Override
    public void setManaManager(Mana mana) {
        this.manaManager = mana;
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

// Knowledge End

}
