package io.webApp.springbootstarter.fileStorage;

import java.nio.file.Path;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {

	public String storeFile(MultipartFile file);
	
	public boolean DeleteFile(String file);
	
	public Path getFileStorageLocation();
		
}
