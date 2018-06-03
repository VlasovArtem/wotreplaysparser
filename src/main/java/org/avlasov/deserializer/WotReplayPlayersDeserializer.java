package org.avlasov.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.avlasov.entity.wotreplay.WotReplayPlayer;

import java.io.IOException;
import java.util.*;

/**
 * Created By artemvlasov on 02/06/2018
 **/
public class WotReplayPlayersDeserializer extends JsonDeserializer<List<WotReplayPlayer>> {

    @Override
    public List<WotReplayPlayer> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        TreeNode treeNode = p.getCodec().readTree(p);
        if (treeNode instanceof ObjectNode) {
            ObjectNode playersObject = (ObjectNode) treeNode;
            Iterator<Map.Entry<String, JsonNode>> fields = playersObject.fields();
            List<WotReplayPlayer> wotReplayPlayers = new ArrayList<>();
            while(fields.hasNext()) {
                Map.Entry<String, JsonNode> next = fields.next();
                WotReplayPlayer wrp = p.getCodec().readValue(next.getValue().traverse(), WotReplayPlayer.class);
                wrp.setPlayerId(Integer.parseInt(next.getKey()));
                wotReplayPlayers.add(wrp);
            }
            return wotReplayPlayers;
        }
        return Collections.emptyList();
    }

}
