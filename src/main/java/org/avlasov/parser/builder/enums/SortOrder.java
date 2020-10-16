package org.avlasov.parser.builder.enums;

import lombok.Getter;

@Getter
public enum  SortOrder {

    ASC("asc"), DESC("desc");

    private final String order;

    SortOrder(String order) {
        this.order = order;
    }
}
