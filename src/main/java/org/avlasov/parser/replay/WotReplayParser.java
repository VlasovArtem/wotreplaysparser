package org.avlasov.parser.replay;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.avlasov.parser.builder.enums.WotReplaysLinkAttribute;
import org.avlasov.parser.config.properties.WotReplaysProperties;
import org.avlasov.parser.replay.entity.Replay;
import org.avlasov.parser.replay.entity.WotReplay;
import org.avlasov.parser.replay.entity.match.MatchResult;
import org.avlasov.parser.replay.entity.match.details.MatchPlayerDetails;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PreDestroy;
import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
public class WotReplayParser {

    private final WotReplaysProperties wotReplaysProperties;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final PhantomJSDriver phantomJSDriver;

    public WotReplayParser(WotReplaysProperties wotReplaysProperties,
                           RestTemplate wotReplayParserRestTemplate,
                           ObjectMapper wotReplayParserObjectMapper,
                           PhantomJSDriver phantomJSDriver) {
        this.wotReplaysProperties = wotReplaysProperties;
        this.restTemplate = wotReplayParserRestTemplate;
        this.objectMapper = wotReplayParserObjectMapper;
        this.phantomJSDriver = phantomJSDriver;
    }

    public Set<Replay> parseBySearchLinkAllPages(String searchLink) {
        verifyLink(searchLink, wotReplaysProperties.getSearchLinkPattern());

        if (searchLink.contains("/page/")) {
            searchLink = searchLink.replaceFirst("/page/\\d+", "");
        }
        if (searchLink.endsWith("/")) {
            searchLink = searchLink.substring(0, searchLink.length() - 1);
        }

        Set<WotReplay> wotReplayData = new HashSet<>();

        for (int i = 1; ; i++) {
            String page = String.valueOf(i);
            String pageLink = searchLink + String.format(WotReplaysLinkAttribute.PAGE.getAttributePattern(), page);
            if (!wotReplayData.addAll(parseWotReplayData(pageLink))) {
                break;
            }
        }
        return wotReplayData
                .stream()
                .map(this::parseByDownloadLink)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    public Set<Replay> parseBySearchLink(String searchLink) {
        verifyLink(searchLink, wotReplaysProperties.getSearchLinkPattern());

        return parseWotReplayData(searchLink)
                .stream()
                .map(this::parseByDownloadLink)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    public Replay parseByReplayId(String replayId) {
        if (!replayId.matches("[0-9]*")) {
            throw new IllegalArgumentException(String.format("Replay id %s is not matching pattern '[0-9]*'.", replayId));
        }
        return parseByDownloadLink(String.format(wotReplaysProperties.getDownloadLinkGetPattern(), replayId));
    }

    public Replay parseByReplayLink(String replayLink) {
        verifyLink(replayLink, wotReplaysProperties.getReplayLinkPattern());

        String replayId = getData(replayLink)
                .getElementsByClass("replay-id")
                .attr("value");

        return parseByDownloadLink(String.format(wotReplaysProperties.getDownloadLinkGetPattern(), replayId));
    }

    public Replay parseByDownloadLink(String downloadLink) {
        verifyLink(downloadLink, wotReplaysProperties.getDownloadLinkPattern());

        Replay replay = parser(restTemplate.getForEntity(downloadLink, byte[].class).getBody());
        if (replay != null) {
            String replayId = downloadLink.replace("http://wotreplays.ru/site/download/", "")
                    .replaceAll("/.*", "");
            replay.setWotReplay(new WotReplay(downloadLink, String.format(wotReplaysProperties.getDownloadLinkGetPattern(), replayId)));
        }
        return replay;
    }

    private Set<WotReplay> parseWotReplayData(String pageLink) {
        return getData(pageLink)
                .getElementsByClass("r_list")
                .get(0)
                .getElementsByClass("r-act")
                .stream()
                .map(rAct -> {
                    String downloadLink = wotReplaysProperties.getLink() + (rAct.getElementsByTag("a")
                            .get(0)
                            .attr("href"));
                    String replayLink = wotReplaysProperties.getLink() + (rAct.getElementsByClass("r-act_soc")
                            .get(0)
                            .getElementsByClass("r-act_soc-com")
                            .get(0)
                            .attr("href"));
                    return new WotReplay(replayLink, downloadLink);
                })
                .collect(Collectors.toSet());
    }

    private Replay parseByDownloadLink(WotReplay wotReplay) {
        verifyLink(wotReplay.getDownloadLink(), wotReplaysProperties.getDownloadLinkPattern());

        Replay replay = parser(restTemplate.getForEntity(wotReplay.getDownloadLink(), byte[].class).getBody());
        if (replay != null) {
            replay.setWotReplay(wotReplay);
        }
        return replay;
    }

    @SneakyThrows
    public Replay parse(File replay) {
        byte[] bytes = IOUtils.toByteArray(new FileInputStream(replay));
        return parser(bytes);
    }

    private Replay parser(byte[] replayContent) {
        try {
            if (replayContent != null) {
                Replay replay = new Replay();
                byte[] firstPart = readData(replayContent);
                replay.setMatchPlayerDetails(objectMapper.readValue(firstPart, MatchPlayerDetails.class));
                byte[] secondPart = readData(Arrays.copyOfRange(replayContent, firstPart.length, replayContent.length - firstPart.length));
                JsonNode jsonNode = objectMapper.readTree(secondPart);
                JsonNode matchResultJsonNode = jsonNode.get(0);
                replay.setMatchResult(objectMapper.readValue(matchResultJsonNode.traverse(), MatchResult.class));
                return replay;
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    private void verifyLink(String link, String pattern) {
        if (!link.matches(pattern)) {
            throw new IllegalArgumentException(String.format("Link %s is not matching pattern '%s'.", link, pattern));
        }
    }

    @SneakyThrows
    private Document getData(String link) {
        phantomJSDriver.get(link);
        return Jsoup.parse(phantomJSDriver.getPageSource());
    }

    private byte[] readData(byte[] bytes) {
        byte openedParentheses = 0;
        int objectStartIndex = 0;
        boolean objectStart = false;
        boolean objectEnd = false;
        for (int i = 0; i < bytes.length && !objectEnd; i++) {
            byte read = bytes[i];
            if (objectStart && read < 32) {
                objectStart = false;
                openedParentheses = 0;
                continue;
            }
            if (openedParentheses == 0 && (read == '{' || read == '[')) {
                openedParentheses++;
                objectStart = true;
                objectStartIndex = i;
            } else {
                if (objectStart && openedParentheses != 0) {
                    if (read == '{' || read == '[') {
                        openedParentheses++;
                    }
                    if (read == '}' || read == ']') {
                        openedParentheses--;
                    }
                }
                if (objectStart && openedParentheses == 0) {
                    return Arrays.copyOfRange(bytes, objectStartIndex, i + 1);
                }
            }
        }
        return new byte[]{};
    }

    @PreDestroy
    public void destroy() {
        phantomJSDriver.close();
    }

}
