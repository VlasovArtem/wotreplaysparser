package org.avlasov.config;

import org.avlasov.config.entity.PlatoonData;

import java.util.List;

/**
 * Created By artemvlasov on 21/05/2018
 **/
public class PlatoonConfig {

    private List<PlatoonData> platoonDataList;

    public PlatoonConfig(List<PlatoonData> platoonDataList) {
        this.platoonDataList = platoonDataList;
    }

    public List<PlatoonData> getPlatoonDataList() {
        return platoonDataList;
    }

    public void setPlatoonDataList(List<PlatoonData> platoonDataList) {
        this.platoonDataList = platoonDataList;
    }
}
