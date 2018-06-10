package org.avlasov;

import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * Created By artemvlasov on 09/06/2018
 **/
@RunWith(PowerMockRunner.class)
@PowerMockIgnore({"com.sun.org.apache.xerces.*", "javax.xml.parsers.*", "org.apache.logging.*"})
public abstract class PowerMockTestCase {}
