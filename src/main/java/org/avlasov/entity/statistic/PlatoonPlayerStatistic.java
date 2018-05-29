package org.avlasov.entity.statistic;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.avlasov.entity.match.Platoon;
import org.avlasov.entity.match.Player;

/**
 * Created By artemvlasov on 29/05/2018
 * Without score for WIN or LOSE of a match
 **/
@Getter
@Setter
public class PlatoonPlayerStatistic extends AbstractStatistic {

    @JsonIgnore
    private Platoon platoon;
    private Player player;

    public PlatoonPlayerStatistic(int totalDamageDealt, int totalFrags, int totalScore) {
        super(totalDamageDealt, totalFrags, totalScore);
    }

    public PlatoonPlayerStatistic() {
        super(0, 0, 0);
    }
}
