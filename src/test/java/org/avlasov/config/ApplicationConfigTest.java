package org.avlasov.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.avlasov.config.entity.PlatoonConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.powermock.api.mockito.PowerMockito.*;

/**
 * Created By artemvlasov on 09/06/2018
 **/
@RunWith(PowerMockRunner.class)
@PrepareForTest({ApplicationConfig.class})
public class ApplicationConfigTest {

    private ApplicationConfig applicationConfig;

    @Before
    public void setUp() throws Exception {
        applicationConfig = new ApplicationConfig();
    }

    @Test
    public void objectMapper_WithValidData_ReturnObjectMapper() {
        ObjectMapper objectMapper = applicationConfig.objectMapper();
        assertNotNull(objectMapper);
    }

    @Test
    public void phantomJSDriver_WithValidaData_ReturnPhantomJSDriver() throws Exception {
        PhantomJSDriver mock = mock(PhantomJSDriver.class);
        whenNew(PhantomJSDriver.class).withAnyArguments().thenReturn(mock);
        PhantomJSDriver phantomJSDriver = applicationConfig.phantomJSDriver();
        assertNotNull(phantomJSDriver);
    }

    @Test
    public void platoonConfig_WithValidData_ReturnPlatoonConfig() throws IOException {
        ObjectMapper objectMapperMock = mock(ObjectMapper.class);
        when(objectMapperMock.readValue(any(InputStream.class), eq(PlatoonConfig.class)))
                .thenReturn(new PlatoonConfig());
        PlatoonConfig platoonConfig = applicationConfig.platoonConfig(objectMapperMock);
        assertNotNull(platoonConfig);
    }

    @Test(expected = RuntimeException.class)
    public void platoonConfig_WithNullPlatoonConfig_ThrowsRuntimeException() throws IOException {
        ObjectMapper objectMapperMock = mock(ObjectMapper.class);
        when(objectMapperMock.readValue(any(InputStream.class), eq(PlatoonConfig.class)))
                .thenReturn(null);
        applicationConfig.platoonConfig(objectMapperMock);
    }
}