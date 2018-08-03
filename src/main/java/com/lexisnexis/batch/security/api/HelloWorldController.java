package com.lexisnexis.batch.security.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping("/hello")
public class HelloWorldController {

    private String message = "Hello World!";

    @RequestMapping(value = "", method = GET, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<String> sayHello() {
        return ResponseEntity.status(OK).body(message);
    }
}
