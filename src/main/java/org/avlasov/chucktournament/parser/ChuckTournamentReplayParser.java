package org.avlasov.chucktournament.parser;

import lombok.extern.slf4j.Slf4j;
import org.avlasov.chucktournament.entity.ChuckTournamentPlayer;
import org.avlasov.chucktournament.entity.match.MapData;
import org.avlasov.chucktournament.entity.match.Match;
import org.avlasov.chucktournament.entity.match.MatchResult;
import org.avlasov.chucktournament.entity.match.Platoon;
import org.avlasov.chucktournament.entity.match.Player;
import org.avlasov.chucktournament.entity.match.PlayerMatch;
import org.avlasov.chucktournament.entity.match.enums.Result;
import org.avlasov.chucktournament.config.properties.PlatoonsProperties;
import org.avlasov.chucktournament.config.properties.TournamentProperties;
import org.avlasov.parser.builder.WotReplaysLinkUtils;
import org.avlasov.utils.DataUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ChuckTournamentReplayParser {

    private final PlatoonsProperties platoonsProperties;
    private final TournamentProperties tournamentProperties;
    private final PhantomJSDriver phantomJSDriver;
    private final WotReplaysLinkUtils wotReplaysLinkUtils;
    private final DateTimeFormatter matchDateTimeFormatter;

    public ChuckTournamentReplayParser(PhantomJSDriver phantomJSDriver,
                                       PlatoonsProperties platoonsProperties,
                                       TournamentProperties tournamentProperties,
                                       DateTimeFormatter matchDateTimeFormatter,
                                       WotReplaysLinkUtils wotReplaysLinkUtils) {
        this.phantomJSDriver = phantomJSDriver;
        this.platoonsProperties = platoonsProperties;
        this.tournamentProperties = tournamentProperties;
        this.matchDateTimeFormatter = matchDateTimeFormatter;
        this.wotReplaysLinkUtils = wotReplaysLinkUtils;
    }

    public List<Match> parseAllPlatoonsMatches() {
        long start = System.currentTimeMillis();
        log.info("Start parsing all platoons matches");
        Set<Match> allMatches = new HashSet<>();
        for (Platoon platoon : platoonsProperties.getPlatoons()) {
            Set<Match> platoonMatches = new HashSet<>();
            for (Player player : platoon.getPlayers()) {
                log.info(String.format("Start parsing matches for the player %s in the platoon %s", player.getName(), platoon.getPlatoonName()));
                List<Match> matches = parseMatches(player.getName(), platoon);
                if (matches.size() < tournamentProperties.getRequiredNumberOfReplays()) {
                    log.warn(String.format("Player with %s from the platoon %s has not all required matches links (%d is required - %d parsed)", player.getName(), platoon.getPlatoonName(), tournamentProperties.getRequiredNumberOfReplays(), matches.size()));
                } else if (matches.size() > tournamentProperties.getRequiredNumberOfReplays()) {
                    log.warn(String.format("Player with %s from the platoon %s has more then %d required matches links (%d is required - %d parsed)", player.getName(), platoon.getPlatoonName(), tournamentProperties.getRequiredNumberOfReplays(), tournamentProperties.getRequiredNumberOfReplays(), matches.size()));
                }
                platoonMatches.addAll(matches);
                if (platoonMatches.size() == tournamentProperties.getRequiredNumberOfReplays()) {
                    break;
                }
            }
            allMatches.addAll(platoonMatches);
        }
        long end = System.currentTimeMillis();
        log.info(String.format("Parsing platoons data completed in %d seconds", ((end - start) / 1000)));
        phantomJSDriver.close();
        return allMatches
                .stream()
                .sorted(Comparator.comparing(Match::getMatchDate))
                .collect(Collectors.toList());
    }

    public List<Match> parseMatches(String username, Platoon platoon) {
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
        log.info(String.format("%d matches links found for the user %s.", links.size(), username));
        List<Match> collect = links.stream()
                .map(link -> parseMatch(link, platoon))
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(Match::getMatchDate))
                .collect(Collectors.toList());
        phantomJSDriver.close();
        return collect;
    }

    private Match parseMatch(String matchLink, Platoon platoon) {
        validateMatchLink(matchLink);
        String[] split = matchLink.split("#");
        Document data = getData(split[0]);
        List<PlayerMatch> playerMatches = parseKOPM2Players(getAllysPlayersInformation(data), matchLink, platoon);
        if (!playerMatches.isEmpty() && playerMatches.size() == tournamentProperties.getMaxPlatoonPlayers()) {
            List<Player> players = playerMatches.parallelStream()
                    .map(PlayerMatch::getPlayer)
                    .collect(Collectors.toList());
            Platoon platoonData = new Platoon(players, DataUtils.getPlatoonName(playerMatches.get(0)));
            return Match.builder()
                    .playerMatches(playerMatches)
                    .mapData(getMapData(data))
                    .matchDurationInSeconds(getMatchDurationInSeconds(data))
                    .matchDate(getMatchDate(data))
                    .platoon(platoonData)
                    .matchLink(matchLink)
                    .result(getMatchResult(data, playerMatches))
                    .build();
        }
        return null;
    }

    private List<String> getMatchesLinks(String username, Integer page) {
        String link = wotReplaysLinkUtils.getBuilder()
                .withPlayer(username)
                .withPage(page)
                .build();
        Document data = getData(link);
        log.info(String.format("Start collecting links for the user %s with link %s", username, link));
        return data.getElementsByClass("link--pale_orange")
                .stream()
                .filter(element -> {
                    String text = element.text();
                    boolean result = text.matches(".*Турнир Чака.*")
                            && !text.contains("ПЕРЕИГР")
                            && !text.contains("Переигр")
                            && !text.contains("переигр");
                    if (result) {
                        log.info("Find link with name " + text);
                    }
                    return result;
                })
                .map(element -> "http://wotreplays.ru" + element.attr("href"))
                .collect(Collectors.toList());
    }

    private List<PlayerMatch> parseKOPM2Players(Elements players, String matchLink, Platoon platoon) {
        Element replayOwnerElement = findReplayOwnerElement(players);
        if (replayOwnerElement == null) {
            log.error("Replay owner is not found for the match with link " + matchLink);
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

    protected Function<Element, PlayerMatch> mapToPlayerMatch() {
        return element -> {
            String username = element.getElementsByClass("team-table__username").get(0).text();
            username = username.replace("[KOPM2]", "").trim();
            ChuckTournamentPlayer player = new ChuckTournamentPlayer(username, DataUtils.getDrawGroup(username));
            Integer frags = getClassIntegerValue(element, "team-table__frags");
            return new PlayerMatch(player,
                    getClassStringValue(element, "team-table__tank"),
                    getClassIntegerValue(element, "team-table__damageDealt"),
                    frags < 0 ? 0 : frags);
        };
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
        Elements replayInfo = stats.getElementsByClass("replayInfo");
        List<String> data = replayInfo.get(0).getElementsByTag("li")
                .stream()
                .filter(el -> el.text().matches(String.format(".*(%d|%d).*", LocalDate.now().getYear(), LocalDate.now().getYear() - 1)))
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
        return LocalDateTime.parse(date + " " + time, matchDateTimeFormatter);
    }

    protected MapData getMapData(Document stats) {
        String[] split = stats.getElementsByClass("replay-stats__subtitle").get(0).text().split("–");
        return new MapData(split[0].trim(), split[1].trim());
    }

    public Match parseMatch(String matchLink) {
        validateMatchLink(matchLink);
        String[] split = matchLink.split("#");
        Document data = getData(split[0]);
        Elements allysPlayersInformation = getAllysPlayersInformation(data);

//        List<PlayerMatch> playerMatches = parseKOPM2Players(, matchLink, platoon);
//        if (!playerMatches.isEmpty() && playerMatches.size() == tournamentProperties.getMaxPlatoonPlayers()) {
//            List<Player> players = playerMatches.parallelStream()
//                    .map(PlayerMatch::getPlayer)
//                    .collect(Collectors.toList());
//            Platoon platoon = new Platoon(players, DataUtils.getPlatoonName(playerMatches.get(0)));
//            return Match.builder()
//                    .playerMatches(playerMatches)
//                    .mapData(getMapData(data))
//                    .matchDurationInSeconds(getMatchDurationInSeconds(data))
//                    .matchDate(getMatchDate(data))
//                    .platoon(platoon)
//                    .matchLink(matchLink)
//                    .result(getMatchResult(data, playerMatches))
//                    .build();
//        }
        return null;
    }

    private void validateMatchLink(String link) {
        if (!link.matches("http://wotreplays\\.ru/site/\\d+#.*")) {
            throw new IllegalArgumentException("Match link is not matches pattern. Example: http://wotreplays.ru/site/11271400#tundra-sh0tnik-ob_ekt_268_variant_4");
        }
    }

    private String getClassStringValue(Element element, String className) {
        return element.getElementsByClass(className).get(0).text();
    }

    private Integer getClassIntegerValue(Element element, String className) {
        String value = element.getElementsByClass(className).get(0).text();
        return Integer.parseInt(value);
    }

    private List<PlayerMatch> parsePlayers(Elements players) {
        return players.stream()
                .map(mapToPlayerMatch())
                .collect(Collectors.toList());
    }

    private Elements getAllysPlayersInformation(Document data) {
        Elements elementsByClass = data.getElementsByClass("replay-teams__team");
        Element element = elementsByClass.get(0);
        Elements tableBodyData = element.getElementsByClass("team-table__body");
        Element tableBody = tableBodyData.get(0);
        return tableBody.getElementsByTag("tr");
    }

}
