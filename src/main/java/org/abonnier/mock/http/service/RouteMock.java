package org.abonnier.mock.http.service;

import org.abonnier.mock.http.business.RouteHandler;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * This controller uses the Route Handler to provide mock responses from the given http requests.
 * Created by abonnier on 27/02/2017.
 */
@RestController
public class RouteMock {

    private static final String QUERY_CHAR = "?";

    @Autowired
    private RouteHandler routeHandler;

    @GetMapping(value = "/**")
    public ResponseEntity<String> handleRequest(HttpServletRequest request) throws InterruptedException {
        long start = System.currentTimeMillis();
        String requestPath = request.getRequestURI();

        String queryString = request.getQueryString();
        if (StringUtils.isNotBlank(queryString)) {
            requestPath += QUERY_CHAR + queryString;
        }

        return routeHandler.get(requestPath, start);
    }
}
