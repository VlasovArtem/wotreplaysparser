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

    private final PlatoonConfig platoonConfig;

    public DataUtils(PlatoonConfig platoonConfig) {
        this.platoonConfig = platoonConfig;
    }

    public DrawGroup getDrawGroup(PlayerMatch playerMatch) {
        return getDrawGroup(playerMatch.getPlayer().getName());
    }

    public DrawGroup getDrawGroup(String playerName) {
        Optional<Player> platoonPlayer = getPlayerFromPlatoon(playerName);
        return platoonPlayer
                .map(Player::getDrawGroup)
                .orElse(DrawGroup.FIRST);
    }

    public Optional<Platoon> getPlatoonFromPlayer(PlayerMatch playerMatch) {
        return getPlatoonFromPlayer(playerMatch.getPlayer().getName());
    }

    public Optional<Platoon> getPlatoonFromPlayer(String playerName) {
        List<Platoon> platoonDataList = platoonConfig.getPlatoons();
        for (Platoon platoon : platoonDataList) {
            if (platoon.getPlayers().stream().anyMatch(player -> playerName.contains(player.getName())))
                return Optional.of(platoon);
        }
        return Optional.empty();
    }

    public Optional<Player> getPlayerFromPlatoon(PlayerMatch playerMatch) {
        return getPlayerFromPlatoon(playerMatch.getPlayer().getName());
    }

    public Optional<Player> getPlayerFromPlatoon(String playerName) {
        Optional<Platoon> platoonDataFromUser = getPlatoonFromPlayer(playerName);
        if (platoonDataFromUser.isPresent()) {
            return platoonDataFromUser.get().getPlayers()
                    .stream()
                    .filter(player -> playerName.contains(player.getName()))
                    .findFirst();
        }
        return Optional.empty();
    }

    public String getPlatoonName(PlayerMatch playerMatch) {
        return getPlatoonName(playerMatch.getPlayer().getName());
    }

    public String getPlatoonName(String playerName) {
        Optional<Platoon> platoonDataFromUser = getPlatoonFromPlayer(playerName);
        return platoonDataFromUser.
                map(Platoon::getPlatoonName)
                .orElse("");
    }

}
