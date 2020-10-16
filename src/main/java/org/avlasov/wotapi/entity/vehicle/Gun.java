package org.avlasov.wotapi.entity.vehicle;

import lombok.Data;

@Data
public class Gun {

    private float aimTime;
    private int caliber;
    private float dispersion;
    private float fireRate;
    private int moveDownRrc;
    private int moveUpArc;
    private String name;
    private float reloadTime;
    private String tag;
    private int tier;
    private int traverseSpeed;
    private int weight;

}
