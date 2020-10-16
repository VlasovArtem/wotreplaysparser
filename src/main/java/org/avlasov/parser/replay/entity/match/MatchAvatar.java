package org.avlasov.parser.replay.entity.match;

import lombok.Data;

@Data
public class MatchAvatar {

    private int avatarKills;
    private Integer[] prevAccRank;
private int avatarDamaged;
    private int totalDamaged;
    private Object ext;
    private int avatarDamageDealt;
    private Integer[] fairplayViolations;
    private Integer[] badges;

}
