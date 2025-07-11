package com.highnoon;

import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Supplier;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
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

    public void recordOriginalPositions() {
        challengerOrigin = challenger.getPos();
        challengedOrigin = challenged.getPos();
        challengerHealth = challenger.getHealth();
        challengedHealth = challenged.getHealth();
        // Record inventory items
        challengerInventory = new ItemStack[challenger.getInventory().size()];
        challengedInventory = new ItemStack[challenged.getInventory().size()];
        for (int i = 0; i < challengerInventory.length; i++) {
            challengerInventory[i] = challenger.getInventory().getStack(i).copy();
            System.out.println("Item: " + challengerInventory[i].getName().toString() + ", count: " + challengerInventory[i].getCount());
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
     * @param onTimeout Functiont to call when the battle timer ends.
     */
    public void beginBattle(Runnable onTimeout) {
        challenger.setPosition(challengerSpawn);
        challenged.setPosition(challengedSpawn);
        // Start a 3 minute timer
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                onTimeout.run();
            }
        }, TIMER_SECONDS * 1000L);
        
    }

    private void restore() {
        challenger.setPosition(challengerOrigin);
        challenged.setPosition(challengedOrigin);
        challenger.setHealth(challengerHealth);
        challenged.setHealth(challengedHealth);

        System.out.println("restoring");
        
        for (int i = 0; i < challengerInventory.length; i++) {
            challenger.getInventory().setStack(i, challengerInventory[i]);
            System.out.println("Item: " + challengerInventory[i].getName().toString() + ", count: " + challengerInventory[i].getCount());
        }
        for (int i = 0; i < challengedInventory.length; i++) {
            challenged.getInventory().setStack(i, challengedInventory[i]);
        }

        // TODO remove wager if appropriate here.
    }

    public void onDeath(ServerPlayerEntity loser) {
        // Restore the players, add a loss stat and a win stat.
    }

    public void endDraw() {
        // Restore original coordinates and inventory, and add a draw to each player's stats.
        restore();

    }

}
