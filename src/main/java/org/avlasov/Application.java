package org.avlasov;

import lombok.extern.slf4j.Slf4j;
import org.avlasov.config.ApplicationConfig;
import org.avlasov.parser.builder.WotReplaysLinkUtils;
import org.avlasov.parser.builder.enums.BattleType;
import org.avlasov.parser.replay.WotReplayParser;
import org.avlasov.parser.replay.entity.Replay;
import org.avlasov.parser.replay.service.WotReplayService;
import org.avlasov.statistic.config.properties.StatisticProperties;
import org.avlasov.statistic.entity.ArenasStatisticByVehicle;
import org.avlasov.statistic.entity.ArenasStatisticByVehicleType;
import org.avlasov.statistic.entity.data.StatisticArenaInfo;
import org.avlasov.statistic.service.ArenaStatisticService;
import org.avlasov.wotapi.entity.tankopedia.TankopediaInfo;
import org.avlasov.wotapi.service.WotApiDataService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Import;

import java.io.File;
import java.util.List;
import java.util.Set;

@SpringBootApplication
@Import({
        ApplicationConfig.class,
})
@Slf4j
public class Application implements CommandLineRunner {

    private final WotReplaysLinkUtils wotReplaysLinkUtils;
    private final WotApiDataService wotApiDataService;
    private final ArenaStatisticService arenaStatisticService;
    private final WotReplayParser wotReplayParser;
    private final WotReplayService wotReplayService;
    private final StatisticProperties statisticProperties;

    public Application(WotReplaysLinkUtils wotReplaysLinkUtils,
                       WotApiDataService wotApiDataService,
                       ArenaStatisticService arenaStatisticService,
                       WotReplayParser wotReplayParser,
                       WotReplayService wotReplayService,
                       StatisticProperties statisticProperties) {
        this.wotReplaysLinkUtils = wotReplaysLinkUtils;
        this.wotApiDataService = wotApiDataService;
        this.arenaStatisticService = arenaStatisticService;
        this.wotReplayParser = wotReplayParser;
        this.wotReplayService = wotReplayService;
        this.statisticProperties = statisticProperties;
    }

    public static void main(String[] args) {
        new SpringApplicationBuilder()
                .sources(Application.class)
                .build()
                .run(args);
    }

    @Override
    public void run(String... args) throws Exception {
        TankopediaInfo tankopediaInfo = wotApiDataService.getTankopediaInfo();

        String wotReplaysSearchLink = wotReplaysLinkUtils.getBuilder()
                .withWotVersion(tankopediaInfo.getGameVersion())
                .withBattleType(BattleType.STANDART_BATTLE)
                .build();

        Set<Replay> replays = wotReplayParser.parseBySearchLinkAllPages(wotReplaysSearchLink);

        log.info("");

        wotReplayService.writeAllPageReplays(replays);

        List<ArenasStatisticByVehicle> arenasStatisticByVehicles = arenaStatisticService.collectArenasStatisticByVehicles(replays);
        List<ArenasStatisticByVehicleType> arenasStatisticByVehicleTypes = arenaStatisticService.collectArenasStatisticByVehicleTypes(replays);

        arenaStatisticService.writeListArenasStatistic(arenasStatisticByVehicles, ArenasStatisticByVehicle.class);
        arenaStatisticService.writeListArenasStatistic(arenasStatisticByVehicleTypes, ArenasStatisticByVehicleType.class);

        List<ArenasStatisticByVehicle> arenasStatisticByVehiclesWithLimit2 =
                arenaStatisticService.limitData(arenasStatisticByVehicles, StatisticArenaInfo::compareTo,
                        2, ArenasStatisticByVehicle.class);

        List<ArenasStatisticByVehicleType> arenasStatisticByVehicleTypesWithLimit2 =
                arenaStatisticService.limitData(arenasStatisticByVehicleTypes, StatisticArenaInfo::compareTo,
                2, ArenasStatisticByVehicleType.class);

        arenaStatisticService.writeData(new File(String.format(statisticProperties.getVehicleArenaStatisticFilename(), "limit-2")), arenasStatisticByVehiclesWithLimit2);

        arenaStatisticService.writeData(new File(String.format(statisticProperties.getVehicleTypeArenaStatisticFilenamePattern(), "limit-2")), arenasStatisticByVehicleTypesWithLimit2);

        System.exit(0);
    }
}
