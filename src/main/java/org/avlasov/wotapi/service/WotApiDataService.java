package org.avlasov.wotapi.service;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.avlasov.wotapi.config.properties.WotApiProperties;
import org.avlasov.wotapi.entity.arena.Arena;
import org.avlasov.wotapi.entity.data.WotApiDataInformation;
import org.avlasov.wotapi.entity.tankopedia.TankopediaInfo;
import org.avlasov.wotapi.entity.vehicle.Vehicle;
import org.avlasov.wotapi.response.WotApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Spliterators;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.avlasov.wotapi.enums.WotApiParameter.APPLICATION_ID;
import static org.avlasov.wotapi.enums.WotApiParameter.PAGE_NO;

@Component
@Slf4j
public class WotApiDataService {

    private final WotApiProperties wotApiProperties;
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;
    private final File vehiclesDataFile;
    private final File arenasDataFile;
    private final File tankopediaDataFile;
    private final File wotApiDataInformationFile;

    public WotApiDataService(WotApiProperties wotApiProperties,
                             ObjectMapper wotApiObjectMapper,
                             RestTemplate wotApiRestTemplate) {
        this.wotApiProperties = wotApiProperties;
        this.objectMapper = wotApiObjectMapper;
        this.restTemplate = wotApiRestTemplate;
        vehiclesDataFile = new File(wotApiProperties.getVehiclesDataFilename());
        arenasDataFile = new File(wotApiProperties.getArenasDataFilename());
        tankopediaDataFile = new File(wotApiProperties.getTankopediaDataFilename());
        wotApiDataInformationFile = new File(wotApiProperties.getWotApiDataInformationFilename());
    }

    public TankopediaInfo getTankopediaInfo() {
        return readData(tankopediaDataFile, objectMapper.constructType(TankopediaInfo.class));
    }

    public List<Vehicle> getVehicles() {
        return readData(vehiclesDataFile,
                objectMapper.getTypeFactory().constructCollectionLikeType(List.class, Vehicle.class));
    }

    public List<Arena> getArenas() {
        return readData(arenasDataFile,
                objectMapper.getTypeFactory().constructCollectionLikeType(List.class, Arena.class));
    }

    @PostConstruct
    public void postInit() {
        TankopediaInfo tankopediaInfo = getTankopediaInfoFromUrl()
                .orElseThrow(() -> new RuntimeException("Tankopedia information is not collected from the url " +
                        wotApiProperties.getTankopediaApiUrl()));

        if (!vehiclesDataFile.exists() || !arenasDataFile.exists() || !wotApiDataInformationFile.exists()) {
            writeWotApiData(tankopediaInfo);
        } else {
            WotApiDataInformation wotApiDataInformation =
                    readData(wotApiDataInformationFile, objectMapper.constructType(WotApiDataInformation.class));

            if (wotApiDataInformation.getLatestCollectedWotVersion().equals(tankopediaInfo.getGameVersion())) {
                log.info(String.format("Vehicles and Arenas is already collected for the game version %s",
                        tankopediaInfo.getGameVersion()));
                return;
            } else {
                writeWotApiData(tankopediaInfo);
            }
        }

        writeData(tankopediaDataFile, tankopediaInfo);
    }

    Optional<TankopediaInfo> getTankopediaInfoFromUrl() {
        URI uri = UriComponentsBuilder
                .fromHttpUrl(wotApiProperties.getTankopediaApiUrl())
                .queryParam(APPLICATION_ID.getName(), wotApiProperties.getApplicationId())
                .build()
                .toUri();

        ResponseEntity<WotApiResponse> tankopediaResponseInfo = restTemplate.getForEntity(uri, WotApiResponse.class);
        if (tankopediaResponseInfo.getBody() != null && !"error".equals(tankopediaResponseInfo.getBody().getStatus())) {
            return Optional.ofNullable(readData(tankopediaResponseInfo.getBody().getData(),
                    objectMapper.constructType(TankopediaInfo.class)));
        }
        return Optional.empty();
    }

    List<Vehicle> getVehiclesFromUrl() {
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder
                .fromHttpUrl(wotApiProperties.getVehiclesApiUrl())
                .queryParam(APPLICATION_ID.getName(), wotApiProperties.getApplicationId())
                .queryParam(PAGE_NO.getName(), 1);

        ResponseEntity<WotApiResponse> firstPageVehicleResult =
                restTemplate.getForEntity(uriComponentsBuilder.build().toUri(), WotApiResponse.class);

        int firstPageNumber = 1;
        if (firstPageVehicleResult.getBody() != null) {
            int pages = firstPageVehicleResult.getBody().getMeta().getPageTotal();
            List<Vehicle> vehicles = new ArrayList<>(readData(firstPageVehicleResult.getBody().getData(), Vehicle.class));
            if (pages > firstPageNumber) {
                for (int i = firstPageNumber + 1; i < pages; i++) {
                    URI uri = uriComponentsBuilder.queryParam(PAGE_NO.getName(), i)
                            .build()
                            .toUri();
                    ResponseEntity<WotApiResponse> pageResult = restTemplate.getForEntity(uri, WotApiResponse.class);
                    if (firstPageVehicleResult.getBody() != null) {
                        vehicles.addAll(readData(pageResult.getBody().getData(), Vehicle.class));
                    }
                }
            }
            return vehicles;
        }
        return Collections.emptyList();
    }

    List<Arena> getArenasFromUrl() {
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder
                .fromHttpUrl(wotApiProperties.getArenasApiUrl())
                .queryParam(APPLICATION_ID.getName(), wotApiProperties.getApplicationId());

        ResponseEntity<WotApiResponse> result =
                restTemplate.getForEntity(uriComponentsBuilder.build().toUri(), WotApiResponse.class);

        if (result.getBody() != null) {
            return readData(result.getBody().getData(), Arena.class);
        }
        return Collections.emptyList();
    }

    private void writeWotApiData(TankopediaInfo tankopediaInfo) {
        writeData(vehiclesDataFile, getVehiclesFromUrl());
        writeData(arenasDataFile, getArenasFromUrl());

        WotApiDataInformation wotApiDataInformation = new WotApiDataInformation();
        wotApiDataInformation.setLatestCollectedWotVersion(tankopediaInfo.getGameVersion());
        writeData(wotApiDataInformationFile, wotApiDataInformation);

        log.info(String.format("Vehicles and Arenas information collected for the game version %s",
                tankopediaInfo.getGameVersion()));
    }

    @SneakyThrows
    private <T> List<T> readData(byte[] data, Class<T> tClass) {
        JsonNode jsonNode = objectMapper.readTree(data);
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(jsonNode.elements(), 0), false)
                .map(jsonNodeVehicleFunction(tClass))
                .collect(Collectors.toList());
    }

    private <T> Function<JsonNode, T> jsonNodeVehicleFunction(Class<T> tClass) {
        return jsonNode -> {
            try {
                return objectMapper.readValue(jsonNode.traverse(), tClass);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };
    }

    @SneakyThrows
    private <T> T readData(File file, JavaType javaType) {
        return objectMapper.readValue(file, javaType);
    }

    @SneakyThrows
    private <T> T readData(byte[] bytes, JavaType javaType) {
        return objectMapper.readValue(bytes, javaType);
    }

    @SneakyThrows
    private void writeData(File file, Object object) {
        objectMapper.writeValue(file, object);
    }

}
