package org.avlasov.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created By artemvlasov on 04/06/2018
 **/
@RunWith(MockitoJUnitRunner.class)
public class WotReplayLocalDateTimeDeserializerTest {

    @Test
    public void deserialize() throws IOException {
        WotReplayLocalDateTimeDeserializer wotReplayLocalDateTimeDeserializer = new WotReplayLocalDateTimeDeserializer();
        JsonParser jsonParserMock = mock(JsonParser.class);
        DeserializationContext deserializationContextMock = mock(DeserializationContext.class);
        LocalDateTime of = LocalDateTime.of(2018, 5, 22, 15, 50);
        when(jsonParserMock.getValueAsString()).thenReturn("22.05.2018 15:50");
        LocalDateTime deserialize = wotReplayLocalDateTimeDeserializer.deserialize(jsonParserMock, deserializationContextMock);
        assertEquals(of, deserialize);
    }

}