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
import org.jetbrains.annotations.Nullable;

public abstract class SpellBookItem extends Item {
    public SpellBookItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand) {
        ItemStack itemStack = playerEntity.getStackInHand(hand);
        Identifier spell = getSpell(itemStack);
        if (hand == Hand.MAIN_HAND && spell != null) {
            if (playerEntity instanceof NebulaPlayer nebulaPlayer) {
                if (SpellKnowledge.Registry.isRegistered(spell)) {
                    itemStack.setCount(itemStack.getCount() - 1);
                    nebulaPlayer.getSpellKnowledge().addCastableSpell(spell);
                    return TypedActionResult.consume(itemStack);
                }
            }
        }
        return TypedActionResult.fail(itemStack);
    }



    @Nullable
    public Identifier getSpell(ItemStack itemStack) {
        if(itemStack.getNbt() == null)return null;
        if(!itemStack.getNbt().contains("spell"))return null;
        return Identifier.tryParse(itemStack.getNbt().getString("spell"));
    }
}