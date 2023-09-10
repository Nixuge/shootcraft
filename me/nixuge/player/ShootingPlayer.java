package me.nixuge.player;

import org.bukkit.entity.Player;

import lombok.Getter;
import lombok.Setter;
import me.nixuge.GameManager;
import me.nixuge.ShootCraft;
import me.nixuge.config.Config;

public class ShootingPlayer {
    private GameManager gameMgr;

    @Getter
    private Gun gun;
    @Getter
    private Boost boost;
    private RespawnManager respawnManager;

    private int currentLives;
    private int killStreak;
    @Getter
    private int totalKills;

    @Getter
    @Setter
    private boolean isProtected;

    @Getter
    private Player bukkitPlayer;
    @SuppressWarnings("unused")
    private boolean isOnline;

    public ShootingPlayer(Player bukkitPlayer) {
        this.gameMgr = ShootCraft.getInstance().getGameMgr();
        this.bukkitPlayer = bukkitPlayer;
        this.isOnline = true;
        this.gun = new Gun(this);
        this.boost = new Boost(this);
        this.respawnManager = new RespawnManager(this);
        initPlayer();
    }

    private void setMaxHealth() {
        bukkitPlayer.setMaxHealth(Config.lives.getMaxLives() * 2);
    }
 
    private void onInitOrRelog() {
        setMaxHealth();
        bukkitPlayer.getInventory().setHeldItemSlot(4);
    }

    public void initPlayer() {
        this.currentLives = Config.lives.getStartingLives();
        this.killStreak = 0;
        onInitOrRelog();
        respawnManager.initialSpawn();
    }

    public void rejoin(Player player) {
        this.bukkitPlayer = player;
        this.isOnline = true;
        
        onInitOrRelog(); // May need to be delayed 1 tick

        bukkitPlayer.getInventory().clear();

        kill();
    }

    public void leave() {
        this.bukkitPlayer = null;
        this.isOnline = false;
    }

    public void addKill() {
        this.totalKills++;
        this.killStreak++;
        if (Config.lives.getLiveOnKillStreak().contains(killStreak)) {
            int diff = Config.lives.getMaxLives() - (int)(bukkitPlayer.getHealth() / 2);
            if (diff >= 1) {
                gameMgr.broadcastGamePrefix(bukkitPlayer.getDisplayName() + " got a killstreak of " + killStreak + ". He gained a life.");
                this.currentLives++;
                bukkitPlayer.setHealth(currentLives * 2);
            } else {
                gameMgr.broadcastGamePrefix(bukkitPlayer.getDisplayName() + " got a killstreak of " + killStreak + ". He already has maxxed lives.");
            }
        }


        if (Config.game.isKfwEnabled() && Config.game.getKillsForWin() >= totalKills) {
            gameMgr.broadcastGamePrefix("§l§6GAME ENDED !");
            // TODO: end game here
        }
    }

    public void addLife() {
        if (this.currentLives >= Config.lives.getMaxLives())
            return;
        this.currentLives++;
        bukkitPlayer.setHealth(currentLives * 2);
        // TODO: add chestplate
    }


    /**
     * @param killer name of the killer
     * @return true if the hit was performed, false if the player was protected.
     */
    public boolean hit(String killer) {
        if (this.isProtected)
            return false;
            
        if (this.currentLives == 1) {
            kill();
            gameMgr.broadcastGame(bukkitPlayer.getDisplayName() + " §cgot killed by " + killer);
        } else {
            this.currentLives--;
            bukkitPlayer.setHealth(currentLives * 2);
        }
        return true;
    }

    private void kill() {
        gun.onRespawn();
        this.currentLives = Config.lives.getStartingLives();
        this.killStreak = 0;
        respawnManager.respawnAfterKill();
    }
}
