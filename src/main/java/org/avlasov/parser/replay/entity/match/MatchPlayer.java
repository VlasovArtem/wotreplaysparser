package org.avlasov.parser.replay.entity.match;

import lombok.Data;

@Data
public class MatchPlayer {

    private String name;
    //Platoon ID
    private int prebattleID;
    private int igrType;
    private String clanAbbrev;
    private int team;
    private int clanDBID;

}
