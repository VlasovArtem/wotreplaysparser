package org.avlasov.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.avlasov.entity.wotreplay.WotReplayPlayer;
import org.hamcrest.collection.IsEmptyCollection;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Created By artemvlasov on 09/06/2018
 **/
@RunWith(PowerMockRunner.class)
public class WotReplayPlayersDeserializerTest {

    @Test
    public void deserialize_WithNullParser_ReturnEmptyCollection() throws IOException {
        JsonParser jsonParserMock = mock(JsonParser.class, Mockito.RETURNS_DEEP_STUBS);
        ArrayNode arrayNodeMock = mock(ArrayNode.class);
        when(jsonParserMock.getCodec().readTree(any(JsonParser.class)))
                .thenReturn(arrayNodeMock);
        WotReplayPlayersDeserializer wotReplayPlayersDeserializer = new WotReplayPlayersDeserializer();
        List<WotReplayPlayer> deserialize = wotReplayPlayersDeserializer.deserialize(jsonParserMock, null);
        assertThat(deserialize, IsEmptyCollection.empty());
    }

}