package org.avlasov.parser.replay.entity.match.details;

import lombok.Data;

import java.util.Map;

@Data
public class XVMData {

    private Map<String, Object> data;
    private long accountDBID;

}
