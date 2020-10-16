package org.avlasov.wotapi.entity.vehicle;

import lombok.Data;

@Data
public class Module {

    private boolean isDefault;
    private int moduleId;
    private String name;
    private int[] nextModules;
    private int[] nextTanks;
    private int priceCredit;
    private int priceXp;
    private String type;

}
