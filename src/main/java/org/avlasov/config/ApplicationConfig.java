package org.avlasov.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.avlasov.config.entity.PlatoonConfig;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.springframework.context.annotation.*;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created By artemvlasov on 04/06/2018
 **/
@Configuration
@ComponentScan(basePackages = "org.avlasov")
@PropertySource("classpath:project.properties")
public class ApplicationConfig {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper;
    }

    @Bean(destroyMethod = "close")
    public PhantomJSDriver phantomJSDriver() {
        System.setProperty("phantomjs.binary.path", "libs/phantomjs");
        DesiredCapabilities dcap = new DesiredCapabilities();
        String[] phantomArgs = new String[]{"--webdriver-loglevel=NONE"};
        dcap.setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS, phantomArgs);
        return new PhantomJSDriver(dcap);
    }

    @Bean
    @DependsOn(value = "objectMapper")
    public PlatoonConfig platoonConfig(ObjectMapper objectMapper) throws IOException {
        try (InputStream resourceAsStream = ApplicationConfig.class.getResourceAsStream("/platoons.json")) {
            PlatoonConfig platoonConfig = objectMapper.readValue(resourceAsStream, PlatoonConfig.class);
            if (platoonConfig == null)
                throw new RuntimeException("Platoon config is null, please check file ");
            return platoonConfig;
        }
    }

}
