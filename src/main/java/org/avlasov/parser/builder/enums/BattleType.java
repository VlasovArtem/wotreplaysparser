package org.avlasov.parser.builder.enums;

import lombok.Getter;

@Getter
public enum BattleType {

    STANDART_BATTLE(1);

    int battleType;

    BattleType(int battleType) {
        this.battleType = battleType;
    }

}
