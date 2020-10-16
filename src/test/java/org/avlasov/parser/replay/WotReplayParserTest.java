package org.avlasov.parser.replay;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.commons.io.IOUtils;
import org.avlasov.parser.config.properties.WotReplaysProperties;
import org.avlasov.parser.deserializer.MatchLocalDateTimeDeserializer;
import org.avlasov.parser.replay.entity.Replay;
import org.avlasov.parser.replay.entity.match.MatchResult;
import org.avlasov.parser.replay.entity.match.details.MatchPlayerDetails;
import org.hamcrest.collection.IsCollectionWithSize;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.AdditionalMatchers.find;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WotReplayParserTest {

    @Mock
    private WotReplaysProperties wotReplaysProperties;
    @Mock
    private RestTemplate restTemplate;
    @Mock
    private PhantomJSDriver phantomJSDriver;
    private WotReplayParser wotReplayParser;
    private ObjectMapper objectMapper;

    @Before
    public void setUp() throws Exception {
        objectMapper = Jackson2ObjectMapperBuilder
                .json()
                .failOnUnknownProperties(false)
                .modules(new JavaTimeModule())
                .deserializerByType(LocalDateTime.class, new MatchLocalDateTimeDeserializer())
                .build();
        wotReplayParser = new WotReplayParser(wotReplaysProperties, restTemplate, objectMapper, phantomJSDriver);
        when(restTemplate.getForEntity(anyString(), any())).thenReturn(ResponseEntity.ok(getFileContentBytes("file/test_replay.wotreplay")));
    }

    @Test
    public void parseBySearchLinkAllPages() throws Exception {
        String firstPage = getFileContent("search-link-all-pages/search_first_page.html");
        String secondPage = getFileContent("search-link-all-pages/search_second_page.html");
        String link = "test/hello";
        int numberOfReplays = 13;

        doAnswer(invocation -> when(phantomJSDriver.getPageSource()).thenReturn(firstPage))
                .when(phantomJSDriver).get(link + "/page/1");
        doAnswer(invocation -> when(phantomJSDriver.getPageSource()).thenReturn(secondPage))
                .when(phantomJSDriver).get(link + "/page/2");
        doAnswer(invocation -> when(phantomJSDriver.getPageSource()).thenReturn(firstPage))
                .when(phantomJSDriver).get(find(link + "/page/[3-9]"));

        when(wotReplaysProperties.getSearchLinkPattern()).thenReturn("test/.*");
        when(wotReplaysProperties.getLink()).thenReturn(link);
        when(wotReplaysProperties.getDownloadLinkPattern()).thenReturn("test/hello/site/download/[0-9]*");

        Set<Replay> replays = wotReplayParser.parseBySearchLinkAllPages("test/page/1/hello/");

        assertNotNull(replays);

        verify(wotReplaysProperties).getSearchLinkPattern();
        verify(wotReplaysProperties, atLeast(numberOfReplays)).getDownloadLinkPattern();
        verify(wotReplaysProperties, atLeast(numberOfReplays)).getLink();

        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
        verify(phantomJSDriver, times(3)).get(urlCaptor.capture());

        assertTrue(urlCaptor.getAllValues().containsAll(Arrays.asList(link + "/page/1", link + "/page/2")));
    }

    @Test
    public void parseBySearchLink() throws Exception {
        doAnswer(invocation -> when(phantomJSDriver.getPageSource()).thenReturn(getFileContent("search-link/search_single_page.html")))
                .when(phantomJSDriver).get(anyString());
        String link = "test";
        String downloadLink = link + "/site/download/12109338";

        when(wotReplaysProperties.getSearchLinkPattern()).thenReturn("search");
        when(wotReplaysProperties.getLink()).thenReturn(link);
        when(wotReplaysProperties.getDownloadLinkPattern()).thenReturn("test/site/download/[0-9]*");

        Set<Replay> search = wotReplayParser.parseBySearchLink("search");

        assertNotNull(search);
        assertThat(search, IsCollectionWithSize.hasSize(1));

        verify(wotReplaysProperties).getSearchLinkPattern();
        verify(wotReplaysProperties).getDownloadLinkPattern();
        verify(wotReplaysProperties, times(2)).getLink();
        verify(phantomJSDriver).get("search");

        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
        verify(restTemplate).getForEntity(urlCaptor.capture(), any());

        assertEquals(downloadLink, urlCaptor.getValue());
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseByReplayId_WithNotValidLink_ShouldThrowIllegalArgumentException() throws Exception {
        wotReplayParser.parseByReplayId("test");
    }

    @Test
    public void parseByReplayLink() throws Exception{
        doAnswer(invocation -> when(phantomJSDriver.getPageSource()).thenReturn(getFileContent("replay-link/replay.html")))
                .when(phantomJSDriver).get(anyString());
        String link = "test";

        when(wotReplaysProperties.getReplayLinkPattern()).thenReturn(link);
        when(wotReplaysProperties.getDownloadLinkGetPattern()).thenReturn("download/%s");
        when(wotReplaysProperties.getDownloadLinkPattern()).thenReturn("download/[0-9]*");

        Replay replay = wotReplayParser.parseByReplayLink(link);

        assertNotNull(replay);
        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
        verify(restTemplate).getForEntity(urlCaptor.capture(), any());

        assertTrue(urlCaptor.getValue().contains("12109338"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseByDownloadLink_WithNotValidLink_ShouldThrowIllegalArgumentException() throws Exception {
        when(wotReplaysProperties.getDownloadLinkPattern()).thenReturn("valid");

        wotReplayParser.parseByDownloadLink("notvalid");
    }

    @Test
    public void parse() throws Exception {
        File replayFile = new File(WotReplayParserTest.class.getResource("file/test_replay.wotreplay").getFile());
        Replay replay = wotReplayParser.parse(replayFile);

        Replay replayExpected = new Replay();
        replayExpected.setMatchPlayerDetails(objectMapper
                .readValue(new File(WotReplayParserTest.class.getResource("file/match-player-details.json").getFile()), MatchPlayerDetails.class));
        replayExpected.setMatchResult(objectMapper
                .readValue(new File(WotReplayParserTest.class.getResource("file/match-result.json").getFile()), MatchResult.class));

        assertEquals(replayExpected, replay);
    }

    private String getFileContent(String filePath) {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(
                WotReplayParserTest.class.getResourceAsStream(filePath)));
        return bufferedReader.lines().collect(Collectors.joining());
    }


    private byte[] getFileContentBytes(String filePath) throws Exception {
        return IOUtils.toByteArray(new InputStreamReader(
                WotReplayParserTest.class.getResourceAsStream(filePath)), Charset.defaultCharset().name());
    }

}