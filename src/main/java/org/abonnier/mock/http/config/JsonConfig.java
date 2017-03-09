package org.abonnier.mock.http.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.abonnier.mock.http.domain.JsonFile;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by ABONNIER on 06/03/2017.
 */
@Configuration
public class JsonConfig {

    @PostConstruct
    public JsonFile initConfig() throws URISyntaxException, IOException {
         final StringBuilder strBld = new StringBuilder();

        Files.lines(Paths.get(ClassLoader.getSystemResource("mock-http-test.json").toURI())).forEach(strBld::append);

        final String jsonConfig = strBld.toString();

        final ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(jsonConfig, JsonFile.class);
    }
}
