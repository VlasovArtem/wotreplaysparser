package org.avlasov.entity.match;

import java.util.Objects;

/**
 * Created By artemvlasov on 21/05/2018
 **/

public class PlayerMatch {

    private Player player;
    private String vehicleName;
    private int damage;
    private int frags;

    public PlayerMatch() {
    }

    public PlayerMatch(Player player, String vehicleName, int damage, int frags) {
        this.player = player;
        this.vehicleName = vehicleName;
        this.damage = damage;
        this.frags = frags;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public int getFrags() {
        return frags;
    }

    public void setFrags(int frags) {
        this.frags = frags;
    }

    public String getVehicleName() {
        return vehicleName;
    }

    public void setVehicleName(String vehicleName) {
        this.vehicleName = vehicleName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PlayerMatch)) return false;
        PlayerMatch that = (PlayerMatch) o;
        return damage == that.damage &&
                frags == that.frags &&
                Objects.equals(player, that.player) &&
                Objects.equals(vehicleName, that.vehicleName);
    }

    @Override
    public int hashCode() {

        return Objects.hash(player, vehicleName, damage, frags);
    }
}
