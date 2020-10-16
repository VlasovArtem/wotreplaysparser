package org.avlasov.wotapi.enums;

import lombok.Getter;

import java.util.stream.Stream;

@Getter
public enum VehicleType {

    HEAVY_TANK("heavyTank"),
    AT_SPG("AT-SPG"),
    MEDIUM_TANK("mediumTank"),
    LIGHT_TANK("lightTank"),
    SPG("SPG");

    private String type;

    VehicleType(String type) {
        this.type = type;
    }

    public static VehicleType getVehicleType(String type) {
        return Stream.of(VehicleType.values())
                .filter(vt -> vt.type.equals(type))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Vehicle type is not found " + type));
    }

}
