package org.avlasov.wotapi.response;

import lombok.Data;

@Data
public class Meta {

    private int count;
    private int pageTotal;
    private int total;
    private int limit;
    private int page;

}
