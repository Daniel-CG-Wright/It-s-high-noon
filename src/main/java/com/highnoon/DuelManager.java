package com.highnoon;

import java.util.Map;
import java.util.HashMap;

import java.util.UUID;

import com.mojang.brigadier.Command;

import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;


public class DuelManager {
    /** Pending duels. */
  private static final Map<UUID, DuelSession> pending = new HashMap<>();
  /** Active duels. */
  private static final Map<UUID, DuelSession> active   = new HashMap<>();

  public static void init() {
    // listen for player death
    // ServerPlayerEvents.COPY_FROM.register(DuelManager::onPlayerDeath);
    // player disconnect
    ServerPlayConnectionEvents.DISCONNECT.register((player, server) -> onDisconnect(player));
  }

//   public static Command<ServerCommandSource> challenge(ServerCommandSource src, ServerPlayerEntity target) {
//     ServerPlayerEntity challenger = src.getPlayer();
//     src.sendFeedback(() -> Text.literal("Duel request from " + challenger.getName().getString() + " to " + target.getName().getString()), false);
//     // pending.put(target.getUuid(), new DuelSession(challenger, target));
//     // src.sendFeedback(() -> Text.literal("Duel request sent to " + target.getName().getString()), false);
//     // target.sendMessage(Text.literal(challenger.getName().getString() + " challenged you to a duel. Type `/duel accept` to accept."), false);
//     return null;
// }

//   public static void acceptChallenge(ServerPlayerEntity target) {
//     DuelSession session = pending.remove(target.getUuid());
//     if (session == null) throw new CommandException(Text.literal("No duel pending."));
//     startDuel(session);
//   }

//   private static void startDuel(DuelSession session) {
//     active.put(session.challenger.getUuid(), session);
//     active.put(session.target.getUuid(), session);
//     session.recordOriginalPositions();
//     ArenaManager.generateArena(session);
//     session.teleportToArena();
//     session.startTimer(() -> onDuelTimeout(session));
//     broadcast(Text.literal("// DUEL START: " + session.challenger.getName().getString() + " vs. " + session.target.getName().getString()));
//   }

//   // On player death inside duel
//   private static void onPlayerDeath(ServerPlayerEntity dead, ServerPlayerEntity _orig, boolean alive) {
//     DuelSession session = active.get(dead.getUuid());
//     if (session != null) {
//       session.onDeath(dead, alive);
//     }
//   }

  private static void onDisconnect(ServerPlayNetworkHandler player) {
    // DuelSession session = active.get(player.getUuid());
    // if (session != null) session.onDisconnect(player);
    
  }

//   private static void onDuelTimeout(DuelSession session) {
//     session.endDraw();
//   }
}
