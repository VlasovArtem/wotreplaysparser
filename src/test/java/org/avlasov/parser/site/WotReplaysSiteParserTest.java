package org.avlasov.parser.site;

import org.avlasov.entity.WotReplaysSearchReplayDetails;
import org.avlasov.entity.match.Platoon;
import org.avlasov.entity.match.Player;
import org.avlasov.entity.match.enums.DrawGroup;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.powermock.api.mockito.PowerMockito.*;

/**
 * Created By artemvlasov on 04/06/2018
 **/
@RunWith(PowerMockRunner.class)
@PrepareForTest(value = {WotReplaysSiteParser.class})
@PowerMockIgnore({"com.sun.org.apache.xerces.*", "javax.xml.parsers.*", "org.apache.logging.*"})
public class WotReplaysSiteParserTest {

    private static String contentMemberSearch;
    private static String contentPlayerSearch;
    private static String contentMemberWithMultiplsPlatoonPlayersSearch;
    @Mock
    private PhantomJSDriver phantomJSDriverMock;
    private WotReplaysSiteParser parser;
    private Platoon platoon;

    @BeforeClass
    public static void beforeMethod() throws Exception {
        try (InputStream wrms = WotReplaysSiteParserTest.class.getResourceAsStream("./wotrepalysmemberssearch.htm");
             InputStream wrps = WotReplaysSiteParserTest.class.getResourceAsStream("./wotrepalysplayerssearch.htm");
             InputStream wrmswpp = WotReplaysSiteParserTest.class.getResourceAsStream("./wotrepalysmemberssearch_with_multiple_platoon_players.htm")) {
            contentMemberSearch = new String(wrms.readAllBytes());
            contentMemberWithMultiplsPlatoonPlayersSearch = new String(wrmswpp.readAllBytes());
            contentPlayerSearch = new String(wrps.readAllBytes());
        }
    }

    @Before
    public void setUp() throws Exception {
        whenNew(PhantomJSDriver.class).withAnyArguments().thenReturn(phantomJSDriverMock);
        doNothing().when(phantomJSDriverMock).get(anyString());
        parser = new WotReplaysSiteParser();
        platoon = new Platoon();
        platoon.setPlatoonName("19+2");
        List<Player> players = new ArrayList<>();
        players.add(getPlayer("ISERVERI", DrawGroup.FIRST));
        players.add(getPlayer("19CaHTuMeTPoB", DrawGroup.SECOND));
        players.add(getPlayer("SlayerPro", DrawGroup.THIRD));
        platoon.setPlayers(players);
    }

    @Test
    public void parsePlayerReplays_WithValidData_ReturnListOfWotReplaysSearchReplayDetails() {
        when(phantomJSDriverMock.getPageSource()).thenReturn(contentPlayerSearch);
        List<WotReplaysSearchReplayDetails> iserveri = parser.parsePlayerReplays("ISERVERI", ".*Турнир Чака.*", LocalDateTime.of(2018, 5, 24, 18, 19));
        assertEquals(5, iserveri.size());
        assertTrue(iserveri.stream().allMatch(detail -> "ISERVERI".equals(detail.getReplayOwner())));
        assertTrue(iserveri.stream().anyMatch(WotReplaysSearchReplayDetails::isPossiblyCorruptedReplay));
    }

    @Test(expected = IllegalArgumentException.class)
    public void parsePlayerReplays_WithPlayerNameNull_ThrowsIllegalArgumentException() {
        parser.parsePlayerReplays(null, null, null);
    }

    @Test
    public void parsePlayerReplays_WithPlayerNameAndUploadAfter_ReturnListOfWotReplaysSearchReplayDetails() {
        when(phantomJSDriverMock.getPageSource()).thenReturn(contentPlayerSearch);
        List<WotReplaysSearchReplayDetails> iserveri = parser.parsePlayerReplays("ISERVERI", LocalDateTime.of(2018, 5, 24, 18, 20));
        assertEquals(2, iserveri.size());
        assertTrue(iserveri.stream().allMatch(detail -> "ISERVERI".equals(detail.getReplayOwner())));
        assertTrue(iserveri.stream().anyMatch(WotReplaysSearchReplayDetails::isPossiblyCorruptedReplay));
    }

