package com.highnoon;

import com.mojang.brigadier.context.CommandContext;

import net.minecraft.block.Blocks;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class ArenaManager {
    
    public static final int ARENA_SIZE_X = 50;
    public static final int ARENA_SIZE_Z = 32;
    public static final int ARENA_SIZE_Y = 3;
    

    public static void generateArena(CommandContext<ServerCommandSource> context, DuelSession session) {
        // Get a random position between -10000 and 10000 x and z, and then put up to 1000y
        // Generate random x and z between -10000 and 10000, y between 64 and 1000
        double x = session.getChallenger().getPos().x + Math.random() * 500;
        double z = session.getChallenger().getPos().z + Math.random() * 500;

        Vec3d northwestCorner = new Vec3d(x, 298, z);
        ServerWorld world = context.getSource().getWorld();
        
        // Make an obsidian square
        for (int i = 0; i < ARENA_SIZE_X; i++) {
            for (int j = 0; j < ARENA_SIZE_Z; j++) {
                for (int k = 0; k < ARENA_SIZE_Y; k++) {
                    world.setBlockState(new BlockPos((int) northwestCorner.x + i, (int) northwestCorner.y + k, (int) northwestCorner.z + j), Blocks.STONE_BRICKS.getDefaultState());
                }
            }
        }

        // Build barrier walls 20 blocks tall around the arena
        for (int i = 0; i < ARENA_SIZE_X; i++) {
            for (int h = 1; h <= 20; h++) {
            // North wall
            world.setBlockState(new BlockPos((int) northwestCorner.x + i, (int) northwestCorner.y + h, (int) northwestCorner.z), Blocks.BARRIER.getDefaultState());
            // South wall
            world.setBlockState(new BlockPos((int) northwestCorner.x + i, (int) northwestCorner.y + h, (int) northwestCorner.z + ARENA_SIZE_Z - 1), Blocks.BARRIER.getDefaultState());
            
            }
        }

        for (int i = 0; i < ARENA_SIZE_Z; i++) {
            world.setBlockState(new BlockPos((int) northwestCorner.x + (ARENA_SIZE_X / 2), (int) northwestCorner.y + ARENA_SIZE_Y, (int) northwestCorner.z + i), Blocks.SEA_LANTERN.getDefaultState());
            for (int h = 1; h <= 20; h++) {
                // West wall
                world.setBlockState(new BlockPos((int) northwestCorner.x, (int) northwestCorner.y + h, (int) northwestCorner.z + i), Blocks.BARRIER.getDefaultState());
                // East wall
                world.setBlockState(new BlockPos((int) northwestCorner.x + ARENA_SIZE_X - 1, (int) northwestCorner.y + h, (int) northwestCorner.z + i), Blocks.BARRIER.getDefaultState());
            }
        }


        // Store the northwest corner in the session for later clearing
        session.setArenaLocation(northwestCorner);

        Vec3d challengerSpawn = new Vec3d(
            northwestCorner.x + 5,
            northwestCorner.y + ARENA_SIZE_Y + 1,
            northwestCorner.z + ARENA_SIZE_Z / 2
        );
        Vec3d challengedSpawn = new Vec3d(
            northwestCorner.x + ARENA_SIZE_X - 6,
            northwestCorner.y + 1,
            northwestCorner.z + ARENA_SIZE_Z / 2
        );

        session.saveArenaSpawnPoints(challengerSpawn, challengedSpawn);

    }

    public static void clearArena(CommandContext<ServerCommandSource> context, DuelSession session) {
        // Get the session arena location to clear
        Vec3d northwestCorner = session.getArenaLocation();
        ServerWorld world = context.getSource().getWorld();
        // Set the blocks to air to clear.
        for (int i = 0; i < ARENA_SIZE_X; i++) {
            for (int j = 0; j < ARENA_SIZE_Z; j++) {
                for (int k = 0; k < ARENA_SIZE_Y + 20; k++) {
                    world.setBlockState(new BlockPos((int) northwestCorner.x + i, (int) northwestCorner.y + k, (int) northwestCorner.z + j), Blocks.AIR.getDefaultState());
                }
            }
        }

    }
}
