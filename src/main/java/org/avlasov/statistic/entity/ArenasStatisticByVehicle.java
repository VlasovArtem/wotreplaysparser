package org.avlasov.statistic.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.avlasov.statistic.entity.data.StatisticVehicle;

@EqualsAndHashCode(callSuper = true)
@Data
public class ArenasStatisticByVehicle extends AbstractArenasStatistic {

    private StatisticVehicle statisticVehicle;

}
