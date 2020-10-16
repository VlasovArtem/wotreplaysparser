package org.avlasov.wotapi.entity.vehicle;

import lombok.Data;

import java.util.List;

@Data
public class DefaultProfile {

    private int hp;
    private int hullHp;
    private int hullWeight;
    private int maxAmmo;
    private int maxWeight;
    private int speedBackward;
    private int speedForward;
    private int weight;
    private List<Ammo> ammo;
    private Armor armor;
    private Engine engine;
    private Gun gun;
    private Modules modules;
    private Radio radio;
    private Rapid rapid;
    private Siege siege;
    private Suspension suspension;
    private Turret turret;

}
