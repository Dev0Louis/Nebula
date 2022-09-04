package org.d1p4k.nebula.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.d1p4k.nebula.Nebula;
import org.d1p4k.nebula.api.NebulaPlayer;

import java.util.Collection;

;

public class NebluaCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
                (CommandManager.literal("Nebula")
                        .requires(NebluaCommand::hasPermissionLevel))

                        .then(CommandManager.literal("fillmana")
                                .executes(NebluaCommand::fillMana)
                                .then(CommandManager.argument("targets", EntityArgumentType.players()).executes((context) ->
                                        fillMana(EntityArgumentType.getPlayers(context, "targets"))
                                ))
                        ).then(CommandManager.literal("getmana")
                                .then(CommandManager.argument("target", EntityArgumentType.player()).executes((context) -> {
                                            EntityArgumentType.getPlayer(context, "target").sendMessage(
                                                    Text.of(
                                                            String.valueOf(((NebulaPlayer) EntityArgumentType.getPlayer(context, "target")).getMana())
                                                    )
                                            );
                                            return 1;
                                        }
                                ))

        )
        );
    }


    private static int fillMana(CommandContext<ServerCommandSource> context) {
        if(context.getSource().getPlayer() != null) {
            Nebula.LOGGER.info("No Arguments, but still giving Mana too: " + context.getSource().getPlayer().getName().getString());
            return 1;
        }else {
            context.getSource().sendFeedback(Text.of("No Arguments, so not filling up Mana"), false);
            return 0;
        }
    }

    private static int fillMana(Collection<ServerPlayerEntity> players) {
        for(ServerPlayerEntity player : players) {
            Nebula.LOGGER.info("Filling mana for " + player.getName().getString());
        }
        return 1;
    }

    private static boolean hasPermissionLevel(ServerCommandSource serverCommandSource) {
        return serverCommandSource.hasPermissionLevel(4);
    }
}
