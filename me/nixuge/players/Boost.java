package me.nixuge.players;

public class Boost {
    private ShootingPlayer player;

    public Boost(ShootingPlayer player) {
        this.player = player;
    }

    public boolean canBoost() {
        return false;
    }

    public void boost() {
    }
}
