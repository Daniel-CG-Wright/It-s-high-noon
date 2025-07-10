package com.highnoon;

import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Supplier;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.PersistentState;

public class DuelSession extends PersistentState {
    public static final int TIMER_SECONDS = 180;
    
    /** Initiator of challenge */
    private ServerPlayerEntity challenger;
    /** A challenged individual */
    private ServerPlayerEntity challenged;

    private PlayerInventory challengerInventory;
    private PlayerInventory challengedInventory;

    /** Where challenger last was when duel was accepted. */
    private Vec3d challengerOrigin;
    private PlayerInventory challengerInventoryCopy;
    private double challengerHealth;
    private Vec3d challengedOrigin;
    private PlayerInventory challengedInventoryCopy;
    private double challengedHealth;

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

    @Override
    public NbtCompound writeNbt(NbtCompound nbt, WrapperLookup wrapperLookup) {
        NbtCompound 
    }

    public void recordOriginalPositions() {
        challengerOrigin = challenger.getPos();
        challengedOrigin = challenged.getPos();
        challengerHealth = challenger.getHealth();
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
        
    }

    public void onDeath(ServerPlayerEntity loser) {
        
    }

    public void endDraw() {
        // Restore original coordinates and inventory, and add a draw to each player's stats.
        

    }

}
