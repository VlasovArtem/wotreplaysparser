package org.avlasov.utils.statistic;

import org.avlasov.entity.match.Match;
import org.avlasov.entity.match.enums.DrawGroup;
import org.avlasov.entity.statistic.DrawGroupPlayerStatistic;
import org.avlasov.entity.statistic.PlayerStatistic;
import org.hamcrest.collection.IsCollectionWithSize;
import org.junit.Test;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.avlasov.entity.match.enums.DrawGroup.FIRST;
import static org.avlasov.test.TestEntitiesCreator.*;
import static org.junit.Assert.*;

/**
 * Created By artemvlasov on 10/06/2018
 **/
public class DrawGroupPlayerStatisticUtilsTest {

    private DrawGroupPlayerStatisticUtils drawGroupPlayerStatisticUtils;

    public DrawGroupPlayerStatisticUtilsTest() {
        drawGroupPlayerStatisticUtils = new DrawGroupPlayerStatisticUtils();
    }

    @Test
    public void collectDrawGroupPlayers_WithValidData_ReturnMap() {
        Map<DrawGroup, List<DrawGroupPlayerStatistic>> drawGroupListMap = drawGroupPlayerStatisticUtils.collectDrawGroupPlayers(List.of(getPlayerStatistic()));
        verifyDrawGroupMap(drawGroupListMap);
    }

    @Test
    public void collectDrawGroupPlayers_WithNullPlayerStatistic_ReturnEmpty() {
        Map<DrawGroup, List<DrawGroupPlayerStatistic>> drawGroupListMap = drawGroupPlayerStatisticUtils.collectDrawGroupPlayers(null);
        assertNotNull(drawGroupListMap);
        assertTrue(drawGroupListMap.isEmpty());
    }

    @Test
    public void collectDrawGroupPlayers_WithEmptyPlayerStatistic_ReturnEmpty() {
        Map<DrawGroup, List<DrawGroupPlayerStatistic>> drawGroupListMap = drawGroupPlayerStatisticUtils.collectDrawGroupPlayers(Collections.emptyList());
        assertNotNull(drawGroupListMap);
        assertTrue(drawGroupListMap.isEmpty());
    }

    @Test
    public void findWorstDrawGroupPlayer_WithValidData_ReturnMap() {
        Map<DrawGroup, List<DrawGroupPlayerStatistic>> drawGroupListMap = drawGroupPlayerStatisticUtils.collectDrawGroupPlayers(List.of(getPlayerStatistic()));
        Map<DrawGroup, DrawGroupPlayerStatistic> worstDrawGroupPlayer = drawGroupPlayerStatisticUtils.findWorstDrawGroupPlayer(drawGroupListMap);
        assertNotNull(worstDrawGroupPlayer);
        assertFalse(worstDrawGroupPlayer.isEmpty());
        verifyDrawGroup(worstDrawGroupPlayer.get(FIRST));
    }

    @Test
    public void findWorstDrawGroupPlayer_WithNullMap_ReturnEmptyMap() {
        Map<DrawGroup, DrawGroupPlayerStatistic> worstDrawGroupPlayer = drawGroupPlayerStatisticUtils.findWorstDrawGroupPlayer(null);
        assertNotNull(worstDrawGroupPlayer);
        assertTrue(worstDrawGroupPlayer.isEmpty());
    }

    @Test
    public void findWorstDrawGroupPlayer_WithEmptyMap_ReturnEmptyMap() {
        Map<DrawGroup, DrawGroupPlayerStatistic> worstDrawGroupPlayer = drawGroupPlayerStatisticUtils.findWorstDrawGroupPlayer(Collections.emptyMap());
        assertNotNull(worstDrawGroupPlayer);
        assertTrue(worstDrawGroupPlayer.isEmpty());
    }

    @Test
    public void findBestDrawGroupPlayer_WithValidData_ReturnMap() {
        Map<DrawGroup, List<DrawGroupPlayerStatistic>> drawGroupListMap = drawGroupPlayerStatisticUtils.collectDrawGroupPlayers(List.of(getPlayerStatistic()));
        Map<DrawGroup, DrawGroupPlayerStatistic> bestDrawGroupPlayer = drawGroupPlayerStatisticUtils.findBestDrawGroupPlayer(drawGroupListMap);
        assertNotNull(bestDrawGroupPlayer);
        assertFalse(bestDrawGroupPlayer.isEmpty());
        verifyDrawGroup(bestDrawGroupPlayer.get(FIRST));
    }

    @Test
    public void findBestDrawGroupPlayer_WithNullMap_ReturnEmptyMap() {
        Map<DrawGroup, DrawGroupPlayerStatistic> worstDrawGroupPlayer = drawGroupPlayerStatisticUtils.findBestDrawGroupPlayer(null);
        assertNotNull(worstDrawGroupPlayer);
        assertTrue(worstDrawGroupPlayer.isEmpty());
    }

    @Test
    public void findBestDrawGroupPlayer_WithEmptyMap_ReturnEmptyMap() {
        Map<DrawGroup, DrawGroupPlayerStatistic> worstDrawGroupPlayer = drawGroupPlayerStatisticUtils.findBestDrawGroupPlayer(Collections.emptyMap());
        assertNotNull(worstDrawGroupPlayer);
        assertTrue(worstDrawGroupPlayer.isEmpty());
    }

    private PlayerStatistic getPlayerStatistic() {
        Match match = getBestMatch();
        return PlayerStatistic.builder()
                .matches(List.of(match))
                .platoon(getPlatoon())
                .player(getPlayer(FIRST))
                .totalDamageDealt(match.getResult().getMatchPlatoonDamageDealt())
                .totalFrags(match.getResult().getMatchPlatoonFrags())
                .totalScore(match.getResult().getMatchScore())
                .build();

    }

    private void verifyDrawGroupMap(Map<DrawGroup, List<DrawGroupPlayerStatistic>> data) {
        assertNotNull(data);
        assertEquals(1, data.size());
        List<DrawGroupPlayerStatistic> drawGroupPlayerStatistics = data.get(FIRST);
        assertNotNull(drawGroupPlayerStatistics);
        assertThat(drawGroupPlayerStatistics, IsCollectionWithSize.hasSize(1));
        verifyDrawGroup(drawGroupPlayerStatistics.get(0));
    }

    private void verifyDrawGroup(DrawGroupPlayerStatistic drawGroupPlayerStatistic) {
        assertNotNull(drawGroupPlayerStatistic);
        Match match = getBestMatch();
        assertEquals(match.getResult().getMatchPlatoonDamageDealt(), drawGroupPlayerStatistic.getTotalDamageDealt());
        assertEquals(match.getResult().getMatchPlatoonFrags(), drawGroupPlayerStatistic.getTotalFrags());
        assertEquals(match.getResult().getMatchScore(), drawGroupPlayerStatistic.getTotalScore());
    }

}