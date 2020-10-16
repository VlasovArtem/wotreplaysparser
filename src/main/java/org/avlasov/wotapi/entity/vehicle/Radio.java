package org.avlasov.wotapi.entity.vehicle;

import lombok.Data;

@Data
public class Radio {

    private String name;
    private int signalRange;
    private String tag;
    private int tier;
    private int weight;

}
