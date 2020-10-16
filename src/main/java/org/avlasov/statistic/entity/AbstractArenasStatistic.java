package org.avlasov.statistic.entity;

import lombok.Data;
import org.avlasov.statistic.entity.data.StatisticArenaInfo;

import java.util.List;

@Data
public abstract class AbstractArenasStatistic {

    private List<StatisticArenaInfo> statisticArenaInfos;

}
