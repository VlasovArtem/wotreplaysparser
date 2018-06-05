package org.avlasov.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Created By artemvlasov on 03/06/2018
 **/
@EqualsAndHashCode
@Getter
@Setter
public class WotReplaysSearchReplayDetails {

    private String replayName;
    private String replayLink;
    private String replayDownloadLink;
    private String replayOwner;
    private boolean possiblyCorruptedReplay;
    private LocalDateTime uploadDate;

}
