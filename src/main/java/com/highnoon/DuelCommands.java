package com.highnoon;

// literal("foo")
import static net.minecraft.server.command.CommandManager.literal;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;

import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
// argument("bar", word())
import static net.minecraft.server.command.CommandManager.argument;


public class DuelCommands {
  
  public static void register(CommandDispatcher<ServerCommandSource> disp,
        CommandRegistryAccess commandRegistryAccess,
        CommandManager.RegistrationEnvironment registrationEnvironment) {
    LiteralCommandNode<ServerCommandSource> root = disp.register(
      literal("duel")
      // /duel <player>
      .then(argument("target", EntityArgumentType.player())
        .executes(ctx -> DuelManager.challenge(ctx.getSource(), EntityArgumentType.getPlayer(ctx, "target")))
        .then(literal("wager")
          .then(argument("levels", IntegerArgumentType.integer(1, 10))
            .executes(ctx -> DuelManager.challengeWithWager(ctx.getSource(), EntityArgumentType.getPlayer(ctx, "target"), IntegerArgumentType.getInteger(ctx, "wager")))
          )
        )
      )
      // /duel accept
      .then(literal("accept")
        .executes(ctx -> DuelManager.acceptChallenge(ctx, ctx.getSource().getPlayer()) )
      )
      // /duel reject
      .then(literal("reject"))
        .executes(ctx -> DuelManager.rejectChallenge(ctx, ctx.getSource().getPlayer()))
      // /duel stats [player]
      .then(literal("stats")
        .executes(ctx -> StatsManager.showStats(ctx, ctx.getSource().getPlayer()))
        .then(argument("player", EntityArgumentType.player())
          .executes(ctx -> StatsManager.showStats(ctx, EntityArgumentType.getPlayer(ctx, "player")))
        )
      )
    );
  }
}
