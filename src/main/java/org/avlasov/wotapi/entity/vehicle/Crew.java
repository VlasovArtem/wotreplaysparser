package org.avlasov.wotapi.entity.vehicle;

import lombok.Data;

import java.util.Map;

@Data
public class Crew {

    private String memberId;
    private Map<String, String> roles;

}
