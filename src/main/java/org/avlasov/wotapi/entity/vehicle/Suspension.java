package org.avlasov.wotapi.entity.vehicle;

import lombok.Data;

@Data
public class Suspension {

    private int loadLimit;
    private String name;
    private int steeringLockAngle;
    private String tag;
    private int tier;
    private int traverseSpeed;
    private int weight;

}
