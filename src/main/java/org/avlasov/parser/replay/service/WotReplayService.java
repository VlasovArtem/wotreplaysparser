package org.avlasov.parser.replay.service;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.avlasov.parser.config.properties.WotReplaysProperties;
import org.avlasov.parser.replay.entity.Replay;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.Set;

@Service
public class WotReplayService {

    private final ObjectMapper wotReplayParserObjectMapper;
    private final File allPageReplaysFile;
    private final File searchLinkReplaysFile;

    public WotReplayService(ObjectMapper wotReplayParserObjectMapper,
                            WotReplaysProperties wotReplaysProperties) {
        this.wotReplayParserObjectMapper = wotReplayParserObjectMapper;
        allPageReplaysFile = new File(wotReplaysProperties.getAllPageReplaysDataFilename());
        searchLinkReplaysFile = new File(wotReplaysProperties.getSearchLinkReplaysDataFilename());
    }

    public Set<Replay> readAllPageReplays() {
        return readData(allPageReplaysFile, Replay.class);
    }

    public Set<Replay> readSearchLinkReplays() {
        return readData(searchLinkReplaysFile, Replay.class);
    }

    public Set<Replay> readReplays(File file) {
        return readData(file, Replay.class);
    }

    public Replay readReplay(File file) {
        return readData(file, wotReplayParserObjectMapper.constructType(Replay.class));
    }

    public void writeAllPageReplays(Set<Replay> replays) {
        writeData(replays, allPageReplaysFile);
    }

    public void writeSearchLinkReplays(Set<Replay> replays) {
        writeData(replays, searchLinkReplaysFile);
    }

    public void writeReplays(Set<Replay> replays, File file) {
        writeData(replays, file);
    }

    public void writeReplay(Replay replay, File file) {
        writeData(replay, file);
    }

    private void writeData(Object data, File file) {
        try {
            wotReplayParserObjectMapper.writeValue(file, data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private <T> Set<T> readData(File file, Class<T> tClass) {
        return readData(file, wotReplayParserObjectMapper.getTypeFactory().constructCollectionLikeType(Set.class, tClass));
    }

    private <T> T readData(File file, JavaType javaType) {
        if (!file.exists()) {
            return null;
        }
        try {
            return wotReplayParserObjectMapper.readValue(file, javaType);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


}
