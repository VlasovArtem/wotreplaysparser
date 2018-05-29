package org.avlasov.entity.match;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created By artemvlasov on 21/05/2018
 **/
@Builder
@Getter
@EqualsAndHashCode(of = {"result", "platoon", "matchDate", "mapData"})
@NoArgsConstructor
@AllArgsConstructor
public class Match {

    private MatchResult result;
    private List<PlayerMatch> playerMatches;
    private Platoon platoon;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime matchDate;
    private int matchDurationInSeconds;
    private MapData mapData;
    private String matchLink;

}
