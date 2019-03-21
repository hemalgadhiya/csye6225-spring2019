package io.webApp.springbootstarter.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class MyFileNotFoundException extends RuntimeException {
	
	private final static Logger logger = LoggerFactory.getLogger(MyFileNotFoundException.class);
	
    public MyFileNotFoundException(String message) {
        super(message);
        logger.error(message);
    }

    public MyFileNotFoundException(String message, Throwable cause) {
        super(message, cause);
        logger.error(message, cause);
    }
}