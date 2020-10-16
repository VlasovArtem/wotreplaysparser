package org.avlasov.statistic.entity.data;

import lombok.Data;

@Data
public class ReplayStatisticArenaInfo {

    private int battles;
    private int wins;
    private int lose;
    private int damageDealt;
    private int frags;
    private int xp;
    private int spotted;
    private int damageAssisted;
    private int droppedCapturePoints;

}
