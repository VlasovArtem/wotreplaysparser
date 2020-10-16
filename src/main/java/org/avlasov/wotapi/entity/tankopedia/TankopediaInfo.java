package org.avlasov.wotapi.entity.tankopedia;

import lombok.Data;

import java.util.Map;

@Data
public class TankopediaInfo {

    private Map<String, String> vehicleCrewRoles;
    private long tanksUpdatedAt;
    private Map<String, String> languages;
    private Map<String, AchievementSection> achievementSections;
    private Map<String, String> vehicleTypes;
    private Map<String, String> vehicleNations;
    private String gameVersion;

}
