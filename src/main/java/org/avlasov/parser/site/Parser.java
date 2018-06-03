package org.avlasov.parser.site;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.avlasov.entity.match.Match;
import org.avlasov.entity.match.enums.DrawGroup;
import org.avlasov.entity.statistic.*;
import org.avlasov.utils.DataUtils;
import org.avlasov.utils.MatchStatisticUtils;
import org.avlasov.utils.PlatoonStatisticUtils;
import org.avlasov.utils.PlayerStatisticUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Handler;
import java.util.logging.Logger;

import static org.avlasov.utils.DrawGroupPlayerStatisticUtils.*;
import static org.avlasov.utils.MatchesStatisticUtils.*;
import static org.avlasov.utils.StatisticUtils.findBestByScore;
import static org.avlasov.utils.StatisticUtils.findWorstByScore;
import static org.avlasov.utils.VehicleStatisticUtils.getVehicleStatistics;

/**
 * Created By artemvlasov on 21/05/2018
 **/
public class Parser {

    private final String dataFolderPath;
    private final String matchesDataFileName;
    private final ObjectMapper objectMapper;

    public Parser() {
        matchesDataFileName = "matches_data.json";
        dataFolderPath = "./data";
        objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        disableDefaultLogger();
    }

    private void disableDefaultLogger() {
        Logger globalLogger = Logger.getLogger("");
        Handler[] handlers = globalLogger.getHandlers();
        for (Handler handler : handlers) {
            globalLogger.removeHandler(handler);
        }
    }

    public void parseDataAll() throws IOException {
        parseData(ParseSiteData::parseAllPlatoonsMatches);
    }

    public void parseData(String username) throws IOException {
        parseData(pd -> pd.parseMatches(username, DataUtils.getPlatoonDataFromUser(username).get()));
    }

    private void parseData(Function<ParseSiteData, List<Match>> parserDataListFunction) throws IOException {
        File dataFolder = getDataFolder();
        List<Match> matches = parseSavedMatches(dataFolder);
        if (matches.isEmpty()) {
            matches = parserDataListFunction.apply(new ParseSiteData());
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

    public static void main(String[] args) throws IOException {
        Parser parser = new Parser();
        parser.parseDataAll();
    }

}
