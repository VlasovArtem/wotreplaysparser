package org.avlasov.statistic.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.avlasov.wotapi.enums.VehicleType;

@EqualsAndHashCode(callSuper = true)
@Data
public class ArenasStatisticByVehicleType extends AbstractArenasStatistic {

    private VehicleType vehicleType;

}
