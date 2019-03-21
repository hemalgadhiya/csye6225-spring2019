package io.webApp.springbootstarter.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileStorageException extends RuntimeException {
	private final static Logger logger = LoggerFactory.getLogger(FileStorageException.class);

    public FileStorageException(String message) {
        super(message);
        logger.error(message);
    }

    public FileStorageException(String message, Throwable cause) {
        super(message, cause);
    	logger.error(message, cause);
    }
}

