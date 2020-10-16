package org.avlasov.wotapi.entity.vehicle;

import lombok.Data;

@Data
//    Характеристики машин в скоростном режиме (для колёсной техники)
public class Rapid {

    private int speedBackward;
    private int speedForward;
    private int suspensionSteeringLockAngle;
    private float switchOffTime;
    private float switchOnTime;

}
