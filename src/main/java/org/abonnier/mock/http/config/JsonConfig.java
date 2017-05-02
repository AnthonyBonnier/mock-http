package org.abonnier.mock.http.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.abonnier.mock.http.domain.json.JsonFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * JSON file configuration.
 * Created by ABONNIER on 06/03/2017.
 */
@Configuration
public class JsonConfig {

    @Getter
    @Value("${mock.config.file.name:mock-http.json}")
    private String defaultConfigName;

    /**
     * @return the Path of the JSON config File
     * @throws URISyntaxException when exceptions are thrown by the toURI
     */
    public Path getJsonFilePath() throws URISyntaxException {
        return Paths.get(ClassLoader.getSystemResource(defaultConfigName).toURI());
    }

    /**
     * Loads the JSON file configuration and make a usable bean.
     * @return a JsonFile bean.
     * @throws URISyntaxException when the json file URI is invalid
     * @throws IOException when the json file cannot be read
     */
    @Bean
    @Scope("prototype")
    public JsonFile initConfig() throws URISyntaxException, IOException {
        final Path jsonConfigPath = getJsonFilePath();
        if (jsonConfigPath.toFile().exists()) {

            final StringBuilder strBld = new StringBuilder();

            Files.lines(jsonConfigPath).forEach(strBld::append);

            final String jsonConfig = strBld.toString();

            final ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(jsonConfig, JsonFile.class);
        } else {
            return new JsonFile();
        }
    }
}
