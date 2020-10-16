package org.avlasov.statistic.entity.data;

import com.googlecode.jmapper.JMapper;
import com.googlecode.jmapper.annotations.JGlobalMap;
import lombok.Data;
import org.avlasov.wotapi.entity.arena.Arena;

@Data
@JGlobalMap(excluded = {"TO_STATISTIC_ENTITY"})
public class StatisticArena {

    public static JMapper<StatisticArena, Arena> TO_STATISTIC_ENTITY = new JMapper<>(StatisticArena.class, Arena.class);

    private String nameI18n;
    private String arenaId;

}
