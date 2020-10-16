package org.avlasov.parser.replay.entity.match.details;

import lombok.Data;

import java.util.List;

@Data
public class XVM {

    private List<XVMTiming> timing;
    private XVMGlobal global;
    private String version;

}
