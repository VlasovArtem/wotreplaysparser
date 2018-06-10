package org.avlasov.config.entity;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created By artemvlasov on 09/06/2018
 **/
public class PlatoonConfigTest {

    @Test
    public void getPlatoons() {
        PlatoonConfig platoonConfig = new PlatoonConfig();
        assertNull(platoonConfig.getPlatoons());
    }

}