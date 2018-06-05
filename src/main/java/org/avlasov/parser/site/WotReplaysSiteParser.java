package org.avlasov.parser.site;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.avlasov.entity.WotReplaysSearchReplayDetails;
import org.avlasov.entity.match.Platoon;
import org.avlasov.entity.match.Player;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.time.temporal.ChronoField.*;

/**
 * Created By artemvlasov on 03/06/2018
 **/
public class WotReplaysSiteParser {

    private final static Logger LOGGER = LogManager.getLogger(WotReplaysSiteParser.class);
    private final PhantomJSDriver phantomJSDriver;
    private DateTimeFormatter uploadDateTimFormatter;

    public WotReplaysSiteParser() {
        System.setProperty("phantomjs.binary.path", "libs/phantomjs");
        DesiredCapabilities dcap = new DesiredCapabilities();
        String[] phantomArgs = new String[]{"--webdriver-loglevel=NONE"};
        dcap.setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS, phantomArgs);
        phantomJSDriver = new PhantomJSDriver(dcap);
        uploadDateTimFormatter = new DateTimeFormatterBuilder()
                .appendValue(YEAR, 4)
                .appendLiteral('-')
                .appendValue(MONTH_OF_YEAR, 2)
                .appendLiteral('-')
                .appendValue(DAY_OF_MONTH, 2)
                .appendLiteral(' ')
                .appendValue(HOUR_OF_DAY, 2)
                .appendLiteral(':')
                .appendValue(MINUTE_OF_HOUR, 2)
                .toFormatter();
    }

    public List<WotReplaysSearchReplayDetails> parseMembersReplays(Platoon platoon, String replayNamePattern, LocalDateTime uploadAfter) {
        if (platoon == null || platoon.getPlayers() == null || platoon.getPlayers().isEmpty())
            throw new IllegalArgumentException("Platoon and Platoon Players is required.");
        String basePath = String.format("http://wotreplays.ru/site/index/version/63/members/%s/sort/uploaded_at.desc/page/", platoon.getPlayers().get(0).getName());
        LOGGER.info(String.format("Start parsing platoon '%s' members replays with link '%s' with additional filter%s",
                platoon.getPlatoonName(),
                basePath,
                (replayNamePattern != null ? "\n\t- replay pattern name " + replayNamePattern : "")
                        + (uploadAfter != null ? "\n\t- upload after date " + uploadAfter.toLocalDate() : "")));
        List<WotReplaysSearchReplayDetails> details = parseReplays(basePath, replayNamePattern, uploadAfter);
        List<String> platoonPlayersNames = platoon.getPlayers()
                .stream()
                .map(Player::getName)
                .collect(Collectors.toList());
        details = details.parallelStream()
                .filter(filterByPlatoonPlayers(platoonPlayersNames))
                .collect(Collectors.toList());
        LOGGER.info(String.format("Complete parsing platoon '%s' members replays (replays found %d)", platoon.getPlatoonName(), details.size()));
        return details;
    }

    public List<WotReplaysSearchReplayDetails> parseMembersReplays(Platoon platoon, String replayNamePattern) {
        if (replayNamePattern == null || replayNamePattern.isEmpty())
            throw new IllegalArgumentException("replayNamePattern field is required.");
        return parseMembersReplays(platoon, replayNamePattern, null);
    }

    public List<WotReplaysSearchReplayDetails> parseMembersReplays(Platoon platoon, LocalDateTime uploadAfter) {
        if (uploadAfter == null)
            throw new IllegalArgumentException("uploadAfter field is required.");
        return parseMembersReplays(platoon, null, uploadAfter);
    }

    public Map<String, List<WotReplaysSearchReplayDetails>> parseMembersReplaysAsOwnerMap(Platoon platoon, String replayNamePattern, LocalDateTime uploadAfter) {
        List<WotReplaysSearchReplayDetails> details = parseMembersReplays(platoon, replayNamePattern, uploadAfter);
        Map<String, List<WotReplaysSearchReplayDetails>> ownerWotReplayDetailsMap = new HashMap<>();
        for (WotReplaysSearchReplayDetails detail : details) {
            ownerWotReplayDetailsMap.compute(detail.getReplayOwner(), (owner, wrsrd) -> {
                if (wrsrd == null)
                    wrsrd = new ArrayList<>();
                wrsrd.add(detail);
                return wrsrd;
            });
        }
        return ownerWotReplayDetailsMap;
    }

    public Map<String, List<WotReplaysSearchReplayDetails>> parseMembersReplaysAsOwnerMap(Platoon platoon, String replayNamePattern) {
        if (replayNamePattern == null || replayNamePattern.isEmpty())
            throw new IllegalArgumentException("replayNamePattern field is required.");
        return parseMembersReplaysAsOwnerMap(platoon, replayNamePattern, null);
    }

    public Map<String, List<WotReplaysSearchReplayDetails>> parseMembersReplaysAsOwnerMap(Platoon platoon, LocalDateTime uploadAfter) {
        if (uploadAfter == null)
            throw new IllegalArgumentException("uploadAfter field is required.");
        return parseMembersReplaysAsOwnerMap(platoon, null, uploadAfter);
    }

    public List<WotReplaysSearchReplayDetails> parsePlayerReplays(String playerName, String replayNamePattern, LocalDateTime uploadAfter) {
        if (playerName == null || playerName.isEmpty())
            throw new IllegalArgumentException("playerName field is required.");
        String basePath = String.format("http://wotreplays.ru/site/index/version/63/player/%s/sort/uploaded_at.desc/page/", playerName);
        LOGGER.info(String.format("Start parsing player %s replays with link '%s' with additional filter%s",
                playerName,
                basePath,
                (replayNamePattern != null ? "\n\t- replay pattern name " + replayNamePattern : "")
                        + (uploadAfter != null ? "\n\t- upload after date " + uploadAfter.toLocalDate() : "")));
        List<WotReplaysSearchReplayDetails> details = parseReplays(basePath, replayNamePattern, uploadAfter);
        LOGGER.info(String.format("Complete parsing player %s replays (replays found %d)", playerName, details.size()));
        return details;
    }

    public List<WotReplaysSearchReplayDetails> parsePlayerReplays(String playerName, LocalDateTime uploadAfter) {
        if (uploadAfter == null)
            throw new IllegalArgumentException("uploadAfter field is required");
        return parsePlayerReplays(playerName, null, uploadAfter);
    }

    public List<WotReplaysSearchReplayDetails> parsePlayerReplays(String playerName, String replayNamePattern) {
        if (replayNamePattern == null || replayNamePattern.isEmpty())
            throw new IllegalArgumentException("replayNamePattern field is required");
        return parsePlayerReplays(playerName, replayNamePattern, null);
    }

    public List<WotReplaysSearchReplayDetails> parsePlayerReplays(String playerName) {
        return parsePlayerReplays(playerName, null, null);
    }

    private List<WotReplaysSearchReplayDetails> parseReplays(String basePath, String replayNamePattern, LocalDateTime uploadAfter) {
        String searchLinkPattern = basePath + "%d/";
        List<WotReplaysSearchReplayDetails> details = new ArrayList<>();
        for (int i = 1;;i++) {
            String link = String.format(searchLinkPattern, i);
            Elements replaysElements = findReplayElements(link);
            for (Element replaysElement : replaysElements) {
                WotReplaysSearchReplayDetails replayDetail = parseWotReplaysSearchReplayDetails(replaysElement);
                if (replayDetail != null) {
                    if (isCircleRequest(details, replayDetail) || isAnOldReplay(replayDetail, uploadAfter)) {
                        return details;
                    }
                    if (isReplayNameMatchingPattern(replayDetail, replayNamePattern))
                        details.add(replayDetail);
                }
            }
        }
    }

    /**
     * Check if  parsing go on second circle
     *
     * @param parsedDetails Already parsed details
     * @param lastParsedDetail last parsed details
     * @return {@link true} if {@param lastParsedDetail} is in {@param parsedDetails}, otherwise {@link false}
     */
    private boolean isCircleRequest(List<WotReplaysSearchReplayDetails> parsedDetails, WotReplaysSearchReplayDetails lastParsedDetail) {
        return parsedDetails.contains(lastParsedDetail);
    }

    /**
     * Check if last parsed detail is upload before {@param uploadAfter} date
     *
     * @param lastParsedDetail Last parsed detail
     * @param uploadAfter Upload after parameter
     * @return {@link true} if {@param uploadAfter} is not null and {@link WotReplaysSearchReplayDetails#getUploadDate()} is before {@param uploadAfter}, otherwise {@link false}
     */
    private boolean isAnOldReplay(WotReplaysSearchReplayDetails lastParsedDetail, LocalDateTime uploadAfter) {
        return uploadAfter != null && lastParsedDetail.getUploadDate() != null && lastParsedDetail.getUploadDate().isBefore(uploadAfter);
    }

    /**
     * Check if last parsed replay name {@link WotReplaysSearchReplayDetails#getReplayName()} is matching {@param replayNamePattern}
     *
     * @param lastParsedDetail last parsed detail
     * @param replayNamePattern replay name pattern
     * @return {@link true} if {@param replayNamePattern} is null or is empty or {@link WotReplaysSearchReplayDetails#getReplayName()} is matching {@param replayNamePattern}
     */
    private boolean isReplayNameMatchingPattern(WotReplaysSearchReplayDetails lastParsedDetail, String replayNamePattern) {
        return replayNamePattern == null || replayNamePattern.isEmpty() || lastParsedDetail.getReplayName().matches(replayNamePattern);
    }

    private Elements findReplayElements(String link) {
        Document data = getData(link);
        Element replayLinks = data.getElementsByClass("r_list").get(0);
        return replayLinks.getElementsByClass("clearfix")
                .stream()
                .filter(element -> "li".equals(element.tag().getName()))
                .collect(Collectors.toCollection(Elements::new));
    }

    private WotReplaysSearchReplayDetails parseWotReplaysSearchReplayDetails(Element element) {
        Elements replayInfo = element.getElementsByClass("r-info");
        if (replayInfo != null && !replayInfo.isEmpty()) {
            Element replayInfoElement = replayInfo.get(0);
            if (replayInfoElement != null) {
                WotReplaysSearchReplayDetails details = new WotReplaysSearchReplayDetails();
                addReplayLinkAndName(replayInfoElement, details);
                details.setPossiblyCorruptedReplay(isReplayPossiblyCorrupted(replayInfoElement));
                details.setReplayOwner(getReplayOwner(replayInfoElement));
                details.setReplayDownloadLink(getDownloadLink(element));
                details.setUploadDate(getUploadReplayDate(replayInfoElement));
                return details;
            }
        }
        return null;
    }

    private void addReplayLinkAndName(Element replayInfo, WotReplaysSearchReplayDetails details) {
        Element h3 = replayInfo.getElementsByTag("h3").get(0);
        Element replayLinkElement = h3.getElementsByTag("a").get(0);
        details.setReplayName(replayLinkElement.text());
        details.setReplayLink("http://wotreplays.ru" + replayLinkElement.attr("href"));
    }

    private boolean isReplayPossiblyCorrupted(Element replayInfo) {
        Element replayInfoDetail = replayInfo.getElementsByClass("r-info_ri").get(0);
        int li = replayInfoDetail.getElementsByTag("li")
                .stream()
                .mapToInt(value -> Integer.parseInt(value.text().trim()))
                .sum();
        return li == 0;
    }

    private String getReplayOwner(Element replayInfo) {
        Element replayInfoOwnerDetail = replayInfo.getElementsByClass("r-info_ci").get(0);
        Elements li = replayInfoOwnerDetail.getElementsByTag("li");
        return li.get(li.size() - 1).ownText();
    }

    private LocalDateTime getUploadReplayDate(Element replayInfo) {
        Element replayInfoOwnerDetail = replayInfo.getElementsByClass("r-info_ci").get(0);
        Elements li = replayInfoOwnerDetail.getElementsByTag("li");
        String uploadDateString = li.get(li.size() - 2).getElementsByClass("b-date").get(0).text();
        return LocalDateTime.parse(uploadDateString, uploadDateTimFormatter);
    }

    private String getDownloadLink(Element element) {
        Element replayActionElement = element.getElementsByClass("r-act").get(0);
        Element downloadLink = replayActionElement.getElementsByTag("a").get(0);
        return "http://wotreplays.ru" + downloadLink.attr("href");
    }

    private Document getData(String link) {
        phantomJSDriver.get(link);
        return Jsoup.parse(phantomJSDriver.getPageSource());
    }

    private Predicate<? super WotReplaysSearchReplayDetails> filterByPlatoonPlayers(List<String> platoonPlayersNames) {
        return detail -> platoonPlayersNames.contains(detail.getReplayOwner());
    }

}
