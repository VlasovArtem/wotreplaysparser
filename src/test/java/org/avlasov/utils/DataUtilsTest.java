package org.avlasov.utils;

import org.avlasov.config.entity.PlatoonConfig;
import org.avlasov.entity.match.Platoon;
import org.avlasov.entity.match.Player;
import org.avlasov.entity.match.enums.DrawGroup;
import org.avlasov.test.TestEntitiesCreator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Collections;
import java.util.Optional;

import static org.avlasov.test.TestEntitiesCreator.getPlatoon;
import static org.avlasov.test.TestEntitiesCreator.getBestPlayerMatch;
import static org.junit.Assert.*;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Created By artemvlasov on 09/06/2018
 **/
@RunWith(PowerMockRunner.class)
@PrepareForTest(DataUtils.class)
public class DataUtilsTest {

    @Mock
    private PlatoonConfig platoonConfig;
    @InjectMocks
    private DataUtils dataUtils;

    @Before
    public void setUp() throws Exception {
        when(platoonConfig.getPlatoons()).thenReturn(Collections.singletonList(getPlatoon()));
    }

    @Test
    public void getDrawGroup_WithExistingName_ReturnDrawGroup() {
        DrawGroup drawGroup = dataUtils.getDrawGroup("test name");
        assertNotNull(drawGroup);
        assertEquals(DrawGroup.FIRST, drawGroup);
    }

    @Test
    public void getDrawGroup_WithNotExistingName_ReturnDrawGroup() {
        DrawGroup drawGroup = dataUtils.getDrawGroup("not existing name");
        assertNotNull(drawGroup);
        assertEquals(DrawGroup.FIRST, drawGroup);
    }

    @Test
    public void getDrawGroup_WithPlayerMatch_ReturnDrawGroup() {
        DrawGroup drawGroup = dataUtils.getDrawGroup(getBestPlayerMatch(DrawGroup.FIRST));
        assertNotNull(drawGroup);
        assertEquals(DrawGroup.FIRST, drawGroup);
    }

    @Test
    public void getPlatoonDataFromUser_WithExistingName_ReturnPlatoon() {
        Optional<Platoon> dataPlatoon = dataUtils.getPlatoonFromPlayer("test name");
        assertTrue(dataPlatoon.isPresent());
        assertEquals(getPlatoon(), dataPlatoon.get());
    }

    @Test
    public void getPlatoonDataFromUser_WithEmptyPlatoons_ReturnOptionalEmpty() {
        when(platoonConfig.getPlatoons()).thenReturn(Collections.emptyList());
        Optional<Platoon> dataPlatoon = dataUtils.getPlatoonFromPlayer("test name");
        assertFalse(dataPlatoon.isPresent());
    }

    @Test
    public void getPlatoonDataFromUser_WithNotMatchingUser_ReturnOptionalEmpty() {
        when(platoonConfig.getPlatoons()).thenReturn(Collections.emptyList());
        Optional<Platoon> dataPlatoon = dataUtils.getPlatoonFromPlayer("not existing user");
        assertFalse(dataPlatoon.isPresent());
    }

    @Test
    public void getPlatoonDataFromUser_WithPlayerMatch_ReturnPlatoon() {
        Optional<Platoon> platoonDataFromUser = dataUtils.getPlatoonFromPlayer(getBestPlayerMatch(DrawGroup.FIRST));
        assertTrue(platoonDataFromUser.isPresent());
        assertEquals(getPlatoon(), platoonDataFromUser.get());
    }

    @Test
    public void getPlayer_WithExistingName_ReturnPlayer() {
        Optional<Player> platoonPlayer = dataUtils.getPlayerFromPlatoon("test name");
        assertTrue(platoonPlayer.isPresent());
        assertEquals(TestEntitiesCreator.getPlayer(DrawGroup.FIRST), platoonPlayer.get());
    }

    @Test
    public void getPlayer_WithNotExistingPlatoon_ReturnEmptyOptional() {
        Optional<Player> platoonPlayer = dataUtils.getPlayerFromPlatoon("not existing user");
        assertFalse(platoonPlayer.isPresent());
    }

    @Test
    public void getPlayer_WithPlayerMatch_ReturnPlayer() {
        Optional<Player> playerFromPlatoon = dataUtils.getPlayerFromPlatoon(getBestPlayerMatch(DrawGroup.FIRST));
        assertTrue(playerFromPlatoon.isPresent());
        assertEquals(TestEntitiesCreator.getPlayer(DrawGroup.FIRST), playerFromPlatoon.get());
    }

    @Test
    public void getPlatoonName_WithValidData_ReturnPlatoonName() {
        String platoonName = dataUtils.getPlatoonName("test name");
        assertNotNull(platoonName);
        assertEquals("Test", platoonName);
    }

    @Test
    public void getPlatoonName_WithNotMatchingPlatoon_ReturnEmptyString() {
        String platoonName = dataUtils.getPlatoonName("not existing name");
        assertNotNull(platoonName);
        assertEquals("", platoonName);
    }

    @Test
    public void getPlatoonName_WithPlayerMatch_ReturnPlatoonName() {
        String platoonName = dataUtils.getPlatoonName(getBestPlayerMatch(DrawGroup.FIRST));
        assertNotNull(platoonName);
        assertEquals("Test", platoonName);
    }
}