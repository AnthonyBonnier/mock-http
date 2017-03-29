package org.abonnier.mock.http.config;

import org.abonnier.mock.http.domain.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

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

        final JsonFile jsonFile = (new JsonConfig()).initConfig("mock-http-test.json");
        assertNotNull(jsonFile);
        assertTrue(!jsonFile.getEntries().isEmpty());

        final Entry entry1 = jsonFile.getEntries().get(0);
        assertNotNull(entry1);
        assertTrue(entry1.isRepeat());
        assertEquals("/test/service1", entry1.getInput());

        final Output output = entry1.getOutput();
        assertNotNull(output);
        assertEquals(Mode.SORTED, output.getMode());
        assertEquals("text/html", output.getContentType());
        assertEquals(1, output.getResponses().size());

        final Response response = output.getResponses().get(0);
        assertEquals(200, response.getStatus());
        assertEquals(1, response.getTimes());
        assertEquals(0, response.getSleep());
        assertEquals("OK", response.getOutput());
    }
}