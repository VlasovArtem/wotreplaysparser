package org.avlasov.parser;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.avlasov.config.PlatoonConfig;
import org.avlasov.config.PlatoonConfigReader;
import org.avlasov.config.entity.PlatoonData;
import org.avlasov.entity.match.*;
import org.avlasov.entity.match.enums.Result;
import org.avlasov.utils.DataUtils;
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
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.time.temporal.ChronoField.*;

/**
 * Created By artemvlasov on 21/05/2018
 **/
public class ParseData {

    private final static Logger LOGGER = LogManager.getLogger(ParseData.class);
    private final int maxPlatoonPlayers = 3;
    private DateTimeFormatter dateTimeFormatter;
    private final PhantomJSDriver phantomJSDriver;

    public ParseData() {
        System.setProperty("phantomjs.binary.path", "libs/phantomjs");
        DesiredCapabilities dcap = new DesiredCapabilities();
        String[] phantomArgs = new String[]{
                "--webdriver-loglevel=NONE"
        };
        dcap.setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS, phantomArgs);
        phantomJSDriver = new PhantomJSDriver(dcap);
        dateTimeFormatter = new DateTimeFormatterBuilder()
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

    public List<Match> parseAllPlatoonsMatches() {
        long start = System.currentTimeMillis();
        LOGGER.info("Start parsing all platoons matches");
        PlatoonConfigReader platoonConfigReader = new PlatoonConfigReader();
        PlatoonConfig platoonConfig = platoonConfigReader.readData();
        if (platoonConfig == null)
            throw new RuntimeException("Platoon config is null, please check file ");
        Set<Match> allMatches = new HashSet<>();
        for (PlatoonData platoonData : platoonConfig.getPlatoonDataList()) {
            Set<Match> platoonMatches = new HashSet<>();
            for (Player player : platoonData.getPlatoonPlayers()) {
                LOGGER.info(String.format("Start parsing matches for the player %s in the platoon %s", player.getName(), platoonData.getPlatoonName()));
                List<Match> matches = parseMatches(player.getName(), platoonData);
                if (matches.size() < 40) {
                    LOGGER.warn(String.format("Player with %s from the platoon %s has not all required matches links (40 is required - %d parsed)", player.getName(), platoonData.getPlatoonName(), matches.size()));
                } else if (matches.size() > 40) {
                    LOGGER.warn(String.format("Player with %s from the platoon %s has more then 40 required matches links (40 is required - %d parsed)", player.getName(), platoonData.getPlatoonName(), matches.size()));
                }
                platoonMatches.addAll(matches);
                if (platoonMatches.size() == 40) {
                    break;
                }
            }
            allMatches.addAll(platoonMatches);
        }
        long end = System.currentTimeMillis();
        LOGGER.info(String.format("Parsing platoons data completed in %d seconds", ((end - start) / 1000)));
        phantomJSDriver.close();
        return allMatches
                .stream()
                .sorted(Comparator.comparing(Match::getMatchDate))
                .collect(Collectors.toList());
    }

