package org.avlasov.writer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.avlasov.entity.match.enums.DrawGroup;
import org.avlasov.entity.statistic.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created By artemvlasov on 09/06/2018
 **/
@Component
public class StatisticWriter {

    private final static Logger LOGGER = LogManager.getLogger(StatisticWriter.class);
    private final String dataFolderPath;
    private final ObjectMapper objectMapper;

    public StatisticWriter(@Value("${json.data.folder.name}") String dataFolderPath, ObjectMapper objectMapper) {
        this.dataFolderPath = dataFolderPath;
        this.objectMapper = objectMapper;
    }

    public void writeChampionshipStatistic(ChampionshipStatistic championshipStatistic) throws IOException {
        writeData("championship_statistic.json", championshipStatistic);
    }

    public void writeDrawGroupPlayerStatistic(Map<DrawGroup, List<DrawGroupPlayerStatistic>> drawGroupListMap) throws IOException {
        writeData("draw_group_players_statistics.json", drawGroupListMap);
    }

    public void writeVehicleStatistics(List<VehicleStatistic> vehicleStatistics) throws IOException {
        writeData("vehicle_statistics.json", vehicleStatistics);
    }

    public void writePlatoonStatistics(List<PlatoonStatistic> platoonStatistics) throws IOException {
        writeData("platoon_statistics.json", platoonStatistics);
    }

    public void writePlayerStatistics(List<PlayerStatistic> playerStatistics) throws IOException {
        writeData("player_statistics.json", playerStatistics);
    }

    public void writeMatchStatistics(MatchStatistic matchStatistic) throws IOException {
        writeData("match_statistics.json", matchStatistic);
    }

    public <T> void writeData(String filename, T statistic) throws IOException {
        if (statistic != null && filename != null && !filename.isEmpty()) {
            if (dataFolderPath != null && !dataFolderPath.isEmpty()) {
                File folder = new File(dataFolderPath);
                if (!folder.exists())
                    folder.createNewFile();
                File file = new File(dataFolderPath + "/" + filename);
                if (file.exists())
                    file.delete();
                else
                    file.createNewFile();
                objectMapper.writeValue(file, statistic);
            } else {
                throw new IllegalArgumentException("Data folder path is empty or null");
            }
        } else {
            LOGGER.warn("Statistic data is null or filename is null. Data was not written to the file " + filename);
        }
    }

}