    @Test(expected = IllegalArgumentException.class)
    public void parsePlayerReplays_WithPlayerNameAndNullUploadAfter_ThrowsIllegalArgumentException() {
        parser.parsePlayerReplays("ISERVERI", (LocalDateTime) null);
    }

    @Test
    public void parsePlayerReplays_WithPlayerNameAndReplayNamePattern_ReturnListOfWotReplaysSearchReplayDetails() {
        when(phantomJSDriverMock.getPageSource()).thenReturn(contentPlayerSearch);
        List<WotReplaysSearchReplayDetails> iserveri = parser.parsePlayerReplays("ISERVERI", ".*18");
        assertEquals(1, iserveri.size());
        assertTrue(iserveri.stream().allMatch(detail -> "ISERVERI".equals(detail.getReplayOwner())));
        assertTrue(iserveri.stream().noneMatch(WotReplaysSearchReplayDetails::isPossiblyCorruptedReplay));
    }

    @Test
    public void parsePlayerReplays_WithPlayerName_ReturnListOfWotReplaysSearchReplayDetails() {
        when(phantomJSDriverMock.getPageSource()).thenReturn(contentPlayerSearch);
        List<WotReplaysSearchReplayDetails> iserveri = parser.parsePlayerReplays("ISERVERI");
        assertEquals(10, iserveri.size());
        assertTrue(iserveri.stream().allMatch(detail -> "ISERVERI".equals(detail.getReplayOwner())));
        assertTrue(iserveri.stream().anyMatch(WotReplaysSearchReplayDetails::isPossiblyCorruptedReplay));
    }

    @Test(expected = IllegalArgumentException.class)
    public void parsePlayerReplays_WithPlayerNameAndNullReplayNamePattern_ThrowsIllegalArgumentException() {
        parser.parsePlayerReplays("ISERVERI", (String) null);
    }

    @Test
    public void parseMembersReplays_WithValidData_ReturnListOfWotReplaysSearchReplayDetails() {
        when(phantomJSDriverMock.getPageSource()).thenReturn(contentMemberSearch);
        List<WotReplaysSearchReplayDetails> replays = parser.parseMembersReplays(platoon, ".*Турнир Чака.*", LocalDateTime.of(2018, 5, 23, 18, 19));
        assertEquals(7, replays.size());
        assertTrue(replays.stream().noneMatch(WotReplaysSearchReplayDetails::isPossiblyCorruptedReplay));
        assertEquals("SlayerPro", replays.get(0).getReplayOwner());
    }

    @Test
    public void parseMembersReplays_WithMultiplePlatoonPlayers_ReturnListOfWotReplaysSearchReplayDetails() {
        when(phantomJSDriverMock.getPageSource()).thenReturn(contentMemberWithMultiplsPlatoonPlayersSearch);
        List<WotReplaysSearchReplayDetails> replays = parser.parseMembersReplays(platoon, ".*Турнир Чака.*", LocalDateTime.of(2018, 5, 23, 18, 19));
        assertEquals(10, replays.size());
        assertTrue(replays.stream().noneMatch(WotReplaysSearchReplayDetails::isPossiblyCorruptedReplay));
    }

    @Test
    public void parseMembersReplaysWithReplayPatternName_WithMultiplePlatoonPlayers_ReturnListOfWotReplaysSearchReplayDetails() {
        when(phantomJSDriverMock.getPageSource()).thenReturn(contentMemberWithMultiplsPlatoonPlayersSearch);
        List<WotReplaysSearchReplayDetails> replays = parser.parseMembersReplays(platoon, ".*Турнир Чака.*");
        assertEquals(10, replays.size());
        assertTrue(replays.stream().noneMatch(WotReplaysSearchReplayDetails::isPossiblyCorruptedReplay));
    }

    @Test
    public void parseMembersReplaysWithUploadAfter_WithMultiplePlatoonPlayers_ReturnListOfWotReplaysSearchReplayDetails() {
        when(phantomJSDriverMock.getPageSource()).thenReturn(contentMemberWithMultiplsPlatoonPlayersSearch);
        List<WotReplaysSearchReplayDetails> replays = parser.parseMembersReplays(platoon, LocalDateTime.of(2018, 5, 27, 0, 0));
        assertEquals(3, replays.size());
        assertTrue(replays.stream().noneMatch(WotReplaysSearchReplayDetails::isPossiblyCorruptedReplay));
        assertEquals("SlayerPro", replays.get(0).getReplayOwner());
    }

