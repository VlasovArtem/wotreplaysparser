package org.avlasov.entity.statistic;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Created By artemvlasov on 29/05/2018
 **/
@AllArgsConstructor
@Getter
@Setter
public abstract class AbstractStatistic {

    protected int totalDamageDealt;
    protected int totalFrags;
    protected int totalScore;

}
