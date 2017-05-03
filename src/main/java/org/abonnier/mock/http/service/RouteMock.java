package org.abonnier.mock.http.service;

import lombok.extern.slf4j.Slf4j;
import org.abonnier.mock.http.business.RouteHandler;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.stream.Collectors;

/**
 * This controller uses the Route Handler to provide mock responses from the given http requests.
 * Created by abonnier on 27/02/2017.
 */
@Slf4j
@RestController
public class RouteMock {

    private static final String QUERY_CHAR = "?";

    @Autowired
    private RouteHandler routeHandler;

    @GetMapping(value = "/**")
    public ResponseEntity<String> handleGetRequest(HttpServletRequest request) throws InterruptedException {
        long start = System.currentTimeMillis();

        return routeHandler.get(getFullQuery(request), start);
    }

    @PostMapping(value = "/**")
    public ResponseEntity<String> handlePostRequest(HttpServletRequest request) throws InterruptedException {
        long start = System.currentTimeMillis();

        String body = null;
        try {
            body = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        } catch (IOException e) {
            log.error("Error while retrieving request BODY", e);
        }

        return routeHandler.post(getFullQuery(request), start, body);
    }

    private String getFullQuery(HttpServletRequest request) {
        String requestPath = request.getRequestURI();

        String queryString = request.getQueryString();
        if (StringUtils.isNotBlank(queryString)) {
            requestPath += QUERY_CHAR + queryString;
        }
        return requestPath;
    }
}
