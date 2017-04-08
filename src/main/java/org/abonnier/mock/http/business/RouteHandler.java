package org.abonnier.mock.http.business;

import org.abonnier.mock.http.domain.json.Entry;
import org.abonnier.mock.http.domain.json.JsonFile;
import org.abonnier.mock.http.domain.json.Response;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Route Handler is in charge of delivering a mock response from the given JSON configuration file.
 * Created by Anthony on 29/03/2017.
 */
@Component
public class RouteHandler {

    private Map<String, Entry> entriesMap = new HashMap<>();

    /**
     * Construct a Route Handler for a JSON configuration file.
     *
     * @param jf JSON File to parse.
     */
    public RouteHandler(final JsonFile jf) {
        entriesMap = jf.getEntries().parallelStream().collect(Collectors.toMap(Entry::getInput, Function.identity()));
    }

    /**
     * This method returns a ReponseEntity given an input.
     *
     * @param input on the mocked service
     * @param start in millisecond of the current execution
     * @return a ResponseEntity generated from the JSON configuration file or a 400 Bad request if the input is
     * unknown or the mock entry does not deliver response anymore (repeat false).
     */
    public ResponseEntity<String> get(final String input, long start) throws InterruptedException {
        long sleep = 0;

        // Default response
        ResponseEntity<String> responseEntity = new ResponseEntity(input, HttpStatus.BAD_REQUEST);

        // Retrieving entry from the given input
        final Entry entry = entriesMap.get(input);

        // Can be null if input did not match
        if (entry != null) {
            final Response response = entry.getOutput().getNextResponse();

            // Can be null if repeat = false
            if (response != null) {

                if (response.hasSleep()) {
                    sleep = response.getSleep();
                }

                // Headers
                final MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
                headers.add(HttpHeaders.CONTENT_TYPE, entry.getOutput().getContentType());
                // Response building
                responseEntity = new ResponseEntity(response.getOutput(), headers, HttpStatus.valueOf(response.getStatus()));
            }
        }

        // Sleep management
        if (start > 0 && sleep > 0) {
            // The delta is the time the previous instructions take to be executed
            long delta = System.currentTimeMillis() - start;

            // Only if the delta is lesser than the response sleep, otherwise, the response is returned immediately
            if (delta < sleep) {
                sleep -= delta;
                // Sleep during the found response sleep minus the delta time of the previous instructions
                Thread.sleep(sleep);
            }
        }

        return responseEntity;
    }
}
