package org.avlasov.parser.file;

import org.avlasov.entity.wotreplay.WotReplayInfo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.powermock.api.mockito.PowerMockito.doThrow;
import static org.powermock.api.mockito.PowerMockito.spy;

/**
 * Created By artemvlasov on 03/06/2018
 **/
@RunWith(PowerMockRunner.class)
@PrepareForTest(value = {WotReplayFileParser.class})
@PowerMockIgnore({"com.sun.org.apache.xerces.*", "javax.xml.parsers.*", "org.apache.logging.*"})
public class WotReplayFileParserTest {

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
        assertEquals(new BigInteger("11951349323822650"), wotReplayInfo.get().getReplayMatch().getArenaUniqueID());
        assertEquals("Sh0tnik", wotReplayInfo.get().getReplayOwnerInfo().getPlayerName());
        assertEquals(365, wotReplayInfo.get().getReplayMatch().getCommon().getDuration());
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


}