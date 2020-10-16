package org.avlasov.wotapi.serializer;


import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.avlasov.wotapi.enums.VehicleType;

import java.io.IOException;

public class VehicleTypeSerializer extends JsonSerializer<VehicleType> {

    @Override
    public void serialize(VehicleType value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeString(value.getType());
    }


}
