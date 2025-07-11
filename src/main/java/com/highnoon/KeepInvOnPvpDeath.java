package com.highnoon;

import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;

import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents.AfterRespawn;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

public class KeepInvOnPvpDeath {
    
    private static LinkedList<UUID> currentlyDeadByPlayer;
    /**
     * For when a player dies, keep inventory if killed by player
     */
    public static void onPlayerDeathProper(LivingEntity entity, DamageSource damageSource) {
        if (currentlyDeadByPlayer == null) {
            currentlyDeadByPlayer = new LinkedList<UUID>();
        }
        if (damageSource.getAttacker() instanceof ServerPlayerEntity && entity instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) entity;
            // Died to a player, so save inventroy and experience then clear inv
            // PlayerInf inf = new PlayerInf(player.getInventory(), player.experienceLevel, player.experienceProgress);
            currentlyDeadByPlayer.add(player.getUuid());
        }
    }

    public static void onRespawn(ServerPlayerEntity oldPlayer, ServerPlayerEntity newPlayer, boolean alive) {
        if (currentlyDeadByPlayer.contains(oldPlayer.getUuid()) && !alive) {
            
            oldPlayer.getInventory().clone(newPlayer.getInventory());
            newPlayer.setHealth(20.0f);
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
