package org.avlasov.config.entity;

import org.avlasov.entity.match.Player;

import java.util.List;

/**
 * Created By artemvlasov on 21/05/2018
 **/
public class PlatoonData {

    private String platoonName;
    private List<Player> platoonPlayers;

    public PlatoonData(String platoonName, List<Player> platoonPlayers) {
        this.platoonName = platoonName;
        this.platoonPlayers = platoonPlayers;
    }

    public String getPlatoonName() {
        return platoonName;
    }

    public List<Player> getPlatoonPlayers() {
        return platoonPlayers;
    }
}
