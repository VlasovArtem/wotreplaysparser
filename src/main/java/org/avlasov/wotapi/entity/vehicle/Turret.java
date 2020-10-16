package org.avlasov.wotapi.entity.vehicle;

import lombok.Data;

@Data
public class Turret {

    private int hp;
    private String name;
    private String tag;
    private int tier;
    private int traverseLeftArc;
    private int traverseRightArc;
    private int traverseSpeed;
    private int viewRange;
    private int weight;

}
