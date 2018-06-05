package org.avlasov.entity.match;

import lombok.*;

import java.util.List;

/**
 * Created By artemvlasov on 21/05/2018
 **/
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(exclude = { "players" })
@NoArgsConstructor
public class Platoon {

    private List<Player> players;
    private String platoonName;

}
