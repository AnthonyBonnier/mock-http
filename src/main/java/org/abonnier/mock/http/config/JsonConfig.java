package org.abonnier.mock.http.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.abonnier.mock.http.domain.json.BluetoothJsonFile;
import org.abonnier.mock.http.domain.json.HttpJsonFile;
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
    @Value("${http.mock.config.file.name:mock-http.json}")
    private String httpMockConfigFileName;

    @Getter
    @Value("${bluetooth.mock.config.file.name:mock-bluetooth.json}")
    private String bluetoothMockConfigFileName;

    /**
     * @return the Path of the JSON config File of the http Mock
     * @throws URISyntaxException when exceptions are thrown by the toURI
     */
    public Path getHttpMockJsonFilePath() throws URISyntaxException {
        return Paths.get(ClassLoader.getSystemResource(httpMockConfigFileName).toURI());
    }

    /**
     * @return the Path of the JSON config File of the bluetooth Mock
     * @throws URISyntaxException when exceptions are thrown by the toURI
     */
    public Path getBluetoothMockJsonFilePath() throws URISyntaxException {
        return Paths.get(ClassLoader.getSystemResource(bluetoothMockConfigFileName).toURI());
    }

    /**
     * Loads the JSON file configuration for http Mock and make a usable bean.
     * @return a HttpJsonFile bean.
     * @throws URISyntaxException when the json file URI is invalid
     * @throws IOException when the json file cannot be read
     */
    @Bean
    @Scope("prototype")
    public HttpJsonFile httpMockConfig() throws URISyntaxException, IOException {
        final Path httpConfigPath = getHttpMockJsonFilePath();
        if (httpConfigPath.toFile().exists()) {

            final StringBuilder strBld = new StringBuilder();

            Files.lines(httpConfigPath).forEach(strBld::append);

            final String httpConfig = strBld.toString();

            final ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(httpConfig, HttpJsonFile.class);
        } else {
            return new HttpJsonFile();
        }
    }

    /**
     * Loads the JSON file configuration for Bluetooth Mock and make a usable bean.
     * @return a HttpJsonFile bean.
     * @throws URISyntaxException when the json file URI is invalid
     * @throws IOException when the json file cannot be read
     */
    @Bean
    @Scope("prototype")
    public BluetoothJsonFile bluetoothMockConfig() throws URISyntaxException, IOException {
        final Path bthConfigPath = getBluetoothMockJsonFilePath();
        if (bthConfigPath.toFile().exists()) {

            final StringBuilder strBld = new StringBuilder();

            Files.lines(bthConfigPath).forEach(strBld::append);

            final String bthConfig = strBld.toString();

            final ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(bthConfig, BluetoothJsonFile.class);
        } else {
            return new BluetoothJsonFile();
        }
    }
}
