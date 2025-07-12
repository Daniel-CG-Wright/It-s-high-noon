package com.highnoon;

import java.util.LinkedList;
import java.util.UUID;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

public class KeepInvOnPvpDeath {
    
    private static LinkedList<UUID> currentlyDeadByPlayer = new LinkedList<UUID>();
    /**
     * For when a player dies, keep inventory if killed by player
     */
    public static boolean onPlayerDeathProper(LivingEntity entity, DamageSource damageSource, float dmg) {
        if (damageSource.getAttacker() instanceof ServerPlayerEntity && entity instanceof ServerPlayerEntity) {
            System.out.println("Player died!");
            ServerPlayerEntity player = (ServerPlayerEntity) entity;
            // Died to a player, so save inventroy and experience then clear inv
            // PlayerInf inf = new PlayerInf(player.getInventory(), player.experienceLevel, player.experienceProgress);
            currentlyDeadByPlayer.add(player.getUuid());
        }
        return true;
    }

    public static void onRespawn(ServerPlayerEntity oldPlayer, ServerPlayerEntity newPlayer, boolean alive) {
        System.out.println(currentlyDeadByPlayer);
        if (currentlyDeadByPlayer.contains(oldPlayer.getUuid()) && !alive) {
            System.out.println("Restoring");
            newPlayer.copyFrom(oldPlayer, true);
            System.out.println("Copied");
            newPlayer.setHealth(20.0f);
            System.out.println("Done");
        }
    }
}

class PlayerInf {
    public ItemStack[] inv;
    public float experienceProgress;
    public int experienceLevel;

    public PlayerInf(PlayerInventory inventory, int _experienceLevel, float _experienceProgress) {
        for (int i = 0; i < inventory.size(); i++) {
            inv[i] = inventory.getStack(i).copy();
        }
        experienceLevel = _experienceLevel;
        experienceProgress = _experienceProgress;
    }
}
