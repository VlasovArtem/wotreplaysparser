package org.avlasov.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.avlasov.entity.wotreplay.WotReplayVehicle;

import java.io.IOException;
import java.util.*;

/**
 * Created By artemvlasov on 02/06/2018
 **/
public class WotReplayVehiclesDeserializer extends JsonDeserializer<List<WotReplayVehicle>> {

    @Override
    public List<WotReplayVehicle> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        TreeNode treeNode = p.getCodec().readTree(p);
        if (treeNode instanceof ObjectNode) {
            ObjectNode playersObject = (ObjectNode) treeNode;
            Iterator<Map.Entry<String, JsonNode>> fields = playersObject.fields();
            List<WotReplayVehicle> wotReplayVehicles = new ArrayList<>();
            while(fields.hasNext()) {
                Map.Entry<String, JsonNode> next = fields.next();
                WotReplayVehicle wrp = p.getCodec().readValue(next.getValue().get(0).traverse(), WotReplayVehicle.class);
                wrp.setVehicleMatchId(Integer.parseInt(next.getKey()));
                wotReplayVehicles.add(wrp);
            }
            return wotReplayVehicles;
        }
        return Collections.emptyList();
    }

}
