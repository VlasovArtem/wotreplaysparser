package org.avlasov.parser.replay.entity.match;

import lombok.Data;

@Data
public class MatchCommon {

    private Object division;
    private int finishReason;
    private int guiType;
    private long arenaCreateTime;
    private Object extCommon;
    private int duration;
    private int arenaTypeID;
    private int gasAttackWinnerTeam;
    private int winnerTeam;
    private int vehLockMode;
    private int bonusType;
    private Object bots;
    private Object accountCompDescr;

}
