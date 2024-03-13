package dev.louis.nebula.command;

import com.mojang.brigadier.CommandDispatcher;
import dev.louis.nebula.api.spell.SpellType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Collection;
import java.util.List;

import static com.mojang.brigadier.arguments.IntegerArgumentType.getInteger;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static net.minecraft.command.argument.EntityArgumentType.getPlayers;
import static net.minecraft.command.argument.EntityArgumentType.players;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class NebulaCommand {
    public static void init() {
        CommandRegistrationCallback.EVENT.register(NebulaCommand::register);
    }

    private static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment) {

        var learnSpellCommand = literal("learnSpell");
        SpellType.REGISTRY.forEach(spellType -> {
            if (!spellType.needsLearning()) return;
            learnSpellCommand.then(CommandManager.literal(spellType.getId().toString()).executes(context -> {
                if(context.getSource().isExecutedByPlayer()) {
                    learnSpell(context.getSource(), spellType);
                }
                return 0;
            }));
        });

        var command = literal("nebula").requires(source -> source.hasPermissionLevel(4))
                .then(literal("getMana")
                        .executes(context -> getMana(context.getSource()))
                        .then(argument("players", players())
                                .executes(context -> getMana(context.getSource(), getPlayers(context, "players")))))
                .then(literal("setMana")
                        .then(argument("players", players())
                                .then(argument("mana", integer(0))
                                        .executes(context -> setMana(
                                                context.getSource(),
                                                getPlayers(context, "players"),
                                                getInteger(context, "mana"))))))
                .then(learnSpellCommand);

        command.then(learnSpellCommand);

        dispatcher.register(command);
    }

    private static int setMana(ServerCommandSource source, Collection<ServerPlayerEntity> players, int mana) {
        for (ServerPlayerEntity player : players) {
            player.getManaManager().setMana(mana);
            source.sendMessage(Text.of(player.getName().getString() + " now has " + player.getManaManager().getMana() + " Mana."));
        }

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

    private static int learnSpell(ServerCommandSource source, SpellType<?> spellType) {
        if(source.getPlayer() != null) {
            source.sendMessage(Text.of("Learned spell " + spellType.getId()));
        }
        return 1;
    }
}
