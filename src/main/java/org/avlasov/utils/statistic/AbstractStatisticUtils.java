package org.avlasov.utils.statistic;

import org.avlasov.entity.match.Match;

import java.util.List;

/**
 * Created By artemvlasov on 10/06/2018
 **/
public abstract class AbstractStatisticUtils<T> {

    public abstract T calculateStatistic(List<Match> matches);

}
