package org.avlasov.reader;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.avlasov.entity.match.Match;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * Created By artemvlasov on 09/06/2018
 **/
@Component
public class MatchesReader {

    private final ObjectMapper objectMapper;
    private final String dataFolderPath;
    private final String matchesDataFileName;

    public MatchesReader(@Value("${json.data.folder.name}") String dataFolderPath,
                         @Value("${json.data.matches.file.name}") String matchesDataFileName,
                         ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.matchesDataFileName = matchesDataFileName;
        this.dataFolderPath = dataFolderPath;
    }

    public List<Match> readMatches() throws IOException {
        File dataFolder = new File(dataFolderPath);
        if (dataFolder.exists()) {
            File[] folderFiles = dataFolder.listFiles();
            if (folderFiles != null) {
                for (File folderFile : folderFiles) {
                    if (matchesDataFileName.equals(folderFile.getName()))
                        return objectMapper.readValue(folderFile, TypeFactory.defaultInstance().constructCollectionLikeType(List.class, Match.class));
                }
            }
        }
        return Collections.emptyList();
    }

}
