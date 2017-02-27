package org.abonnier.mock.http.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by abonnier on 27/02/2017.
 */
@RestController
public class MockRouteManager {

    @GetMapping(value = "/**")
    public String handleRequest(HttpServletRequest request) {
         return "OK  : " + request.getContextPath();
    }
}
