package io.webApp.springbootstarter.register;

import org.springframework.stereotype.Service;

@Service
public class registerService {
	
	TableCreation tableCreation;

	
	public void dbCreation() {
		tableCreation.createDB();
	}
}
