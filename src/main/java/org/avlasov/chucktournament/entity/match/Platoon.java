package org.avlasov.chucktournament.entity.match;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.avlasov.chucktournament.entity.match.Player;

import java.util.List;

/**
 * Created By artemvlasov on 21/05/2018
 **/
@AllArgsConstructor
@Getter
@EqualsAndHashCode(exclude = { "players" })
@NoArgsConstructor
@Data
public class Platoon {

    private List<Player> players;
    private String platoonName;

}
