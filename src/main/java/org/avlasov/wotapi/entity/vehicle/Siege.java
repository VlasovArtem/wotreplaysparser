package org.avlasov.wotapi.entity.vehicle;

import lombok.Data;

@Data
//    Характеристики машин в осадном режиме
public class Siege {

    private float aimTime;
    private float dispersion;
    private int moveDownArc;
    private int moveUpArc;
    private float reloadTime;
    private int speedBackward;
    private int suspensionTraverseSpeed;
    private float switchOffTime;
    private float switchOnTime;

}
