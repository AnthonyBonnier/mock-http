package org.abonnier.mock.http.business;

import org.abonnier.mock.http.config.JsonConfig;
import org.abonnier.mock.http.domain.json.JsonFile;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.TEXT_HTML;


/**
 * Testing the RouteHandler.
 * Created by Anthony on 29/03/2017.
 */
@RunWith(SpringRunner.class)
public class RouteHandlerTest {

    private RouteHandler routeHandler;

    @Before
    public void setUp() throws Exception {
        Field defaultConfigNameField = ReflectionUtils.findField(JsonConfig.class, "defaultConfigName");
        ReflectionUtils.makeAccessible(defaultConfigNameField);
        final JsonConfig jsonConfig = new JsonConfig();
        ReflectionUtils.setField(defaultConfigNameField, jsonConfig, "mock-http-test.json");

        final JsonFile jsonFile = jsonConfig.initConfig();

        routeHandler = new RouteHandler(jsonFile);
    }

    @Test
    public void testRouteHandlerOK() throws Exception {
        // Testing a simple call to the mock service 1
        final ResponseEntity<String> response = routeHandler.get("/test/service1", 0L);
        assertResponseMatches(response, OK, "Test service1 OK", TEXT_HTML);
    }

    @Test
    public void testRouteHandlerKO() throws Exception {
        // Testing a wrong call to the mock service unknown (which does not exist)
        final ResponseEntity<String> response = routeHandler.get("/test/unknown", 0L);
        assertResponseMatches(response, BAD_REQUEST, "/test/unknown", null);
    }

    @Test
    public void testRouteHandlerSortedResponses() throws Exception {
        // First call should returns response 1
        ResponseEntity<String> response = routeHandler.get("/test/service2", 0L);
        assertResponseMatches(response, OK, "Test service2 response 1", APPLICATION_JSON);

        // Second call should returns response 2
        response = routeHandler.get("/test/service2", 0L);
        assertResponseMatches(response, OK, "Test service2 response 2", APPLICATION_JSON);

        // Third call should returns response 3
        response = routeHandler.get("/test/service2", 0L);
        assertResponseMatches(response, OK, "Test service2 response 3", APPLICATION_JSON);

        // Next call should returns response 1, because of repeat = true
        response = routeHandler.get("/test/service2", 0L);
        assertResponseMatches(response, OK, "Test service2 response 1", APPLICATION_JSON);
    }

    @Test
    public void testRouteHandlerRepeatOnce() throws Exception {
        // First call should returns response 1
        ResponseEntity<String> response = routeHandler.get("/test/service3", 0L);
        assertResponseMatches(response, OK, "Test service3 once", TEXT_HTML);

        // Second call should returns 400 BAD REQUEST, because of repeat = false
        response = routeHandler.get("/test/service3", 0L);
        assertResponseMatches(response, BAD_REQUEST, "/test/service3", null);
    }

    @Test
    public void testRouteHandlerSortedMultipleResponses() throws Exception {
        // First call should returns response 1 once
        ResponseEntity<String> response = routeHandler.get("/test/service4", 0L);
        assertResponseMatches(response, OK, "Test service4 response 1 once", APPLICATION_JSON);

        // Next 2 calls should returns response 2 twice
        response = routeHandler.get("/test/service4", 0L);
        assertResponseMatches(response, OK, "Test service4 response 2 twice", APPLICATION_JSON);
        response = routeHandler.get("/test/service4", 0L);
        assertResponseMatches(response, OK, "Test service4 response 2 twice", APPLICATION_JSON);

        // Next 3 calls should returns response 3 three times
        response = routeHandler.get("/test/service4", 0L);
        assertResponseMatches(response, OK, "Test service4 response 3 three times", APPLICATION_JSON);
        response = routeHandler.get("/test/service4", 0L);
        assertResponseMatches(response, OK, "Test service4 response 3 three times", APPLICATION_JSON);
        response = routeHandler.get("/test/service4", 0L);
        assertResponseMatches(response, OK, "Test service4 response 3 three times", APPLICATION_JSON);

        // Next call should returns response 1 because of repeat = true
        response = routeHandler.get("/test/service4", 0L);
        assertResponseMatches(response, OK, "Test service4 response 1 once", APPLICATION_JSON);
    }

    @Test
    public void testRouteHandlerRandomMultipleResponsesNoRepeat() throws Exception {

        // Should contains all responses
        final List<ResponseEntity<String>> allResponses = new ArrayList<>();

        // Make the 21 calls to the mock service
        for (int i = 0; i <= 21; i++) {
            allResponses.add(routeHandler.get("/test/service5", 0L));
        }

        // Building an ordered responses indexes list to check the randomized distribution of the responses
        List<Integer> orderedResponsesIndexesList = new ArrayList<>();
        int currentIndex = 1;
        for (int i = 0; i < 21; i++) {
            orderedResponsesIndexesList.add(currentIndex);
            if (i == 4 || i == 11) {
                currentIndex++;
            }
        }

        // Will count the last bad request response
        int otherResponses = 0;
        // Will store the order of the responses to compare with the ordered list
        List<Integer> responsesIndexesList = new ArrayList<>();

        // Checking all responses
        for (ResponseEntity<String> response : allResponses) {
            if (response.getBody().contains("response 1")) {

                responsesIndexesList.add(1);
                assertResponseMatches(response, OK, "Test service5 response 1 - 5 times", APPLICATION_JSON);

            } else if (response.getBody().contains("response 2")) {

                responsesIndexesList.add(2);
                assertResponseMatches(response, OK, "Test service5 response 2 - 7 times", APPLICATION_JSON);

            } else if (response.getBody().contains("response 3")) {

                responsesIndexesList.add(3);
                assertResponseMatches(response, OK, "Test service5 response 3 - 9 times", APPLICATION_JSON);

            } else {
                otherResponses++;
                assertResponseMatches(response, BAD_REQUEST, "/test/service5", null);
            }
        }

        // Counting occurrences of responses
        Map<Integer, Long> counters =
                responsesIndexesList.stream()
                        .collect(groupingBy(i -> i, counting()));

        // Responses occurrences should equal the 'times' attribute set in the mock json file for the test
        assertEquals(5, counters.get(1).intValue()); // response 1 should occurred 5 times
        assertEquals(7, counters.get(2).intValue()); // response 2 should occurred 7 times
        assertEquals(9, counters.get(3).intValue()); // response 3 should occurred 9 times
        assertEquals(1, otherResponses); // last call to the service is done once only because of repeat = false

        // The ordered indexes list and the responses indexes list should not equal
        assertNotEquals(orderedResponsesIndexesList, responsesIndexesList);
    }

    /**
     * Utility method to assert the validity of the response.
     *
     * @param response to check
     * @param status   the response must have
     * @param body     the response must have
     * @param type     the response must have
     */
    private void assertResponseMatches(ResponseEntity<String> response, HttpStatus status, String body, MediaType type) {
        assertNotNull(response);
        assertEquals(status, response.getStatusCode());
        assertEquals(body, response.getBody());
        if (type != null) {
            assertEquals(type, response.getHeaders().getContentType());
        }
    }
}