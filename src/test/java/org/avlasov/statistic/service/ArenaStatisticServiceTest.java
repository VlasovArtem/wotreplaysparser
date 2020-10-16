package org.avlasov.statistic.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.avlasov.parser.config.ParserConfig;
import org.avlasov.parser.replay.entity.Replay;
import org.avlasov.statistic.config.properties.StatisticProperties;
import org.avlasov.statistic.entity.ArenasStatisticByVehicle;
import org.avlasov.statistic.entity.ArenasStatisticByVehicleType;
import org.avlasov.statistic.entity.data.StatisticArenaInfo;
import org.avlasov.wotapi.config.WotApiConfig;
import org.avlasov.wotapi.entity.arena.Arena;
import org.avlasov.wotapi.entity.vehicle.Vehicle;
import org.avlasov.wotapi.enums.VehicleType;
import org.avlasov.wotapi.service.WotApiDataService;
import org.hamcrest.collection.IsCollectionWithSize;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ArenaStatisticServiceTest {

    private static ObjectMapper replaysObjectMapper;
    private static ObjectMapper dataObjectMapper;
    @Mock
    private WotApiDataService wotApiDataService;
    private ArenaStatisticService findArenasToExclude;

    private static Set<Replay> replays;
    private static List<Vehicle> vehicles;
    private static List<Arena> arenas;

    @BeforeClass
    public static void init() throws Exception {
        replaysObjectMapper = new ParserConfig().wotReplayParserObjectMapper();
        dataObjectMapper = new WotApiConfig().wotApiObjectMapper();
        replays = readReplays();
        vehicles = readVehicles();
        arenas = readArenas();
    }

    @Before
    public void setUp() throws Exception {
        when(wotApiDataService.getArenas()).thenReturn(arenas);
        when(wotApiDataService.getVehicles()).thenReturn(vehicles);
        StatisticProperties statisticProperties = mock(StatisticProperties.class, invocation -> "test");

        findArenasToExclude = new ArenaStatisticService(wotApiDataService, statisticProperties, replaysObjectMapper);
    }

    @Test
    public void collectArenasStatisticByVehicleTypes() {
        List<ArenasStatisticByVehicleType> arenasStatisticByVehicleTypes = findArenasToExclude.collectArenasStatisticByVehicleTypes(replays);

        assertNotNull(arenasStatisticByVehicleTypes);

        List<ArenasStatisticByVehicleType> collect = arenasStatisticByVehicleTypes.stream()
                .filter(arenasStatisticByVehicleType -> VehicleType.SPG == arenasStatisticByVehicleType.getVehicleType())
                .collect(Collectors.toList());

        assertNotNull(collect);
        assertThat(collect, IsCollectionWithSize.hasSize(1));

        ArenasStatisticByVehicleType arenasStatisticByVehicleType = collect.get(0);

        List<StatisticArenaInfo> asiaGreatWall = arenasStatisticByVehicleType.getStatisticArenaInfos()
                .stream()
                .filter(statisticArenaInfo -> "59_asia_great_wall".equals(statisticArenaInfo.getStatisticArena().getArenaId()))
                .collect(Collectors.toList());

        StatisticArenaInfo asiaGreatWallActual = asiaGreatWall.get(0);
        StatisticArenaInfo asiaGreatWallExpected = new StatisticArenaInfo(asiaGreatWallActual.getStatisticArena(), 12, 6, 6, 422.75, 0.5, 0.08333333333333333,	876.5, 422.75, 0, 517.0, 0);
        assertEquals(asiaGreatWallExpected, asiaGreatWallActual);

    }

    @Test
    public void collectArenasStatisticByVehicles() throws Exception {
        List<ArenasStatisticByVehicle> arenasStatisticByVehicles = findArenasToExclude.collectArenasStatisticByVehicles(replays);

        assertNotNull(arenasStatisticByVehicles);

        List<ArenasStatisticByVehicle> collect = arenasStatisticByVehicles
                .stream()
                .filter(arenasStatisticByVehicle -> "R110_Object_260".equals(arenasStatisticByVehicle.getStatisticVehicle().getTag()))
                .collect(Collectors.toList());

        assertNotNull(collect);
        assertThat(collect, IsCollectionWithSize.hasSize(1));

        ArenasStatisticByVehicle arenasStatisticByVehicle = collect.get(0);

        assertNotNull(arenasStatisticByVehicle);

        List<StatisticArenaInfo> airFieldArena = arenasStatisticByVehicle.getStatisticArenaInfos()
                .stream()
                .filter(statisticArenaInfo -> "31_airfield".equals(statisticArenaInfo.getStatisticArena().getArenaId()))
                .collect(Collectors.toList());

        assertNotNull(airFieldArena);
        assertThat(airFieldArena, IsCollectionWithSize.hasSize(1));

        StatisticArenaInfo airFieldActual = airFieldArena.get(0);
        StatisticArenaInfo airFieldExpected = new StatisticArenaInfo(airFieldActual.getStatisticArena(), 1, 1, 0, 816.0, 1, 3, 2997, 816, 1, 1278, 0);
        assertEquals(airFieldExpected, airFieldActual);

        List<StatisticArenaInfo> westFieldArena = arenasStatisticByVehicle.getStatisticArenaInfos()
                .stream()
                .filter(statisticArenaInfo -> "23_westfeld".equals(statisticArenaInfo.getStatisticArena().getArenaId()))
                .collect(Collectors.toList());

        assertNotNull(westFieldArena);
        assertThat(westFieldArena, IsCollectionWithSize.hasSize(1));

        StatisticArenaInfo westFieldActual = westFieldArena.get(0);
        StatisticArenaInfo westFieldExpected = new StatisticArenaInfo(westFieldActual.getStatisticArena(), 2, 2, 0, 1287.5, 1, 5, 5709.5, 1287.5, 3, 1844, 0);
        assertEquals(westFieldExpected, westFieldActual);

        List<StatisticArenaInfo> mannerheimLineArena = arenasStatisticByVehicle.getStatisticArenaInfos()
                .stream()
                .filter(statisticArenaInfo -> "38_mannerheim_line".equals(statisticArenaInfo.getStatisticArena().getArenaId()))
                .collect(Collectors.toList());

        assertNotNull(mannerheimLineArena);
        assertThat(mannerheimLineArena, IsCollectionWithSize.hasSize(1));

        StatisticArenaInfo mannerheimLineActual = mannerheimLineArena.get(0);
        StatisticArenaInfo mannerheimLineExpected = new StatisticArenaInfo(mannerheimLineActual.getStatisticArena(), 10, 8, 2, 1068.3, 0.8, 4.4, 5497.8, 1068.3, 3.5, 1357, 0);
        assertEquals(mannerheimLineExpected, mannerheimLineActual);

    }

    private static Set<Replay> readReplays() throws Exception {
        return replaysObjectMapper.readValue(ArenaStatisticServiceTest.class.getResourceAsStream("replays.json"),
                replaysObjectMapper.getTypeFactory().constructCollectionLikeType(Set.class, Replay.class));
    }

    private static List<Arena> readArenas() throws Exception {
        return readList(dataObjectMapper, new FileInputStream(new File("./wot-data/arenas.json")), Arena.class);
    }

    public static List<Vehicle> readVehicles() throws Exception {
        return readList(dataObjectMapper, new FileInputStream(new File("./wot-data/vehicles.json")), Vehicle.class);
    }

    private static <T> List<T> readList(ObjectMapper objectMapper, InputStream inputStream, Class<T> tClass) throws Exception {
        return objectMapper.readValue(inputStream,
                objectMapper.getTypeFactory().constructCollectionLikeType(List.class, tClass));
    }

}