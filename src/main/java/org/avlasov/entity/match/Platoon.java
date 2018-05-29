package org.avlasov.entity.match;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Created By artemvlasov on 21/05/2018
 **/
@AllArgsConstructor
@Getter
@EqualsAndHashCode(exclude = { "players" })
@NoArgsConstructor
public class Platoon {

    private List<Player> players;
    private String platoonName;

}
