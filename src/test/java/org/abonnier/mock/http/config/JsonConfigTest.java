package org.abonnier.mock.http.config;

import org.abonnier.mock.http.domain.JsonFile;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;

/**
 * Created by abonnier on 09/03/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class JsonConfigTest {

    @Test
    public void testInitConfig() throws Exception {

        JsonConfig config = new JsonConfig();

        final JsonFile jsonFile = config.initConfig();

        Assert.assertNotNull(jsonFile);
    }
}