package dev.louis.nebula.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.LongArgumentType;
import dev.louis.nebula.api.mana.manager.ServerManaManager;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.ApiStatus;

import java.util.Collection;
import java.util.List;

import static net.minecraft.command.argument.EntityArgumentType.*;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

@ApiStatus.Internal
public class NebulaCommand {
    public static void init() {
        CommandRegistrationCallback.EVENT.register(NebulaCommand::register);
    }

    private static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment) {
        var command = literal("nebula").requires(source -> source.hasPermissionLevel(4))
                .then(literal("getMana")
                        .executes(context -> getMana(context.getSource()))
                        .then(argument("players", players())
                                .executes(context -> getMana(context.getSource(), getPlayers(context, "players")))))
                .then(literal("insertMana")
                        .then(argument("entities", entities())
                                .then(argument("insertion", LongArgumentType.longArg(0))
                                        .executes(context -> addMana(
                                                context.getSource(),
                                                getEntities(context, "entities"),
                                                LongArgumentType.getLong(context, "insertion"))
                                        )
                                )
                        )
                )
                .then(literal("extractMana")
                        .then(argument("entities", entities())
                                .then(argument("extraction", LongArgumentType.longArg(0))
                                        .executes(context -> extractMana(
                                                context.getSource(),
                                                getEntities(context, "entities"),
                                                LongArgumentType.getLong(context, "extraction"))
                                        )
                                )
                        )
                );;


        dispatcher.register(command);
    }

    private static int addMana(ServerCommandSource source, Collection<? extends Entity> entities, long mana) {
        entities.stream().filter(LivingEntity.class::isInstance).map(LivingEntity.class::cast).forEach(livingEntity -> {
            try(Transaction transaction = Transaction.openOuter()) {
                var insertion = ((ServerManaManager) livingEntity.getManaManager()).insertMana(mana, transaction);
                source.sendMessage(Text.of("Inserted " + insertion + " mana into " + livingEntity.getName().getString() + "."));
                transaction.commit();
            }
        });

        return 1;
    }

    private static int extractMana(ServerCommandSource source, Collection<? extends Entity> entities, long mana) {
        entities.stream().filter(LivingEntity.class::isInstance).map(LivingEntity.class::cast).forEach(livingEntity -> {
            try(Transaction transaction = Transaction.openOuter()) {
                var insertion = ((ServerManaManager) livingEntity.getManaManager()).extractMana(mana, transaction);
                source.sendMessage(Text.of("Extracted " + insertion + " mana from " + livingEntity.getName().getString() + "."));
                transaction.commit();
            }
        });

        return 1;
    }

    private static int getMana(ServerCommandSource source) {
        if(source.getPlayer() != null) {
            return getMana(source, List.of(source.getPlayer()));
        }
        return 0;
    }

    private static int getMana(ServerCommandSource source, Collection<ServerPlayerEntity> players) {
        for (ServerPlayerEntity player : players) {
            source.sendMessage(Text.of(player.getName().getString() + " has " + source.getPlayer().getManaManager().getMana() + " mana."));
        }
        return 1;
    }
}
