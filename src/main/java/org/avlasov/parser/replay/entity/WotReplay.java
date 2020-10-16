package org.avlasov.parser.replay.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WotReplay {

    private String replayLink;
    private String downloadLink;

}
