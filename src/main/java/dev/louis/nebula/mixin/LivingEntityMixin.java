package dev.louis.nebula.mixin;

import dev.louis.nebula.api.spell.Spell;
import dev.louis.nebula.api.spell.SpellCaster;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@SuppressWarnings("UnreachableCode")
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin<T extends LivingEntity> extends Entity implements SpellCaster {
    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Override
    public <T> void castSpell(Spell<T> spell) {

    }

    @SuppressWarnings("AddedMixinMembersNamePattern")
    //@Override
    < public void cas2tSpell(Spell<?> spell) {
        try(Transaction transaction = Transaction.openOuter()) {
            //try {
                //spell.cast((LivingEntity) (Object) this, transaction);
            //} catch (SpellException spellException) {
                //spell.fail(((LivingEntity) (Object) this));
            //}
        }
    }

    @Override
    public Vec3d getPos() {
        return super.getPos();
    }

    @Override
    public BlockPos getBlockPos() {
        return super.getBlockPos();
    }
}
