package org.avlasov.parser.site;

import org.avlasov.PowerMockTestCase;
import org.avlasov.utils.DataUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.powermock.core.classloader.annotations.PrepareForTest;

import static org.mockito.ArgumentMatchers.anyString;
import static org.powermock.api.mockito.PowerMockito.doNothing;

/**
 * Created By artemvlasov on 10/06/2018
 **/
@PrepareForTest(ParseSiteData.class)
public class ParseSiteDataTest extends PowerMockTestCase  {

    @Mock
    private DataUtils dataUtilsMock;
    @Mock
    private PhantomJSDriver phantomJSDriverMock;
    @InjectMocks
    private ParseSiteData parseSiteData;

    @Before
    public void setUp() throws Exception {
        doNothing().when(phantomJSDriverMock).get(anyString());
    }

    @Test
    public void parseMatches() {
    }

    @Test
    public void parseMatches1() {
    }

    @Test
    public void parseMatches2() {
    }
}