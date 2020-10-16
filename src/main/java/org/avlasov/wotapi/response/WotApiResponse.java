package org.avlasov.wotapi.response;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import org.avlasov.wotapi.deserializer.ByteArrayDeserializer;

@Data
public class WotApiResponse {

    private String status;
    private Meta meta;
    @JsonDeserialize(using = ByteArrayDeserializer.class)
    private byte[] data;

}
