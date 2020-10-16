package org.avlasov.wotapi.enums;

import lombok.Getter;

@Getter
public enum WotApiParameter {

    APPLICATION_ID("application_id"),
    PAGE_NO("page_no");

    String name;

    WotApiParameter(String name) {
        this.name = name;
    }
}
