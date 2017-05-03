package org.abonnier.mock.http.config;

import org.abonnier.mock.http.domain.json.Entry;
import org.abonnier.mock.http.domain.json.HttpJsonFile;
import org.abonnier.mock.http.domain.json.Mode;
import org.abonnier.mock.http.domain.json.Output;
import org.abonnier.mock.http.domain.json.Response;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Unit test for JSON File deserialization.
 * Created by abonnier on 09/03/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class JsonConfigTest {

    @Test
    public void testInitConfig() throws Exception {

        Field defaultConfigNameField = ReflectionUtils.findField(JsonConfig.class, "defaultConfigName");
        ReflectionUtils.makeAccessible(defaultConfigNameField);
        final JsonConfig jsonConfig = new JsonConfig();
        ReflectionUtils.setField(defaultConfigNameField, jsonConfig, "mock-http-test.json");

        final HttpJsonFile httpJsonFile = jsonConfig.httpMockConfig();
        assertNotNull(httpJsonFile);
        assertTrue(!httpJsonFile.getEntries().isEmpty());

        final Entry entry1 = httpJsonFile.getEntries().get(0);
        assertNotNull(entry1);
        assertEquals("/test/service1", entry1.getInput());

        final Output output = entry1.getOutput();
        assertNotNull(output);
        assertTrue(output.isRepeat());
        assertEquals(Mode.SORTED, output.getMode());
        assertEquals("text/html", output.getContentType());
        assertEquals(1, output.getResponses().size());

        final Response response = output.getResponses().get(0);
        assertEquals(200, response.getStatus());
        assertEquals(1, response.getTimes());
        assertEquals(0, response.getSleep());
        assertEquals("Test service1 OK", response.getOutput());
    }
}