    public List<Match> parseMatches(String username, PlatoonData platoonData) {
        Set<String> links = new HashSet<>();
        for (int i = 0; i < 10; i++) {
            List<String> matchesLinks = getMatchesLinks(username, i + 1);
            if (links.stream().anyMatch(matchesLinks::contains)) {
                break;
            } else if (!matchesLinks.isEmpty()) {
                links.addAll(matchesLinks);
            } else if (!links.isEmpty()) {
                break;
            }
        }
        LOGGER.info(String.format("%d matches links found for the user %s.", links.size(), username));
        List<Match> collect = links.stream()
                .map(link -> parseMatch(link, platoonData))
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(Match::getMatchDate))
                .collect(Collectors.toList());
        phantomJSDriver.close();
        return collect;
    }

    private Match parseMatch(String matchLink, PlatoonData platoonData) {
        validateMatchLink(matchLink);
        String[] split = matchLink.split("#");
        Document data = getData(split[0]);
        List<PlayerMatch> playerMatches = parseKOPM2Players(getAllysPlayersInformation(data), matchLink, platoonData);
        if (!playerMatches.isEmpty() && playerMatches.size() == maxPlatoonPlayers) {
            List<Player> players = playerMatches.parallelStream()
                    .map(PlayerMatch::getPlayer)
                    .collect(Collectors.toList());
            Platoon platoon = new Platoon(players, DataUtils.getPlatoonName(playerMatches.get(0)));
            return Match.builder()
                    .playerMatches(playerMatches)
                    .mapData(getMapData(data))
                    .matchDurationInSeconds(getMatchDurationInSeconds(data))
                    .matchDate(getMatchDate(data))
                    .platoon(platoon)
                    .matchLink(matchLink)
                    .result(getMatchResult(data, playerMatches))
                    .build();
        }
        return null;
    }

    private MapData getMapData(Document stats) {
        String[] split = stats.getElementsByClass("replay-stats__subtitle").get(0).text().split("–");
        return new MapData(split[0].trim(), split[1].trim());
    }

    private void validateMatchLink(String link) {
        if (!link.matches("http://wotreplays\\.ru/site/\\d+#.*")) {
            throw new IllegalArgumentException("Match link is not matches pattern. Example: http://wotreplays.ru/site/11271400#tundra-sh0tnik-ob_ekt_268_variant_4");
        }
    }

    private Elements getAllysPlayersInformation(Document data) {
        Elements elementsByClass = data.getElementsByClass("replay-teams__team");
        Element element = elementsByClass.get(0);
        Elements tableBodyData = element.getElementsByClass("team-table__body");
        Element tableBody = tableBodyData.get(0);
        return tableBody.getElementsByTag("tr");
    }

    private List<PlayerMatch> parseKOPM2Players(Elements players, String matchLink, PlatoonData platoonData) {
        Element replayOwnerElement = findReplayOwnerElement(players);
        if (replayOwnerElement == null) {
            LOGGER.error("Replay owner is not found for the match with link " + matchLink);
        } else {
            return players.stream()
                    .filter(filterPlatoonPlayers(platoonData))
                    .map(mapToPlayerMatch())
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private Predicate<Element> filterPlatoonPlayers(PlatoonData platoonData) {
        return element -> {
            String username = element.getElementsByClass("team-table__username").get(0).text();
            return platoonData.getPlatoonPlayers()
                    .stream()
                    .anyMatch(player -> username.contains(player.getName()));
        };
    }

    private Function<Element, PlayerMatch> mapToPlayerMatch() {
        return element -> {
            String username = element.getElementsByClass("team-table__username").get(0).text();
            username = username.replace("[KOPM2]", "").trim();
            Player player = new Player(username, DataUtils.getDrawGroup(username));
            return new PlayerMatch(player,
                    getClassStringValue(element, "team-table__tank"),
                    getClassIntegerValue(element, "team-table__damageDealt"),
                    getClassIntegerValue(element, "team-table__frags"));
        };
    }

    private String getClassStringValue(Element element, String className) {
        return element.getElementsByClass(className).get(0).text();
    }

    private Integer getClassIntegerValue(Element element, String className) {
        String value = element.getElementsByClass(className).get(0).text();
        return Integer.parseInt(value);
    }

    private MatchResult getMatchResult(Document stats, List<PlayerMatch> playerMatches) {
        Elements elementsByClass = stats.getElementsByClass("replay-stats__hat--win");
        Result result = Result.WIN;
        if (elementsByClass == null || elementsByClass.isEmpty())
            result = Result.LOSE;
        int matchPlatoonDamageDealt = playerMatches.parallelStream()
                .mapToInt(PlayerMatch::getDamage)
                .sum();
        int matchPlatoonFrags = playerMatches.parallelStream()
                .mapToInt(PlayerMatch::getFrags)
                .sum();
        int matchScore = (Result.WIN.equals(result) ? 3000 : 0) + matchPlatoonDamageDealt + (matchPlatoonFrags * 300);
        return MatchResult.builder()
                .result(result)
                .matchPlatoonDamageDealt(matchPlatoonDamageDealt)
                .matchPlatoonFrags(matchPlatoonFrags)
                .matchScore(matchScore)
                .build();
    }

    private LocalDateTime getMatchDate(Document stats) {
        Elements replay__info = stats.getElementsByClass("replay__info");
        List<String> data = replay__info.get(0).getElementsByTag("li")
                .stream()
                .filter(el -> el.text().matches(".*2018.*"))
                .map(Element::text)
                .collect(Collectors.toList());
        String s = data.get(0);
        String[] timeDate = s.split(" ");
        String date = "";
        String time = "";
        for (String d : timeDate) {
            if (d.matches("\\d{4}-\\d{2}-\\d{2}")) {
                date = d;
            } else if (d.matches("\\d{2}:\\d{2}")) {
                time = d;
            }
        }
        return LocalDateTime.parse(date + " " + time, dateTimeFormatter);
    }

    private Element findReplayOwnerElement(Elements players) {
        return players.stream()
                .filter(element -> element.hasClass("owner"))
                .findFirst()
                .orElse(null);
    }

    private Integer getPlatoonNumber(Element element) {
        Elements elementsByClass = element.getElementsByClass("team-table__platoon");
        if (elementsByClass == null || elementsByClass.isEmpty()) {
            Element platoonTag = elementsByClass.get(0);
            Elements span = platoonTag.getElementsByTag("span");
            if (span != null && !span.isEmpty()) {
                return Integer.parseInt(span.get(0).text());
            }
        }
        return 0;
    }

    private int getMatchDurationInSeconds(Document data) {
        Element element = data.getElementsByClass("replay-details__timings").get(0);
        Elements li = element.getElementsByClass("replay-details__table").get(0).getElementsByTag("li");
        for (Element liElement : li) {
            if (liElement.text().contains("Продолжительность боя")) {
                String span = liElement.getElementsByTag("span").get(0).text();
                String[] split = span.split(" ");
                boolean minutesFound = false;
                int minutes = 0;
                int seconds = 0;
                for (String s : split) {
                    if (s.matches("\\d{1,2}")) {
                        if (!minutesFound) {
                            minutes = Integer.parseInt(s);
                            minutesFound = true;
                        } else {
                            seconds = Integer.parseInt(s);
                        }
                    }
                }
                return (minutes * 60) + seconds;
            }
        }
        return 0;
    }

    private Document getData(String link) {
        phantomJSDriver.get(link);
        return Jsoup.parse(phantomJSDriver.getPageSource());
    }

    private List<String> getMatchesLinks(String username, Integer page) {
        String link = String.format("http://wotreplays.ru/site/index/version/63/player/%s/sort/uploaded_at.desc/page/%d/", username, page);
        Document data = getData(link);
        LOGGER.info(String.format("Start collecting links for the user %s with link %s", username, link));
        return data.getElementsByClass("link--pale_orange")
                .stream()
                .filter(element -> {
                    String text = element.text();
                    boolean result = text.matches(".*Турнир Чака.*")
                            && !text.contains("ПЕРЕИГР")
                            && !text.contains("Переигр")
                            && !text.contains("переигр");
                    if (result) {
                        LOGGER.info("Find link with name " + text);
                    }
                    return result;
                })
                .map(element -> "http://wotreplays.ru" + element.attr("href"))
                .collect(Collectors.toList());
    }

}
