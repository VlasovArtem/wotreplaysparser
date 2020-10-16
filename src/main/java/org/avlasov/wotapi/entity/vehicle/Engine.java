package org.avlasov.wotapi.entity.vehicle;

import lombok.Data;

@Data
public class Engine {

    private float fireChance;
    private String name;
    private int power;
    private String tag;
    private int tier;
    private int weight;

}
