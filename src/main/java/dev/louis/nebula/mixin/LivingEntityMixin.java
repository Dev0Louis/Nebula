package dev.louis.nebula.mixin;

import dev.louis.nebula.Nebula;
import dev.louis.nebula.api.mana.ManaPool;
import dev.louis.nebula.api.mana.holder.ManaPoolHolder;
import dev.louis.nebula.api.spell.SpellSource;
import dev.louis.nebula.api.spell.TickingSpell;
import dev.louis.nebula.api.spell.TickingSpellType;
import dev.louis.nebula.mana.InternalManaManagerHolder;
import dev.louis.nebula.mana.NebulaManaManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtOps;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Debug(export = true)
@SuppressWarnings("AddedMixinMembersNamePattern")
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements InternalManaManagerHolder, ManaPoolHolder {
    @Unique
    private static final String TICKING_SPELLS = "TickingSpells";
    @Unique
    private static final String SPELL_ID = "SpellId";

    protected LivingEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Unique
    protected NebulaManaManager manaManager;
    protected List<TickingSpell<SpellSource<?>>> tickingSpells = new ArrayList<>();

    @Inject(
            method = "<init>",
            at = @At(value = "FIELD", target = "Lnet/minecraft/entity/LivingEntity;attributes:Lnet/minecraft/entity/attribute/AttributeContainer;", shift = At.Shift.AFTER)
    )
    public void lateManaManagerInit(EntityType<?> entityType, World world, CallbackInfo ci) {
        manaManager = Nebula.createManaManager((LivingEntity) (Object) this);
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("RETURN"))
    public void writeManaAndSpellToNbt(NbtCompound nbt, CallbackInfo ci) {
        this.manaManager.writeNbt(nbt);
        NbtCompound nebulaNbt = nbt.getCompound(Nebula.MOD_ID);

        var spellNbtList = new NbtList();
        for (var tickingSpell : tickingSpells) {
            NbtCompound spellNbt = new NbtCompound();
            spellNbt.putString(SPELL_ID, tickingSpell.getType().id().toString());
            tickingSpell.getType().encoder().accept(tickingSpell, spellNbt);
            spellNbtList.add(spellNbt);
        }

        nebulaNbt.put(TICKING_SPELLS, spellNbtList);
        nbt.put(Nebula.MOD_ID, nebulaNbt);
    }

    @Inject(method = "readCustomDataFromNbt",at = @At("RETURN"))
    public void readManaAndSpellToNbt(NbtCompound nbt, CallbackInfo ci) {
        this.manaManager.readNbt(nbt);

        NbtCompound nebulaNbt = nbt.getCompound(Nebula.MOD_ID);

        var spellNbtList = nebulaNbt.getList(TICKING_SPELLS, NbtElement.END_TYPE);
        spellNbtList.stream().map(nbtElement -> {
            TickingSpellType.REGISTRY.getOrEmpty(Identifier.tryParse(nebulaNbt.getString(SPELL_ID))).ifPresent(spellType -> {
                spellType.codec()
                        .parse(NbtOps.INSTANCE, nebulaNbt.get(SPELL_DATA))
                        .resultOrPartial(Nebula.LOGGER::error)
                        .ifPresent(spell -> {
                            this.tickingSpells.add((TickingSpell<SpellSource<?>>) spell);
                        });
            });
        })
    }

    @Inject(method = "tick", at = @At("RETURN"))
    public void tickManaAndSpellManager(CallbackInfo ci) {
        this.manaManager.tick();
    }

    @Override
    public @NotNull NebulaManaManager getManaManager() {
        return this.manaManager;
    }

    @Override
    public @NotNull ManaPool getManaPool() {
        return this.getManaManager();
    }
}
