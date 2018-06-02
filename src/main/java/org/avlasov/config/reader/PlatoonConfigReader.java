package org.avlasov.config.reader;

import org.avlasov.config.PlatoonConfig;
import org.avlasov.config.entity.PlatoonData;
import org.avlasov.entity.match.Player;
import org.avlasov.entity.match.enums.DrawGroup;
import org.avlasov.parser.Parser;
import org.yaml.snakeyaml.Yaml;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created By artemvlasov on 21/05/2018
 **/
public class PlatoonConfigReader {

    public PlatoonConfig readData() {
        Yaml yaml = new Yaml();
        Map<String, List<Map<String, Object>>> load = yaml.load(Parser.class.getResourceAsStream("/platoons.yml"));
        List<PlatoonData> data = new ArrayList<>();
        for (Map.Entry<String, List<Map<String, Object>>> stringListEntry : load.entrySet()) {
            List<Player> platoonPlayers = new ArrayList<>();
            for (Map<String, Object> stringStringMap : stringListEntry.getValue()) {
                String nickname = "";
                int drawGroup = 0;
                for (Map.Entry<String, Object> stringStringEntry : stringStringMap.entrySet()) {
                    if ("nickname".equals(stringStringEntry.getKey())) {
                        nickname = stringStringEntry.getValue() + "";
                    } else {
                        drawGroup = (Integer) stringStringEntry.getValue();
                    }
                }
                platoonPlayers.add(new Player(nickname, drawGroup(drawGroup)));
            }
            data.add(new PlatoonData(stringListEntry.getKey(), platoonPlayers));
        }
        return new PlatoonConfig(data);
    }

    private DrawGroup drawGroup(int group) {
        switch (group) {
            case 1: return DrawGroup.FIRST;
            case 2: return DrawGroup.SECOND;
            case 3: return DrawGroup.THIRD;
            default: return DrawGroup.THIRD;
        }
    }

}
