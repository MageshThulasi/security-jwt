package com.lexisnexis.batch.security.api;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping("/protected")
public class ProtectedController {

    private String message = "Bonjour!";

    @PreAuthorize("hasRole('USER')")
    @RequestMapping(value = "", method = GET, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<String> sayBonjour() {
        return ResponseEntity.status(OK).body(message);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/api", method = GET, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<String> sayBonjourWrite() {
        return ResponseEntity.status(OK).body(message + " API user");
    }
}
