package com.highnoon;

import java.util.Map;
import java.util.HashMap;

import java.util.UUID;

import com.mojang.brigadier.Command;

import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents.AfterDeath;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents.AllowDeath;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
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

  public static void challenge(ServerCommandSource src, ServerPlayerEntity target) throws IllegalStateException {
    ServerPlayerEntity challenger = src.getPlayer();
    // Assign the pending challenge to the target's uuid
    if (pending.get(target.getUuid()) != null || active.get(target.getUuid()) != null ||
      pending.get(challenger.getUuid()) != null || active.get(challenger.getUuid()) != null) {
        throw new IllegalStateException("Either you or your opponent already has a duel pending or active.");
      }

    pending.put(target.getUuid(), new DuelSession(challenger, target));
    src.sendFeedback(() -> Text.literal("Duel request sent to " + target.getName().getString()), false);
    target.sendMessage(Text.literal(challenger.getName().getString() + " challenged you to a duel. Type `/duel accept` to accept."), false);
}

  public static void acceptChallenge(ServerCommandSource source, ServerPlayerEntity target) throws IllegalStateException {
    DuelSession session = pending.remove(target.getUuid());
    if (session == null) throw new IllegalStateException("No duel pending.");
    startDuel(source, session);
  }

  private static void startDuel(ServerCommandSource source, DuelSession session) {
    // Assign the session to the challenger and the challenged
    active.put(session.getChallenger().getUuid(), session);
    active.put(session.getChallenged().getUuid(), session);
    session.recordOriginalPositions();
    ArenaManager.generateArena(session);
    session.beginBattle(() -> onDuelTimeout(session));
    source.getServer().getPlayerManager().broadcast(
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
        session.onDeath(player);
        return false; // Don't actually kill the player
      }
    }
    return true;
  }

  private static void onDisconnect(ServerPlayNetworkHandler player) {
    // DuelSession session = active.get(player.getUuid());
    // if (session != null) session.onDisconnect(player);
    
  }

  private static void onDuelTimeout(DuelSession session) {
    session.endDraw();
  }
}
