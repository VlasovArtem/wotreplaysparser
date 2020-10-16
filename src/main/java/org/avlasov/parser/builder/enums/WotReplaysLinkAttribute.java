package org.avlasov.parser.builder.enums;

import lombok.Getter;

@Getter
public enum WotReplaysLinkAttribute {
    VERSION("/version/%s"),
    PLAYER("/player/%s"),
    SORT("/sort/%s"),
    PAGE("/page/%s"),
    BATTLE_TYPE("/battle_type/%s");

    private final String attributePattern;

    WotReplaysLinkAttribute(String attributePattern) {
        this.attributePattern = attributePattern;
    }

}
