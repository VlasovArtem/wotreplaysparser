package org.avlasov.parser.builder;

import org.avlasov.parser.builder.enums.BattleType;
import org.avlasov.parser.builder.enums.SortOrder;
import org.avlasov.parser.builder.enums.WotReplaysLinkAttribute;
import org.avlasov.parser.config.properties.WotReplaysProperties;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WotReplaysLinkBuilder {

    private final WotReplaysProperties wotReplaysProperties;
    private final Map<WotReplaysLinkAttribute, String> wotReplaysLinkAttributes;

    WotReplaysLinkBuilder(WotReplaysProperties wotReplaysProperties) {
        this.wotReplaysProperties = wotReplaysProperties;
        wotReplaysLinkAttributes = new HashMap<>();
    }

    public WotReplaysLinkBuilder withWotVersion(String wotVersion) {
        return withWotVersions(wotVersion);
    }

    public WotReplaysLinkBuilder withWotVersions(String... wotVersions) {
        String versions = Stream.of(wotVersions)
                .map(wotVersion ->
                        Optional.ofNullable(wotReplaysProperties.getWotVersionsMapper().get(wotVersion))
                                .orElseThrow(() -> new IllegalArgumentException(String.format("Wot version %s is not supported.", wotVersion))))
                .map(Objects::toString)
                .collect(Collectors.joining(","));
        wotReplaysLinkAttributes.put(WotReplaysLinkAttribute.VERSION, versions);
        return this;
    }

    public WotReplaysLinkBuilder withSort(String sortAttribute, SortOrder sortOrder) {
        sortOrder = Optional.ofNullable(sortOrder)
                .orElse(SortOrder.DESC);
        wotReplaysLinkAttributes.put(WotReplaysLinkAttribute.SORT, String.format(sortAttribute, sortOrder.getOrder()));
        return this;
    }

    public WotReplaysLinkBuilder withPage(Integer page) {
        wotReplaysLinkAttributes.put(WotReplaysLinkAttribute.PAGE, page.toString());
        return this;
    }

    public WotReplaysLinkBuilder withPlayer(String playerName) {
        wotReplaysLinkAttributes.put(WotReplaysLinkAttribute.PLAYER, playerName);
        return this;
    }

    public WotReplaysLinkBuilder withBattleType(BattleType battleType) {
        wotReplaysLinkAttributes.put(WotReplaysLinkAttribute.BATTLE_TYPE, String.valueOf(battleType.getBattleType()));
        return this;
    }

    public String build() {
        String attributes = wotReplaysLinkAttributes.entrySet()
                .stream()
                .map(attribute ->
                        String.format(attribute.getKey().getAttributePattern(), attribute.getValue()))
                .collect(Collectors.joining());
        return wotReplaysProperties.getSearchLink() + attributes;
    }

}
