package org.avlasov.wotapi.entity.vehicle;

import lombok.Data;

@Data
public class Ammo {

    private int[] damage;
    private int[] penetration;
    private String type;
    private Stun stun;

}
