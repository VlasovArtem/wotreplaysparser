package org.avlasov.test;

import org.avlasov.entity.match.*;
import org.avlasov.entity.match.enums.DrawGroup;
import org.avlasov.entity.match.enums.Result;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

import static org.avlasov.entity.match.enums.DrawGroup.*;
import static org.avlasov.entity.match.enums.Result.*;

/**
 * Created By artemvlasov on 09/06/2018
 **/
public class TestEntitiesCreator {

    public static Platoon getPlatoon() {
        Platoon platoon = new Platoon();
        platoon.setPlatoonName("Test");
        platoon.setPlayers(Arrays.asList(getPlayer(FIRST), getPlayer(SECOND), getPlayer(THIRD)));
        return platoon;
    }

    public static Player getPlayer(DrawGroup group) {
        Player player = new Player();
        player.setName("test name");
        player.setDrawGroup(group);
        return player;
    }

    public static PlayerMatch getBestPlayerMatch(DrawGroup group) {
        return getPlayerMatch(group, "Best Vehicle",3000, 20);
    }

    public static PlayerMatch getWorstPlayerMatch(DrawGroup group) {
        return getPlayerMatch(group, "Worst Vehicle", 3000, 5);
    }

    public static PlayerMatch getPlayerMatch(DrawGroup drawGroup, String vehicleName, int damage, int frags) {
        return new PlayerMatch(getPlayer(drawGroup), vehicleName, damage, frags);
    }

    public static MatchResult getBestMatchResult() {
        return getMatchResult(20, 3000, WIN);
    }

    public static MatchResult getWorstMatchResult() {
        return getMatchResult(5, 3000, LOSE);
    }

    public static MatchResult getMatchResult(int frags, int damageDealt, Result result) {
        return MatchResult.builder()
                .matchScore(damageDealt + frags * 300 + (WIN.equals(result) ? 3000 : 0))
                .matchPlatoonFrags(frags)
                .matchPlatoonDamageDealt(damageDealt)
                .result(result)
                .build();
    }

    public static Match getBestMatch() {
        return getMatch(getBestMatchResult(), getBestPlayerMatch(FIRST));
    }

    public static Match getWorstMatch() {
        return getMatch(getWorstMatchResult(), getWorstPlayerMatch(FIRST));
    }

    public static Match getMatch(MatchResult matchResult, PlayerMatch playerMatch) {
        return Match.builder()
                .matchLink("Test link")
                .result(matchResult)
                .platoon(getPlatoon())
                .matchDate(LocalDateTime.now())
                .matchDurationInSeconds(300)
                .mapData(new MapData("test", "testType"))
                .playerMatches(Collections.singletonList(playerMatch))
                .build();
    }

}
