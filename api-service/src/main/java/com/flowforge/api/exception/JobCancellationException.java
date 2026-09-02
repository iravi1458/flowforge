package com.flowforge.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(HttpStatus.CONFLICT)
public class JobCancellationException extends RuntimeException {

    public JobCancellationException(UUID id) {
        super("Job cannot be cancelled in its current state: " + id);
    }
}
