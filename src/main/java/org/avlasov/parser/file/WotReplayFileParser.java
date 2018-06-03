package org.avlasov.parser.file;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.avlasov.entity.wotreplay.WotReplayInfo;
import org.avlasov.entity.wotreplay.WotReplayMatch;
import org.avlasov.entity.wotreplay.WotReplayOwnerInfo;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created By artemvlasov on 02/06/2018
 **/
public class WotReplayFileParser {

    private final static Logger LOGGER = LogManager.getLogger(WotReplayFileParser.class);
    private final ObjectMapper objectMapper;

    public WotReplayFileParser() {
        objectMapper = new ObjectMapper();
    }

    public Optional<WotReplayInfo> parseWotReplay(String replayPath) {
        verifyReplayPath(replayPath);
        File replayFile = new File(replayPath);
        if (!replayFile.exists()) {
            String message = String.format("Replay with path %s is not exists.", replayPath);
            LOGGER.error(message);
            throw new IllegalArgumentException(message);
        }
        LOGGER.info("Start parsing wot replay " + replayFile.getName());
        try (FileInputStream is = new FileInputStream(replayFile)) {
            List<String> replayDataJson = findReplayDataJson(is);
            if (isCorruptedReplay(replayDataJson)) {
                LOGGER.warn("Replay with name " + replayFile.getName() + " is corrupted.");
                return Optional.empty();
            }
            WotReplayInfo wotReplayInfo = new WotReplayInfo(findReplayOwnerInfo(replayDataJson), findWotReplayMatch(replayDataJson));
            LOGGER.info("Completed parsing replay " + replayFile.getName());
            return Optional.of(wotReplayInfo);
        } catch (IOException | DecoderException  e) {
            LOGGER.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private boolean isCorruptedReplay(List<String> wotReplayInfo) {
        return wotReplayInfo.stream()
                .noneMatch(s -> s.contains("arenaUniqueID"));
    }

    private void verifyReplayPath(String path) {
        if (!path.matches(".*wotreplay")) {
            String message = "WOT replay is not matching pattern .*wotreplay";
            LOGGER.error(message);
            throw new IllegalArgumentException(message);
        }
    }

    private List<String> findReplayDataJson(FileInputStream fis) throws IOException, DecoderException {
        int wotReplayInfoJsonElementsNumber = 4;
        int brackets = 0;
        boolean jsonElementEnterIsFound = false;
        List<String> stringList = new ArrayList<>();
        StringBuilder dataInfo = new StringBuilder();
        int read;
        while ((read = fis.read()) != -1) {
            String s = Integer.toHexString(read);
            if ("7B".equalsIgnoreCase(s)) {
                brackets++;
                jsonElementEnterIsFound = true;
            } else if ("7D".equalsIgnoreCase(s)) {
                brackets--;
            }
            if (jsonElementEnterIsFound)
                dataInfo.append(s);
            if (brackets == 0 && jsonElementEnterIsFound) {
                String data = dataInfo.toString();
                stringList.add(new String(Hex.decodeHex(data.toCharArray())));
                dataInfo = new StringBuilder();
                jsonElementEnterIsFound = false;
            }
            if (stringList.size() == wotReplayInfoJsonElementsNumber)
                return stringList;
        }
        return stringList;
    }

    private <T> T readWotReplayInfo(String replayDataJson, Class<T> tClass) throws IOException {
        return objectMapper.readValue(replayDataJson, tClass);
    }

    private WotReplayOwnerInfo findReplayOwnerInfo(List<String> jsonData) throws IOException {
        for (String jsonDatum : jsonData) {
            if (jsonDatum.contains("clientVersionFromXml")) {
                return readWotReplayInfo(jsonDatum, WotReplayOwnerInfo.class);
            }
        }
        LOGGER.warn("Wot Replay Owner Info is not found");
        return null;
    }

    private WotReplayMatch findWotReplayMatch(List<String> jsonData) throws IOException {
        for (String jsonDatum : jsonData) {
            if (jsonDatum.contains("arenaUniqueID")) {
                return readWotReplayInfo(jsonDatum, WotReplayMatch.class);
            }
        }
        LOGGER.warn("Wot Replay Match is not found");
        return null;
    }

}
