package org.avlasov.entity.match;

import org.avlasov.entity.match.enums.DrawGroup;

import java.util.Objects;

/**
 * Created By artemvlasov on 21/05/2018
 **/
public class Player {

    private String name;
    private DrawGroup drawGroup;

    public Player() {
    }

    public Player(String name, DrawGroup drawGroup) {
        this.name = name;
        this.drawGroup = drawGroup;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DrawGroup getDrawGroup() {
        return drawGroup;
    }

    public void setDrawGroup(DrawGroup drawGroup) {
        this.drawGroup = drawGroup;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Player)) return false;
        Player player = (Player) o;
        return Objects.equals(name, player.name) &&
                drawGroup == player.drawGroup;
    }

    @Override
    public int hashCode() {

        return Objects.hash(name, drawGroup);
    }
}
