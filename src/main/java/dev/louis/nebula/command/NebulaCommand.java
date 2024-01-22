package dev.louis.nebula.command;

import com.mojang.brigadier.CommandDispatcher;
import dev.louis.nebula.api.spell.SpellType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import static com.mojang.brigadier.arguments.IntegerArgumentType.getInteger;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static net.minecraft.command.argument.EntityArgumentType.getPlayer;
import static net.minecraft.command.argument.EntityArgumentType.player;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class NebulaCommand {
    public static void init() {
        CommandRegistrationCallback.EVENT.register(NebulaCommand::register);
    }

    private static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment) {
        var command = literal("nebula").requires(source -> source.hasPermissionLevel(4));
        var getManaCommand = literal("getPlayerMana").executes(context -> getMana(context.getSource()));
        var getManaWithPlayerCommand = argument("player", player())
                .executes(ctx -> getMana(ctx.getSource(), getPlayer(ctx, "player")));
        var setManaCommand = literal("setPlayerMana")
                .then(argument("player", player())
                        .then(argument("mana", integer(0))
                                .executes(context -> setMana(context.getSource(), getPlayer(context, "player"), getInteger(context, "mana")))));

        getManaCommand.then(getManaWithPlayerCommand);
        command.then(getManaCommand);
        command.then(setManaCommand);

        var learnSpellCommand = literal("learnSpell");
        SpellType.REGISTRY.forEach(spellType -> {
            learnSpellCommand.then(CommandManager.literal(spellType.getId().toString()).executes(context -> {
                if(context.getSource().isExecutedByPlayer()) {
                    context.getSource().getPlayer().getSpellManager().learnSpell(spellType);
                }
                return 0;
            }));
        });

        command.then(learnSpellCommand);

        dispatcher.register(command);
    }

    private static int setMana(ServerCommandSource source, ServerPlayerEntity player, int mana) {
        if(source.getPlayer() != null) {
            player.getManaManager().setMana(mana);
            source.sendMessage(Text.of(player.getName().getString() + " now has " + player.getManaManager().getMana() + " Mana."));
        }
        return 1;
    }

    private static int getMana(ServerCommandSource source) {
        if(source.getPlayer() != null) {
            source.sendMessage(Text.of(String.valueOf(source.getPlayer().getManaManager().getMana())));
        }
        return 1;
    }

    private static int getMana(ServerCommandSource source, ServerPlayerEntity player) {
        source.sendMessage(Text.of(String.valueOf(player.getManaManager().getMana())));
        return 1;
    }
}
