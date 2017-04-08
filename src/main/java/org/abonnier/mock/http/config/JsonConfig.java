package org.abonnier.mock.http.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.abonnier.mock.http.domain.json.JsonFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * JSON file configuration.
 * Created by ABONNIER on 06/03/2017.
 */
@Configuration
public class JsonConfig {

    @Value("${mock.config.file.name:mock-http.json}")
    private String defaultConfigName;

    /**
     * Loads the JSON file configuration and make a usable bean.
     * @return a JsonFile bean.
     * @throws URISyntaxException when the json file URI is invalid
     * @throws IOException when the json file cannot be read
     */
    @Bean
    public JsonFile initConfig() throws URISyntaxException, IOException {
        final StringBuilder strBld = new StringBuilder();

        Files.lines(Paths.get(ClassLoader.getSystemResource(defaultConfigName).toURI())).forEach(strBld::append);

        final String jsonConfig = strBld.toString();

        final ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(jsonConfig, JsonFile.class);
    }
}
