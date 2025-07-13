package com.highnoon;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

public class KeepInvOnPvpDeath {
    
    private static Map<UUID, PlayerInf> currentlyDeadByPlayer = new HashMap<UUID, PlayerInf>();

    /**
     * For when a player dies, keep inventory if killed by player
     */
    public static boolean onPlayerDeathProper(LivingEntity entity, DamageSource damageSource, float dmg) {
        if (damageSource.getAttacker() instanceof ServerPlayerEntity && entity instanceof ServerPlayerEntity) {
            System.out.println("Player died!");
            ServerPlayerEntity player = (ServerPlayerEntity) entity;
            // Died to a player, so save inventroy and experience then clear inv
            // PlayerInf inf = new PlayerInf(player.getInventory(), player.experienceLevel, player.experienceProgress);
            ItemStack[] invCopy = new ItemStack[player.getInventory().size()];
            for (int i = 0; i < invCopy.length; i++) {
                invCopy[i] = player.getInventory().getStack(i).copy();
            }
            currentlyDeadByPlayer.put(player.getUuid(), new PlayerInf(invCopy, player.experienceLevel, player.experienceProgress));
        }
        return true;
    }

    public static void onRespawn(ServerPlayerEntity oldPlayer, ServerPlayerEntity newPlayer, boolean alive) {
        System.out.println(currentlyDeadByPlayer);
        PlayerInf inf = currentlyDeadByPlayer.get(oldPlayer.getUuid());
        if (inf != null && !alive) {
            System.out.println("Restoring");
            for (int i = 0; i < inf.inv.length; i++) {
                newPlayer.getInventory().setStack(i, inf.inv[i]);
            }
            newPlayer.experienceLevel = inf.experienceLevel;
            newPlayer.experienceProgress = inf.experienceProgress;
            System.out.println("Copied");
        }
    }
}

class PlayerInf {
    public ItemStack[] inv;
    public float experienceProgress;
    public int experienceLevel;

    public PlayerInf(ItemStack[] inventory, int _experienceLevel, float _experienceProgress) {
        inv = inventory;
        experienceLevel = _experienceLevel;
        experienceProgress = _experienceProgress;
    }
}
