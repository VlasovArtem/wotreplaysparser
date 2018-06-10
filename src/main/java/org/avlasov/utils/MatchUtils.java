package org.avlasov.utils;

import org.avlasov.entity.match.Match;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Created By artemvlasov on 10/06/2018
 **/
@Component
public class MatchUtils {

    public Optional<Match> findBestMatch(List<Match> matches) {
        if (matches != null && !matches.isEmpty()) {
            return matches.parallelStream()
                    .max(Comparator.comparingInt(m -> m.getResult().getMatchScore()));
        }
        return Optional.empty();
    }

    public Optional<Match> findWorstMatch(List<Match> matches) {
        if (matches != null && !matches.isEmpty()) {
            return matches.parallelStream()
                    .min(Comparator.comparingInt(m -> m.getResult().getMatchScore()));
        }
        return Optional.empty();
    }

}
