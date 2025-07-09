package com.highnoon;

// getString(ctx, "string")
import static com.mojang.brigadier.arguments.StringArgumentType.getString;
// word()
import static com.mojang.brigadier.arguments.StringArgumentType.word;
 // literal("foo")
import static net.minecraft.server.command.CommandManager.literal;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;

import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.ItemStackArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

// argument("bar", word())
import static net.minecraft.server.command.CommandManager.argument;
// Import everything in the CommandManager
import static net.minecraft.server.command.CommandManager.*;


public class DuelCommands {
  public static void register(CommandDispatcher<ServerCommandSource> disp,
        CommandRegistryAccess commandRegistryAccess,
        CommandManager.RegistrationEnvironment registrationEnvironment) {
    LiteralCommandNode<ServerCommandSource> root = disp.register(
      literal("duel")
      // /duel <player>
      .then(argument("target", EntityArgumentType.player())
        .executes((context) -> {
      // For versions below 1.19, replace "Text.literal" with "new LiteralText".
      // For versions below 1.20, remode "() ->" directly.
      context.getSource().sendFeedback(() -> Text.literal("Called /foo with no arguments"), false);return 1;
    })
        // .executes(ctx -> DuelManager.challenge(ctx.getSource(), EntityArgumentType.getPlayer(ctx, "target")))
        // .then(literal("wager")
        //   .then(argument("item", ItemStackArgumentType.itemStack(null))
        //     .executes(ctx -> challengeWithWager())
        //   )
        // )
      )
    //   // /duel accept
    //   .then(literal("accept")
    //     .executes(ctx -> acceptChallenge(ctx.getSource().getPlayer()))
    //   )
    //   // /duel stats [player]
    //   .then(literal("stats")
    //     .executes(ctx -> showStats(ctx.getSource().getPlayer(), null))
    //     .then(argument("player", EntityArgumentType.player())
    //       .executes(ctx -> showStats(ctx.getSource().getPlayer(), EntityArgumentType.getPlayer(ctx, "player")))
    //     )
    //   )
    //   // admin only
    //   .then(literal("admin")
    //     .requires(src -> src.hasPermissionLevel(4))
    //     .then(literal("reset").then(argument("player", EntityArgumentType.player())
    //       .executes(ctx -> StatsManager.reset(EntityArgumentType.getPlayer(ctx, "player")))
    //     ))
    //     .then(literal("setwins").then(argument("player", EntityArgumentType.player())
    //       .then(argument("count", IntegerArgumentType.integer(0))
    //         .executes(ctx -> StatsManager.setWins())
    //       )
    //     ))
    //   )
    );
  }
}