    @Test
    public void parseMembersReplaysAsOwnerMap_WithValidaData_ReturnMap() {
        when(phantomJSDriverMock.getPageSource()).thenReturn(contentMemberWithMultiplsPlatoonPlayersSearch);
        Map<String, List<WotReplaysSearchReplayDetails>> asOwnerMap = parser.parseMembersReplaysAsOwnerMap(platoon, ".*Турнир Чака.*", LocalDateTime.of(2018, 5, 23, 18, 19));
        assertEquals(2, asOwnerMap.size());
        assertNotNull(asOwnerMap.get("ISERVERI"));
        assertNotNull(asOwnerMap.get("SlayerPro"));
        assertNull(asOwnerMap.get("19CaHTuMeTPoB"));
    }

    @Test
    public void parseMembersReplaysAsOwnerMapWithReplayPatternName_WithValidaData_ReturnMap() {
        when(phantomJSDriverMock.getPageSource()).thenReturn(contentMemberWithMultiplsPlatoonPlayersSearch);
        Map<String, List<WotReplaysSearchReplayDetails>> asOwnerMap = parser.parseMembersReplaysAsOwnerMap(platoon, ".*Турнир Чака.*");
        assertEquals(2, asOwnerMap.size());
        assertNotNull(asOwnerMap.get("ISERVERI"));
        assertNotNull(asOwnerMap.get("SlayerPro"));
        assertNull(asOwnerMap.get("19CaHTuMeTPoB"));
    }

    @Test
    public void parseMembersReplaysAsOwnerMapWithUploadAfter_WithValidaData_ReturnMap() {
        when(phantomJSDriverMock.getPageSource()).thenReturn(contentMemberWithMultiplsPlatoonPlayersSearch);
        Map<String, List<WotReplaysSearchReplayDetails>> asOwnerMap = parser.parseMembersReplaysAsOwnerMap(platoon, LocalDateTime.of(2018, 5, 27, 0, 0));
        assertEquals(1, asOwnerMap.size());
        assertNull(asOwnerMap.get("ISERVERI"));
        assertNotNull(asOwnerMap.get("SlayerPro"));
        assertNull(asOwnerMap.get("19CaHTuMeTPoB"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseMembersReplaysAsOwnerMapWithUploadAfter_WithNullUploadAfter_ThrowsIllegalArgumentException() {
        parser.parseMembersReplaysAsOwnerMap(platoon, (LocalDateTime) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseMembersReplaysAsOwnerMapWithReplayPatternName_WithNullReplayPatternName_ThrowsIllegalArgumentException() {
        parser.parseMembersReplaysAsOwnerMap(platoon, (String) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseMembersReplaysAsOwnerMapWithReplayPatternName_WithEmptyReplayPatternName_ThrowsIllegalArgumentException() {
        parser.parseMembersReplaysAsOwnerMap(platoon, "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseMembersReplaysWithReplayPatternName_WithNullReplayPatternName_ThrowsIllegalArgumentException() {
        parser.parseMembersReplays(platoon, (String) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseMembersReplaysWithReplayPatternName_WithEmptyReplayPatternName_ThrowsIllegalArgumentException() {
        parser.parseMembersReplays(platoon, "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseMembersReplaysWithUploadAfter_WithNullUploadAfter_ThrowsIllegalArgumentException() {
        parser.parseMembersReplays(platoon, (LocalDateTime) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseMembersReplays_WithNullPlatoon_ThrowsIllegalArgumentException() {
        parser.parseMembersReplays(null, ".*Турнир Чака.*", LocalDateTime.of(2018, 5, 23, 18, 19));
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseMembersReplays_WithNullPlatoonPlayers_ThrowsIllegalArgumentException() {
        platoon.setPlayers(null);
        parser.parseMembersReplays(platoon, ".*Турнир Чака.*", LocalDateTime.of(2018, 5, 23, 18, 19));
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseMembersReplays_WithEmptyPlatoonPlayers_ThrowsIllegalArgumentException() {
        platoon.setPlayers(Collections.emptyList());
        parser.parseMembersReplays(platoon, ".*Турнир Чака.*", LocalDateTime.of(2018, 5, 23, 18, 19));
    }

    private static Player getPlayer(String name, DrawGroup group) {
        Player player = new Player();
        player.setDrawGroup(group);
        player.setName(name);
        return player;
    }

}