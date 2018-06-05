package org.avlasov.utils;

import org.avlasov.config.entity.PlatoonConfig;
import org.avlasov.entity.match.Platoon;
import org.avlasov.entity.match.Player;
import org.avlasov.entity.match.PlayerMatch;
import org.avlasov.entity.match.enums.DrawGroup;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Created By artemvlasov on 21/05/2018
 **/
@Component
public class DataUtils {

    private final PlatoonConfig PLATOON_CONFIG;

    public DataUtils(PlatoonConfig PLATOON_CONFIG) {
        this.PLATOON_CONFIG = PLATOON_CONFIG;
    }

    public DrawGroup getDrawGroup(PlayerMatch playerMatch) {
        return getDrawGroup(playerMatch.getPlayer().getName());
    }

    public DrawGroup getDrawGroup(String username) {
        Optional<Player> platoonPlayer = getPlayer(username);
        return platoonPlayer
                .map(Player::getDrawGroup)
                .orElse(DrawGroup.FIRST);
    }

    public Optional<Platoon> getPlatoonDataFromUser(PlayerMatch playerMatch) {
        return getPlatoonDataFromUser(playerMatch.getPlayer().getName());
    }

    public Optional<Platoon> getPlatoonDataFromUser(String name) {
        List<Platoon> platoonDataList = PLATOON_CONFIG.getPlatoons();
        for (Platoon platoon : platoonDataList) {
            if (platoon.getPlayers().stream().anyMatch(player -> name.contains(player.getName())))
                return Optional.of(platoon);
        }
        return Optional.empty();
    }

    public Optional<Player> getPlayer(PlayerMatch playerMatch) {
        return getPlayer(playerMatch.getPlayer().getName());
    }

    public Optional<Player> getPlayer(String name) {
        Optional<Platoon> platoonDataFromUser = getPlatoonDataFromUser(name);
        if (platoonDataFromUser.isPresent()) {
            return platoonDataFromUser.get().getPlayers()
                    .stream()
                    .filter(player -> name.contains(player.getName()))
                    .findFirst();
        }
        return Optional.empty();
    }

    public String getPlatoonName(PlayerMatch playerMatch) {
        return getPlatoonName(playerMatch.getPlayer().getName());
    }

    public String getPlatoonName(String name) {
        Optional<Platoon> platoonDataFromUser = getPlatoonDataFromUser(name);
        return platoonDataFromUser.
                map(Platoon::getPlatoonName)
                .orElse("");
    }

}
