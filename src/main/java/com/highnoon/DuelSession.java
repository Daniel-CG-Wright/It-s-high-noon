package com.highnoon;

import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Supplier;

import com.mojang.brigadier.context.CommandContext;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

public class DuelSession {
    public static final int TIMER_SECONDS = 180;
    
    /** Initiator of challenge */
    private ServerPlayerEntity challenger;
    /** A challenged individual */
    private ServerPlayerEntity challenged;

    private ItemStack[] challengerInventory;
    private ItemStack[] challengedInventory;

    /** Where challenger last was when duel was accepted. */
    private Vec3d challengerOrigin;
    private float challengerHealth;
    private Vec3d challengedOrigin;
    private float challengedHealth;

    private Vec3d challengerSpawn;
    private Vec3d challengedSpawn;

    private int ticksLeft = -1;
    private CommandContext<ServerCommandSource> context;

    /** when the duel is first proposed, but may not yet be accepted. */
    public DuelSession(ServerPlayerEntity challenger, ServerPlayerEntity challenged) {
        this.challenger = challenger;
        this.challenged = challenged;

    }

    public ServerPlayerEntity getChallenger() {
        return challenger;
    }

    public ServerPlayerEntity getChallenged() {
        return challenged;
    }

    private void recordOriginalPositions() {
        challengerOrigin = challenger.getPos();
        challengedOrigin = challenged.getPos();
        challengerHealth = challenger.getHealth();
        challengedHealth = challenged.getHealth();
        // Record inventory items
        challengerInventory = new ItemStack[challenger.getInventory().size()];
        challengedInventory = new ItemStack[challenged.getInventory().size()];
        for (int i = 0; i < challengerInventory.length; i++) {
            challengerInventory[i] = challenger.getInventory().getStack(i).copy();
        }
        for (int i = 0; i < challengedInventory.length; i++) {
            challengedInventory[i] = challenged.getInventory().getStack(i).copy();
        }


    }

    public void saveArenaSpawnPoints(Vec3d challengerSpawn, Vec3d challengedSpawn) {
        this.challengerSpawn = challengerSpawn;
        this.challengedSpawn = challengedSpawn;
    }

    /**
     * TP players to the spawns and then begin the battle timer.
     */
    public void beginBattle(CommandContext<ServerCommandSource> context) {
        recordOriginalPositions();
        challenger.setPosition(challengerSpawn);
        challenged.setPosition(challengedSpawn);
        ticksLeft = 3600; // 180 * 20 for 3 minutes
        this.context = context;
    }


    public boolean tick() {
        if (ticksLeft >= 0) {
            ticksLeft--;
        }
        if (ticksLeft == 0) {
            return true;
        }
        return false;
    }

    private void restore() {
        challenger.setPosition(challengerOrigin);
        challenged.setPosition(challengedOrigin);
        challenger.setHealth(challengerHealth);
        challenged.setHealth(challengedHealth);

        
        for (int i = 0; i < challengerInventory.length; i++) {
            challenger.getInventory().setStack(i, challengerInventory[i]);
        }
        for (int i = 0; i < challengedInventory.length; i++) {
            challenged.getInventory().setStack(i, challengedInventory[i]);
        }

        // TODO remove wager from inventory if appropriate here.
    }

    public void onDeath(ServerPlayerEntity loser) {
        // Restore the players, add a loss stat and a win stat.
    }

    public void endDraw() {
        // Restore original coordinates and inventory, and add a draw to each player's stats.
        restore();

        context.getSource().getServer().getPlayerManager().broadcast(
            Text.literal(challenger.getName().getString() + " and " + challenged.getName().getString() + " drew their duel"), false);
    }

}
