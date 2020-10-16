package org.avlasov.wotapi.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.avlasov.wotapi.enums.VehicleType;

import java.io.IOException;

public class VehicleTypeDeserializer extends JsonDeserializer<VehicleType> {

    @Override
    public VehicleType deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        return VehicleType.getVehicleType(p.getText());
    }

}
