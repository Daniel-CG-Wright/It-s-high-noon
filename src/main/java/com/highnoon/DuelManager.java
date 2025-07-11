package com.highnoon;

import java.util.Map;
import java.util.HashMap;

import java.util.UUID;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;

import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents.AfterDeath;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents.AllowDeath;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.block.entity.VaultBlockEntity.Server;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
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

  public static void tick(ServerWorld world) {
    // For all sessions, tick
    for (DuelSession session : active.values()) {
      if (session.tick()) {
        // End the session with a draw
        onDuelTimeout(session);
      }
    }
  } 

  public static void challenge(ServerCommandSource src, ServerPlayerEntity target) throws IllegalStateException {
    ServerPlayerEntity challenger = src.getPlayer();
    // Assign the pending challenge to the target's uuid
    if (pending.get(target.getUuid()) != null || active.get(target.getUuid()) != null ||
      pending.get(challenger.getUuid()) != null || active.get(challenger.getUuid()) != null) {
        throw new IllegalStateException("Either you or your opponent already has a duel pending or active.");
      }

    pending.put(target.getUuid(), new DuelSession(challenger, target));
    src.sendFeedback(() -> Text.literal("Duel request sent to " + target.getName().getString()), false);
    src.getServer().getPlayerManager().broadcast((Text.literal(challenger.getName().getString() + " challenged " + target.getName().toString() + " to a duel. Type `/duel accept` to accept.")), false);
}

  public static int acceptChallenge(CommandContext<ServerCommandSource> context, ServerPlayerEntity target) throws IllegalStateException {
    DuelSession session = pending.remove(target.getUuid());
    System.out.println(session);
    if (session == null) throw new IllegalStateException("No duel pending.");
    startDuel(context, session);
    return 1;
  }

  public static int rejectChallenge(CommandContext<ServerCommandSource> context, ServerPlayerEntity target) throws IllegalStateException {
    DuelSession session = pending.remove(target.getUuid());
    context.getSource().sendFeedback(() -> Text.literal("All duels rejected."), false);
    return 1;
  }

  private static void startDuel(CommandContext<ServerCommandSource> context, DuelSession session) {
    // Assign the session to the challenger and the challenged
    active.put(session.getChallenger().getUuid(), session);
    active.put(session.getChallenged().getUuid(), session);
    ArenaManager.generateArena(session);
    session.beginBattle(context);
    context.getSource().getServer().getPlayerManager().broadcast(
      Text.literal("// DUEL START: " + session.getChallenger().getName().getString() + " vs. " + session.getChallenged().getName().getString())
      , false);
    

  }

  // On player death
  public static boolean onPlayerDeath(LivingEntity entity, DamageSource _src, float _dmg) {
    // If the player was in a duel, call the right session function.
    if (entity instanceof ServerPlayerEntity) {
      ServerPlayerEntity player = (ServerPlayerEntity) entity;
      DuelSession session = active.get(player.getUuid());
      if (session != null) {
        session.onLoss(player);
        // Remove session from active
        active.remove(session.getChallenged().getUuid());
        active.remove(session.getChallenger().getUuid());
        return false; // Don't actually kill the player
      }
    }
    return true;
  }

  private static void onDisconnect(ServerPlayNetworkHandler netPlayer) {
    DuelSession session = active.get(netPlayer.getPlayer().getUuid());
    if (session != null) {
      session.onLoss(netPlayer.getPlayer());
      active.remove(session.getChallenged().getUuid());
      active.remove(session.getChallenger().getUuid());
    }
    pending.remove(netPlayer.getPlayer().getUuid());
  }

  private static void onDuelTimeout(DuelSession session) {
    session.endDraw();
  }
}
