package org.d1p4k.nebula.knowledge.items;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.d1p4k.nebula.api.NebulaPlayer;
import org.d1p4k.nebula.knowledge.SpellKnowledge;

public abstract class AbstractSpellBook extends Item {
    public AbstractSpellBook(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand) {
        if(hand == Hand.MAIN_HAND) {
            if(playerEntity instanceof NebulaPlayer) {
                Identifier id = getType();
                if(SpellKnowledge.Registry.isRegistered(id)) {
                    ((NebulaPlayer) playerEntity).getSpellKnowledge().addCastableSpell(id);
                    return TypedActionResult.consume(playerEntity.getStackInHand(hand));
                }
            }
        }
        return TypedActionResult.fail(playerEntity.getStackInHand(hand));
    }

    public abstract Identifier getType();
}
