package org.avlasov.parser.file;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.avlasov.PowerMockTestCase;
import org.avlasov.entity.wotreplay.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.powermock.api.mockito.PowerMockito.doThrow;
import static org.powermock.api.mockito.PowerMockito.spy;

/**
 * Created By artemvlasov on 03/06/2018
 **/
@PrepareForTest(value = {WotReplayFileParser.class})
public class WotReplayFileParserTest extends PowerMockTestCase  {

    private WotReplayFileParser wotReplayFileParser;

    @Before
    public void setUp() throws Exception {
        wotReplayFileParser = new WotReplayFileParser();
    }

    @Test
    public void parseWotReplay_WithValidData_ReturnWotReplayInfo() {
        String path = WotReplayFileParserTest.class.getResource("./15272867730864_ussr_R149_Object_268_4_murovanka.wotreplay").getFile();
        Optional<WotReplayInfo> wotReplayInfo = wotReplayFileParser.parseWotReplay(path);
        assertTrue(wotReplayInfo.isPresent());
        WotReplayInfo replayInfo = wotReplayInfo.get();
        WotReplayMatch replayMatch = replayInfo.getReplayMatch();
        WotReplayOwnerInfo ownerInfo = replayInfo.getReplayOwnerInfo();
        verifyWotReplayInfo(ownerInfo);
        assertEquals(new BigInteger("11951349323822650"), replayMatch.getArenaUniqueID());
        assertEquals(365, replayMatch.getCommon().getDuration());
        assertEquals(1, replayMatch.getCommon().getWinnerTeam());
        assertFalse(replayMatch.getPlayers().isEmpty());
        assertFalse(replayMatch.getVehicles().isEmpty());
        verifyWotReplayPlayer(replayMatch.getPlayers().get(0));
        verifyWotReplayVehicle(replayMatch.getVehicles());
    }

    @Test
    public void parseWotReplay_WithCorruptedReplay_ReturnOptionalEmpty() {
        String path = WotReplayFileParserTest.class.getResource("./15271752204742_china_Ch41_WZ_111_5A_desert.wotreplay").getFile();
        Optional<WotReplayInfo> wotReplayInfo = wotReplayFileParser.parseWotReplay(path);
        assertFalse(wotReplayInfo.isPresent());
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseWotReplay_WithInvalidReplayName_ThrowsIllegalArgumentsException() {
        wotReplayFileParser.parseWotReplay("invalidFile");
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseWotReplay_WithMissingFile_ThrowsIllegalArgumentsException() {
        wotReplayFileParser.parseWotReplay("missingfile.wotreplay");
    }

    @Test(expected = RuntimeException.class)
    public void parseWotReplay_WithExceptionDuringParsing_ThrowsRuntimeException() throws Exception {
        WotReplayFileParser spy = spy(wotReplayFileParser);
        doThrow(new IOException()).when(spy, "findReplayDataJson", any(FileInputStream.class));
        String path = WotReplayFileParserTest.class.getResource("./wotreplayData_without_owner_info.wotreplay").getFile();
        spy.parseWotReplay(path);
    }

    @Test
    public void parseWotReplay_WithOutOwnerInfo_ReturnWotReplayInfo() {
        String path = WotReplayFileParserTest.class.getResource("./wotreplayData_without_owner_info.wotreplay").getFile();
        Optional<WotReplayInfo> wotReplayInfo = wotReplayFileParser.parseWotReplay(path);
        assertTrue(wotReplayInfo.isPresent());
        assertNull(wotReplayInfo.get().getReplayOwnerInfo());
        assertEquals(365, wotReplayInfo.get().getReplayMatch().getCommon().getDuration());
    }

    private void verifyWotReplayInfo(WotReplayOwnerInfo replayOwnerInfo) {
        assertNotNull(replayOwnerInfo);
        assertEquals("Sh0tnik", replayOwnerInfo.getPlayerName());
        assertFalse(replayOwnerInfo.getClientVersionFromXml().isEmpty());
        assertEquals("1.0.1.0", replayOwnerInfo.getClientVersionFromExe());
        assertEquals("Мурованка", replayOwnerInfo.getMapDisplayName());
        assertFalse(replayOwnerInfo.isHasMods());
        assertEquals("RU", replayOwnerInfo.getRegionCode());
        assertEquals(32437801, replayOwnerInfo.getPlayerID());
        assertEquals("WOT RU1", replayOwnerInfo.getServerName());
        assertNotNull(replayOwnerInfo.getDateTime());
        assertEquals("11_murovanka", replayOwnerInfo.getMapName());
        assertEquals(1, replayOwnerInfo.getBattleType());
        assertEquals("ussr-R149_Object_268_4", replayOwnerInfo.getPlayerVehicle());
    }

    private void verifyWotReplayPlayer(WotReplayPlayer wotReplayPlayer) {
        assertNotNull(wotReplayPlayer);
        assertNotEquals(0, wotReplayPlayer.getPlayerId());
        assertNotEquals("", wotReplayPlayer.getName());
        assertNotEquals(0, wotReplayPlayer.getPrebattleID());
        if (wotReplayPlayer.getClanAbbrev().isEmpty()) {
            assertEquals("", wotReplayPlayer.getClanAbbrev());
            assertEquals(0, wotReplayPlayer.getClanDBID());
        } else {
            assertNotEquals("", wotReplayPlayer.getClanAbbrev());
            assertNotEquals(0, wotReplayPlayer.getClanDBID());
        }
        assertNotEquals(0, wotReplayPlayer.getTeam());
    }

    private void verifyWotReplayVehicle(List<WotReplayVehicle> wotReplayVehicles) {
        assertNotNull(wotReplayVehicles);
        WotReplayVehicle testWotReplayVehicle = readJsonTestObject();
        assertNotNull(testWotReplayVehicle);
        Optional<WotReplayVehicle> wotReplayVehicle = wotReplayVehicles.parallelStream()
                .filter(wrv -> wrv.getVehicleMatchId() == testWotReplayVehicle.getVehicleMatchId())
                .findFirst();
        assertTrue(wotReplayVehicle.isPresent());
        assertEquals(testWotReplayVehicle, wotReplayVehicle.get());
    }

    private WotReplayVehicle readJsonTestObject() {
        ObjectMapper objectMapper = new ObjectMapper();
        try (InputStream resourceAsStream = WotReplayFileParserTest.class.getResourceAsStream("./wotReplayVehicle.json")) {
            WotReplayVehicle wotReplayVehicle = objectMapper.readValue(resourceAsStream, WotReplayVehicle.class);
            wotReplayVehicle.setVehicleMatchId(9827913);
            return wotReplayVehicle;
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

}