package org.avlasov.parser.site;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.avlasov.entity.match.*;
import org.avlasov.entity.match.enums.Result;
import org.avlasov.utils.DataUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.springframework.stereotype.Component;

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
@Component
public class ParseSiteData {

    private final static Logger LOGGER = LogManager.getLogger(ParseSiteData.class);
    private DateTimeFormatter dateTimeFormatter;
    private final PhantomJSDriver phantomJSDriver;
    private final DataUtils dataUtils;

    public ParseSiteData(PhantomJSDriver phantomJSDriver, DataUtils dataUtils) {
        this.phantomJSDriver = phantomJSDriver;
        this.dataUtils = dataUtils;
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

    public List<Match> parseMatches(Platoon platoon, Set<String> matchesLinks) {
        Objects.requireNonNull(platoon);
        Set<Match> platoonMatches = new HashSet<>();
        for (Player player : platoon.getPlayers()) {
            LOGGER.info(String.format("Start parsing matches for the player %s in the platoon %s", player.getName(), platoon.getPlatoonName()));
            platoonMatches.addAll(parseMatches(player.getName(), platoon, matchesLinks));
        }
        return platoonMatches
                .stream()
                .sorted(Comparator.comparing(Match::getMatchDate))
                .collect(Collectors.toList());
    }

    public List<Match> parseMatches(List<Platoon> platoons, Set<String> matchesLinks) {
        if (platoons != null && !platoons.isEmpty()) {
            long start = System.currentTimeMillis();
            LOGGER.info("Start parsing platoons matches");
            List<Match> collect = platoons.parallelStream()
                    .flatMap(platoon -> parseMatches(platoon, matchesLinks).stream())
                    .sorted(Comparator.comparing(Match::getMatchDate))
                    .collect(Collectors.toList());

            long end = System.currentTimeMillis();
            LOGGER.info(String.format("Parsing platoons data completed in %d seconds", ((end - start) / 1000)));
            return collect;
        }
        return Collections.emptyList();
    }

    public List<Match> parseMatches(String username, Platoon platoon, Set<String> matchesLinks) {
        if (username != null && !username.isEmpty() && platoon != null && matchesLinks != null && !matchesLinks.isEmpty()) {
            return matchesLinks.stream()
                    .map(link -> parseMatch(link, platoon))
                    .filter(Objects::nonNull)
                    .sorted(Comparator.comparing(Match::getMatchDate))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    public List<String> parseMatchesLinks(List<Platoon> platoon) {
        return null;
    }

    public List<String> parseMatchesLinks(Platoon platoon) {
        return null;
    }

    private Match parseMatch(String matchLink, Platoon platoon) {
        validateMatchLink(matchLink);
        String[] split = matchLink.split("#");
        Document data = getData(split[0]);
        List<PlayerMatch> playerMatches = parseKOPM2Players(getAllysPlayersInformation(data), matchLink, platoon);
        int maxPlatoonPlayers = 3;
        if (!playerMatches.isEmpty() && playerMatches.size() == maxPlatoonPlayers) {
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

    private List<PlayerMatch> parseKOPM2Players(Elements players, String matchLink, Platoon platoon) {
        Element replayOwnerElement = findReplayOwnerElement(players);
        if (replayOwnerElement == null) {
            LOGGER.error("Replay owner is not found for the match with link " + matchLink);
        } else {
            return players.stream()
                    .filter(filterPlatoonPlayers(platoon))
                    .map(mapToPlayerMatch())
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private Predicate<Element> filterPlatoonPlayers(Platoon platoon) {
        return element -> {
            String username = element.getElementsByClass("team-table__username").get(0).text();
            return platoon.getPlayers()
                    .stream()
                    .anyMatch(player -> username.contains(player.getName()));
        };
    }

    private Function<Element, PlayerMatch> mapToPlayerMatch() {
        return element -> {
            String username = element.getElementsByClass("team-table__username").get(0).text();
            username = username.replace("[KOPM2]", "").trim();
            Player player = new Player(username, dataUtils.getDrawGroup(username));
            Integer frags = getClassIntegerValue(element, "team-table__frags");
            return new PlayerMatch(player,
                    getClassStringValue(element, "team-table__tank"),
                    getClassIntegerValue(element, "team-table__damageDealt"),
                    frags < 0 ? 0 : frags);
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
