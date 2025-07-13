package com.highnoon;

import com.mojang.brigadier.context.CommandContext;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.TeleportTarget;

public class DuelSession {    
    /** Initiator of challenge */
    private ServerPlayerEntity challenger;
    /** A challenged individual */
    private ServerPlayerEntity challenged;

    public ServerPlayerEntity loser;
    public ServerPlayerEntity winner;

    /** Where challenger last was when duel was accepted. */
    private Vec3d challengerOrigin;
    private float challengerHealth;
    private Vec3d challengedOrigin;
    private float challengedHealth;

    private Vec3d challengerSpawn;
    private Vec3d challengedSpawn;

    private int ticksLeft = -1;
    private CommandContext<ServerCommandSource> context;

    private Vec3d arenaPosition;

    private int levelsWaged = 0;

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

    public void addWager(int levels) {
        levelsWaged = levels;
    }

    public int getWager() { return levelsWaged; }

    private void recordOriginalPositions() {
        challengerOrigin = challenger.getPos();
        challengedOrigin = challenged.getPos();
        challengerHealth = challenger.getHealth();
        challengedHealth = challenged.getHealth();
        // Record inventory items
        // challengerInventory = new ItemStack[challenger.getInventory().size()];
        // challengedInventory = new ItemStack[challenged.getInventory().size()];
        // for (int i = 0; i < challengerInventory.length; i++) {
        //     challengerInventory[i] = challenger.getInventory().getStack(i).copy();
        // }
        // for (int i = 0; i < challengedInventory.length; i++) {
        //     challengedInventory[i] = challenged.getInventory().getStack(i).copy();
        // }


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
        challenger.teleportTo(new TeleportTarget(context.getSource().getWorld(), challengerSpawn, new Vec3d(0,0,0), 0f, 0f, null));
        challenged.teleportTo(new TeleportTarget(context.getSource().getWorld(), challengedSpawn, new Vec3d(0,0,0), 0f, 0f, null));
        ticksLeft = 21600; // 180 * 20 for 3 minutes
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
        
        // for (int i = 0; i < challengerInventory.length; i++) {
        //     challenger.getInventory().setStack(i, challengerInventory[i]);
        // }
        // for (int i = 0; i < challengedInventory.length; i++) {
        //     challenged.getInventory().setStack(i, challengedInventory[i]);
        // }

    }

    public void onLoss(ServerPlayerEntity _loser) {
        loser = _loser;
        winner = loser.equals(challenger) ? challenged : challenger;
        context.getSource().getServer().getPlayerManager().broadcast(
            Text.literal(winner.getName().getString() + " won the duel. " + loser.getName().getString() + " is a loser!"), false);
        
        // Restore original coordinates and inventory
        restore();

        // Give the winner a wager and take it from the loser
        if (levelsWaged != 0) {
            winner.setExperienceLevel(winner.experienceLevel + levelsWaged);
            loser.setExperienceLevel(loser.experienceLevel - levelsWaged);
        }
        ArenaManager.clearArena(context, this);

        // Add stats for win and loss
        StatsManager.addWin(winner);
        StatsManager.addLoss(loser);
    }

    public void endDraw() {
        winner = challenger;
        loser = challenged;
        StatsManager.addDraw(challenger);
        StatsManager.addDraw(challenged);
        // Restore original coordinates and inventory, and add a draw to each player's stats.
        restore();
        ArenaManager.clearArena(context, this);

        context.getSource().getServer().getPlayerManager().broadcast(
            Text.literal(challenger.getName().getString() + " and " + challenged.getName().getString() + " drew their duel"), false);
    }

    public void setArenaLocation(Vec3d northwestCorner) {
        arenaPosition = northwestCorner;
    }

    public Vec3d getArenaLocation() {
        return arenaPosition;
    }

}
