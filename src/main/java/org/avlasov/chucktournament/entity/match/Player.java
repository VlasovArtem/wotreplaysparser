package org.avlasov.chucktournament.entity.match;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class Player {

    private final String name;

    public Player(String name) {
        this.name = name;
    }

}
