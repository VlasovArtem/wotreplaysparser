package org.avlasov.statistic.entity.data;

import com.googlecode.jmapper.JMapper;
import com.googlecode.jmapper.annotations.JGlobalMap;
import lombok.Data;
import org.avlasov.wotapi.entity.vehicle.Vehicle;

@Data
@JGlobalMap(excluded = {"TO_STATISTIC_ENTITY"})
public class StatisticVehicle {

    public static JMapper<StatisticVehicle, Vehicle> TO_STATISTIC_ENTITY = new JMapper<>(StatisticVehicle.class, Vehicle.class);

    private String name;
    private String nation;
    private String shortName;
    private String tag;
    private int tankId;
    private int tier;
    private String type;

}
