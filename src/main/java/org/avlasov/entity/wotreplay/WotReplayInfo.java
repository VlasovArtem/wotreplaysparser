package org.avlasov.entity.wotreplay;

/**
 * Created By artemvlasov on 02/06/2018
 **/
public class WotReplayInfo {

    private WotReplayOwnerInfo replayOwnerInfo;
    private WotReplayMatch replayMatch;

    public WotReplayInfo(WotReplayOwnerInfo replayOwnerInfo, WotReplayMatch replayMatch) {
        this.replayOwnerInfo = replayOwnerInfo;
        this.replayMatch = replayMatch;
    }

    public WotReplayOwnerInfo getReplayOwnerInfo() {
        return replayOwnerInfo;
    }

    public WotReplayMatch getReplayMatch() {
        return replayMatch;
    }

}
