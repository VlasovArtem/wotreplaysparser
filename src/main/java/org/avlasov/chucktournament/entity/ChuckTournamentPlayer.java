package org.avlasov.chucktournament.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.avlasov.chucktournament.entity.match.Player;
import org.avlasov.chucktournament.entity.match.enums.DrawGroup;

@Getter
@EqualsAndHashCode(callSuper = true)
public class ChuckTournamentPlayer extends Player {

    private final DrawGroup drawGroup;

    public ChuckTournamentPlayer(String name, DrawGroup drawGroup) {
        super(name);
        this.drawGroup = drawGroup;
    }

}
