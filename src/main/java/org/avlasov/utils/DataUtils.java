package org.avlasov.utils;

import org.avlasov.chucktournament.entity.match.Platoon;
import org.avlasov.chucktournament.entity.match.Player;
import org.avlasov.chucktournament.entity.match.PlayerMatch;
import org.avlasov.chucktournament.entity.match.enums.DrawGroup;

import java.util.Optional;

/**
 * Created By artemvlasov on 21/05/2018
 **/
public class DataUtils {

    public static DrawGroup getDrawGroup(PlayerMatch playerMatch) {
        return getDrawGroup(playerMatch.getPlayer().getName());
    }

    public static DrawGroup getDrawGroup(String username) {
//        Optional<Player> platoonPlayer = getPlayer(username);
//        return platoonPlayer
//                .map(Player::getDrawGroup)
//                .orElse(DrawGroup.FIRST);
        return null;
    }

    public static Optional<Platoon> getPlatoonDataFromUser(PlayerMatch playerMatch) {
//        return getPlatoonDataFromUser(playerMatch.getPlayer().getName());
        return null;
    }

    public static Optional<Platoon> getPlatoonDataFromUser(String name) {
//        List<PlatoonData> platoonDataList = PLATOON_CONFIG.getPlatoonDataList();
//        for (PlatoonData platoonData : platoonDataList) {
//            if (platoonData.getPlatoonPlayers().stream().anyMatch(player -> name.contains(player.getName())))
//                return Optional.of(platoonData);
//        }
//        return Optional.empty();
        return null;
    }

    public static Optional<Player> getPlayer(PlayerMatch playerMatch) {
        return getPlayer(playerMatch.getPlayer().getName());
    }

    public static Optional<Player> getPlayer(String name) {
//        Optional<Platoon> platoonDataFromUser = getPlatoonDataFromUser(name);
//        if (platoonDataFromUser.isPresent()) {
//            return platoonDataFromUser.get().getPlatoonPlayers()
//                    .stream()
//                    .filter(player -> name.contains(player.getName()))
//                    .findFirst();
//        }
//        return Optional.empty();
        return null;
    }

    public static String getPlatoonName(PlayerMatch playerMatch) {
        return getPlatoonName(playerMatch.getPlayer().getName());
    }

    public static String getPlatoonName(String name) {
//        Optional<Platoon> platoonDataFromUser = getPlatoonDataFromUser(name);
//        return platoonDataFromUser.
//                map(PlatoonData::getPlatoonName)
//                .orElse("");
        return null;
    }

}
