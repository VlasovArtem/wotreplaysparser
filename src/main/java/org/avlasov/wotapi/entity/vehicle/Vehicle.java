package org.avlasov.wotapi.entity.vehicle;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import org.avlasov.wotapi.deserializer.VehicleTypeDeserializer;
import org.avlasov.wotapi.enums.VehicleType;
import org.avlasov.wotapi.serializer.VehicleTypeSerializer;

import java.util.List;
import java.util.Map;

@Data
public class Vehicle {

    private String description;
    private int[] engines;
    private int[] guns;
    private boolean isGift;
    private boolean isPremium;
    private boolean isPremiumIgr;
    private String name;
    private String nation;
    private Object nextTanks;
    private int priceCredit;
    private int priceGold;
    private Object pricesXp;
    private int[] provisions;
    private int[] radios;
    private String shortName;
    private int[] suspensions;
    private String tag;
    private int tankId;
    private int tier;
    private int[] turrets;
    @JsonDeserialize(using = VehicleTypeDeserializer.class)
    @JsonSerialize(using = VehicleTypeSerializer.class)
    private VehicleType type;
    private List<Crew> crew;
    private DefaultProfile defaultProfile;
    private Map<String, Module> modulesTree;

}
