// package com.highnoon;

// import java.util.HashMap;
// import java.util.Map;
// import java.util.UUID;

// import net.minecraft.server.network.ServerPlayerEntity;
// import net.minecraft.server.world.ServerWorld;

// public class StatsManager extends SavedData {
//   private Map<UUID, PlayerStats> stats = new HashMap<>();

//   public static StatsManager get(ServerWorld world) {
//     return world.getPersistentStateManager().getOrCreate(StatsManager::new, "duel_stats");
//   }

//   public static void loadAll() {
//     // Called in onInitialize() – load for each loaded world
//   }
//   public static void saveAll() { /* call on server stop */ }

//   public static void recordWin(UUID winner, UUID loser) { /* update counts */ }
//   public static void recordDraw(UUID a, UUID b) { /* update draws */ }
//   public static PlayerStats getStats(UUID player) { /* … */ }
//   public static void reset(ServerPlayerEntity p) { /* … */ markDirty(); }
// }

// private class PlayerStats {
//     private int wins;
//     private int losses;
//     private int draws;

//     public PlayerStats() {
//         wins = 0;
//         losses = 0;
//         draws = 0;
//     }
// }