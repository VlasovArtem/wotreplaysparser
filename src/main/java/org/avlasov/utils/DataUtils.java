package org.avlasov.utils;

import org.avlasov.config.PlatoonConfig;
import org.avlasov.config.PlatoonConfigReader;
import org.avlasov.config.entity.PlatoonData;
import org.avlasov.entity.match.Player;
import org.avlasov.entity.match.PlayerMatch;
import org.avlasov.entity.match.enums.DrawGroup;

import java.util.List;
import java.util.Optional;

/**
 * Created By artemvlasov on 21/05/2018
 **/
public class DataUtils {

    private final static PlatoonConfig PLATOON_CONFIG;

    static {
        PLATOON_CONFIG = new PlatoonConfigReader().readData();
    }

    public static DrawGroup getDrawGroup(PlayerMatch playerMatch) {
        return getDrawGroup(playerMatch.getPlayer().getName());
    }

    public static DrawGroup getDrawGroup(String username) {
        Optional<Player> platoonPlayer = getPlayer(username);
        return platoonPlayer
                .map(Player::getDrawGroup)
                .orElse(DrawGroup.FIRST);
    }

    public static Optional<PlatoonData> getPlatoonDataFromUser(PlayerMatch playerMatch) {
        return getPlatoonDataFromUser(playerMatch.getPlayer().getName());
    }

    public static Optional<PlatoonData> getPlatoonDataFromUser(String name) {
        List<PlatoonData> platoonDataList = PLATOON_CONFIG.getPlatoonDataList();
        for (PlatoonData platoonData : platoonDataList) {
            if (platoonData.getPlatoonPlayers().stream().anyMatch(player -> name.contains(player.getName())))
                return Optional.of(platoonData);
        }
        return Optional.empty();
    }

    public static Optional<Player> getPlayer(PlayerMatch playerMatch) {
        return getPlayer(playerMatch.getPlayer().getName());
    }

    public static Optional<Player> getPlayer(String name) {
        Optional<PlatoonData> platoonDataFromUser = getPlatoonDataFromUser(name);
        if (platoonDataFromUser.isPresent()) {
            return platoonDataFromUser.get().getPlatoonPlayers()
                    .stream()
                    .filter(player -> name.contains(player.getName()))
                    .findFirst();
        }
        return Optional.empty();
    }

    public static String getPlatoonName(PlayerMatch playerMatch) {
        return getPlatoonName(playerMatch.getPlayer().getName());
    }

    public static String getPlatoonName(String name) {
        Optional<PlatoonData> platoonDataFromUser = getPlatoonDataFromUser(name);
        return platoonDataFromUser.
                map(PlatoonData::getPlatoonName)
                .orElse("");
    }

}
