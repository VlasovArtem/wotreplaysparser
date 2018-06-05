package org.avlasov.parser.site;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.avlasov.entity.match.Match;
import org.avlasov.entity.match.enums.DrawGroup;
import org.avlasov.entity.statistic.*;
import org.avlasov.utils.DataUtils;
import org.avlasov.utils.MatchStatisticUtils;
import org.avlasov.utils.PlatoonStatisticUtils;
import org.avlasov.utils.PlayerStatisticUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static org.avlasov.utils.DrawGroupPlayerStatisticUtils.*;
import static org.avlasov.utils.MatchesStatisticUtils.*;
import static org.avlasov.utils.StatisticUtils.findBestByScore;
import static org.avlasov.utils.StatisticUtils.findWorstByScore;
import static org.avlasov.utils.VehicleStatisticUtils.getVehicleStatistics;

/**
 * Created By artemvlasov on 21/05/2018
 **/
@Component
public class Parser {

    private final String dataFolderPath;
    private final String matchesDataFileName;
    private final ObjectMapper objectMapper;
    private final DataUtils dataUtils;
    private final ParseSiteData parseSiteData;

    public Parser(ObjectMapper objectMapper, DataUtils dataUtils, ParseSiteData parseSiteData) {
        this.objectMapper = objectMapper;
        this.dataUtils = dataUtils;
        this.parseSiteData = parseSiteData;
        matchesDataFileName = "matches_data.json";
        dataFolderPath = "./data";
    }

    public void parseDataAll() throws IOException {
        parseData(ParseSiteData::parseAllPlatoonsMatches);
    }

    public void parseData(String username) throws IOException {
        parseData(pd -> pd.parseMatches(username, dataUtils.getPlatoonDataFromUser(username).get()));
    }

    private void parseData(Function<ParseSiteData, List<Match>> parserDataListFunction) throws IOException {
        File dataFolder = getDataFolder();
        List<Match> matches = parseSavedMatches(dataFolder);
        if (matches.isEmpty()) {
            matches = parserDataListFunction.apply(parseSiteData);
            File matchesData = new File(dataFolderPath + "/" + matchesDataFileName);
            if (matchesData.exists())
                matchesData.delete();
            objectMapper.writeValue(matchesData, matches);
        }
        parseData(matches);
    }

    private void parseData(List<Match> matches) throws IOException {
        writeStatistic(matches);
    }

    private File getDataFolder() throws IOException {
        File dataFolder = new File(dataFolderPath);
        if (!dataFolder.exists())
            dataFolder.createNewFile();
        return dataFolder;
    }

    private List<Match> parseSavedMatches(File dataFolder) throws IOException {
        List<Match> parsedMatched = new ArrayList<>();
        if (dataFolder != null) {
            File[] files = dataFolder.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (matchesDataFileName.equals(file.getName())) {
                        parsedMatched = objectMapper.readValue(file, TypeFactory.defaultInstance().constructCollectionLikeType(List.class, Match.class));
                    }
                }
            }
        }
        return parsedMatched;
    }

    private void writeStatistic(List<Match> matches) throws IOException {
        List<VehicleStatistic> vehicleStatistics = writeVehicleStatistics(matches);
        List<PlatoonStatistic> platoonStatistics = writePlatoonStatistics(matches);
        List<PlayerStatistic> playerStatistics = writePlayerStatistics(matches);
        Map<DrawGroup, List<DrawGroupPlayerStatistic>> drawGroupListMap = writeDrawGroupPlayerStatistic(playerStatistics);
        ChampionshipStatistic build = ChampionshipStatistic.builder()
                .matchesStatistic(calculateMatchesStatistic(matches))
                .bestMatch(findBestMatch(matches))
                .worstMatch(findWorstMatch(matches))
                .bestPlatoon(findBestByScore(platoonStatistics))
                .worstPlatoon(findWorstByScore(platoonStatistics))
                .bestDrawGroupsPlayer(findBestDrawGroupPlayer(drawGroupListMap))
                .worstDrawGroupsPlayers(findWorstDrawGroupPlayer(drawGroupListMap))
                .bestPlayer(findBestByScore(playerStatistics))
                .worstPlayer(findWorstByScore(playerStatistics))
                .bestVehicle(findBestByScore(vehicleStatistics))
                .matchStatistic(writeMatchStatistics(matches))
                .build();
        writeData("championship_statistic.json", build);
    }

    private Map<DrawGroup, List<DrawGroupPlayerStatistic>> writeDrawGroupPlayerStatistic(List<PlayerStatistic> playerStatistics) throws IOException {
        Map<DrawGroup, List<DrawGroupPlayerStatistic>> drawGroupListMap = collectDrawGroupPlayers(playerStatistics);
        writeData("draw_group_players_statistics.json", drawGroupListMap);
        return drawGroupListMap;
    }

    private List<VehicleStatistic> writeVehicleStatistics(List<Match> matches) throws IOException {
        List<VehicleStatistic> vehicleStatistics = getVehicleStatistics(matches);
        writeData("vehicle_statistics.json", vehicleStatistics);
        return vehicleStatistics;
    }

    private List<PlatoonStatistic> writePlatoonStatistics(List<Match> matches) throws IOException {
        List<PlatoonStatistic> platoonStatistics = PlatoonStatisticUtils.getPlatoonStatistics(matches);
        writeData("platoon_statistics.json", platoonStatistics);
        return platoonStatistics;
    }

    private List<PlayerStatistic> writePlayerStatistics(List<Match> matches) throws IOException {
        List<PlayerStatistic> playerStatistics = PlayerStatisticUtils.getPlayerStatistics(matches);
        writeData("player_statistics.json", playerStatistics);
        return playerStatistics;
    }

    private MatchStatistic writeMatchStatistics(List<Match> matches) throws IOException {
        MatchStatistic matchStatistic = MatchStatisticUtils.calculateMatchStatistic(matches);
        writeData("match_statistics.json", matchStatistic);
        return matchStatistic;
    }

    private <T> void writeData(String fileName, T statistic) throws IOException {
        File file = new File(dataFolderPath + "/" + fileName);
        if (file.exists())
            file.delete();
        else
            file.createNewFile();
        objectMapper.writeValue(file, statistic);
    }

}
