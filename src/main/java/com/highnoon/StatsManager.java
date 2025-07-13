package com.highnoon;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import com.mojang.brigadier.context.CommandContext;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class StatsManager {
    private static final Map<String, PlayerStats> statsMap = new HashMap<>();
    private static final String FILE_NAME = "highnoon_stats.txt";

    // Loads all stats from the file
    private static void loadAllStats() {
        statsMap.clear();
        Path file = Path.of(FILE_NAME);
        if (!Files.exists(file)) {
            try {
                Files.createFile(file);
            } catch (IOException e) {
                System.err.println("Could not create stats file: " + e.getLocalizedMessage());
                return;
            }
        }
        try (BufferedReader reader = Files.newBufferedReader(file)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.trim().split("-");
                if (parts.length == 4) {
                    String username = parts[0];
                    int wins = Integer.parseInt(parts[1]);
                    int draws = Integer.parseInt(parts[2]);
                    int losses = Integer.parseInt(parts[3]);
                    PlayerStats stats = new PlayerStats();
                    stats.wins = wins;
                    stats.draws = draws;
                    stats.losses = losses;
                    statsMap.put(username, stats);
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.out.println("Error: " + e.getLocalizedMessage());
        }
    }

    // Saves all stats to the file
    private static void saveAllStats() {
        Path file = Path.of(FILE_NAME);
        if (!Files.exists(file)) {
            try {
                Files.createFile(file);
            } catch (IOException e) {
                System.err.println("Could not create stats file: " + e.getLocalizedMessage());
                return;
            }
        }
        try (BufferedWriter writer = Files.newBufferedWriter(file)) {
            for (Map.Entry<String, PlayerStats> entry : statsMap.entrySet()) {
                PlayerStats stats = entry.getValue();
                String line = entry.getKey() + "-" + stats.wins + "-" + stats.draws + "-" + stats.losses;
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error: " + e.getLocalizedMessage());
        }
    }

    // Loads stats for a player (by username)
    public static PlayerStats loadStats(ServerPlayerEntity player) {
        String username = player.getName().toString();
        if (statsMap.isEmpty()) {
            loadAllStats();
        }
        if (!statsMap.containsKey(username)) {
            statsMap.put(username, new PlayerStats());
        }
        return statsMap.get(username);
    }

    // Saves stats for a player (actually saves all stats)
    public static void saveStats(ServerPlayerEntity player) {
        saveAllStats();
    }

    public static int getWins(ServerPlayerEntity player) {
        return loadStats(player).wins;
    }

    public static int getDraws(ServerPlayerEntity player) {
        return loadStats(player).draws;
    }

    public static int getLosses(ServerPlayerEntity player) {
        return loadStats(player).losses;
    }

    public static void addWin(ServerPlayerEntity player) {
        PlayerStats stats = loadStats(player);
        stats.wins++;
        saveStats(player);
    }

    public static void addDraw(ServerPlayerEntity player) {
        PlayerStats stats = loadStats(player);
        stats.draws++;
        saveStats(player);
    }

    public static void addLoss(ServerPlayerEntity player) {
        PlayerStats stats = loadStats(player);
        stats.losses++;
        saveStats(player);
    }

    public static int showStats(CommandContext<ServerCommandSource> context, ServerPlayerEntity player) {
        PlayerStats stats = loadStats(player);
        context.getSource().sendFeedback(() ->
            Text.literal("Player stats: " + stats.wins + " wins, " + stats.draws + " draws and " + stats.losses + " losses."), false);
        return 1;
    }

    public static class PlayerStats {
        private int wins;
        private int losses;
        private int draws;

        public PlayerStats() {
            wins = 0;
            losses = 0;
            draws = 0;
        }
    }
}