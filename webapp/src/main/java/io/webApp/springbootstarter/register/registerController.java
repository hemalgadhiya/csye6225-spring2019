package io.webApp.springbootstarter.register;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.util.ArrayList;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import io.webApp.springbootstarter.attachments.attachment;
import io.webApp.springbootstarter.attachments.attachmentDao;
import io.webApp.springbootstarter.attachments.metaData;
import io.webApp.springbootstarter.fileStorage.DevFileStorageService;
import io.webApp.springbootstarter.fileStorage.FileStorageService;
import io.webApp.springbootstarter.notes.Note;
import io.webApp.springbootstarter.notes.NoteDao;

@RestController
public class registerController {

	private final static Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern
			.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
	// private final static Pattern VALID_PASSWORD_REGEX = Pattern.compile
	// ("((?=.*\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{6,20})");

	private String email;
	private String password;

	public register userDetails;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	NoteDao noteDao;

	@Autowired
	private FileStorageService fileStorageService;

	@Autowired
	private attachmentDao attachDao;
	
	private List<attachment> attachmentlist;

	/* Method to verify the Junit test suite */
	@RequestMapping(method = RequestMethod.GET, value = "/test", produces = "application/json")
	public register fetchuser() {

		register user = new register();
		user.setEmail("paavan@gmail.com");
		user.setPassword("Pass@123");
		return user;

	}

	@RequestMapping(method = RequestMethod.POST, value = "/user/register")

	public String addUser(@RequestBody register userDetails) {
		if (userDetails.getEmail() == null || userDetails.getPassword() == null || userDetails.getEmail().isEmpty()
				|| userDetails.getPassword().isEmpty()) {
			return "{\"RESPONSE\" : \"Credentials should not be empty\"}";
		}

		if (checkVaildEmailAddr(userDetails.getEmail())) {
			if (checkAlreadyPresent(userDetails)) {
				return "{\"RESPONSE\" : \"User email already exists. Please Login\"}";
			}
			if (!isValidPassword(userDetails.getPassword())) {
				return "{\"RESPONSE\" : \"password should follow NIST standards\"}";
			}
			registerUser(userDetails);
			return "{\"RESPONSE\" : \"Registeration Successful\"}";
		} else
			return "{\"RESPONSE\" : \"Invalid emailID. Check it out !!!\"}";
	}

	public boolean checkVaildEmailAddr(String email) {
		System.out.println("Checking Email ID pattern");
		Matcher mat = VALID_EMAIL_ADDRESS_REGEX.matcher(email);
		return mat.find();
	}

	public boolean checkAlreadyPresent(register userDetails) {
		System.out.println("Checking Email ID Already present");
		ArrayList<register> dbList = new ArrayList<>(userRepository.findAll());

		for (register i : dbList) {
			if (i.getEmail().equals(userDetails.getEmail())) {
				System.out.println("already registered");
				return true;
			}
		}
		return false;
	}

	public boolean checkPassword(register userDetails) {
		System.out.println("Checking password");
		ArrayList<register> dbList = new ArrayList<>(userRepository.findAll());

		for (register i : dbList) {
			if (i.getEmail().equals(userDetails.getEmail())) {
				// String password = BCrypt.hashpw(userDetails.getPassword(), BCrypt.gensalt());
				if (BCrypt.checkpw(userDetails.getPassword(), i.getPassword())) {
					System.out.println("It matches");
					return true;
				} else
					System.out.println("It does not match");
			}
		}
		return false;
	}

	public boolean isValidPassword(String password) {
		if (!(password.matches("^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$"))) {
			System.out.println("Invalid password");
			return false;
		}
		return true;
	}

	public boolean registerUser(@RequestBody final register userData) {

		String password = BCrypt.hashpw(userData.getPassword(), BCrypt.gensalt());
		System.out.println("Password salt : " + password);
		userData.setPassword(password);

		userRepository.save(userData);
		return true;
	}

	@RequestMapping(method = RequestMethod.GET, value = "/", produces = "application/json")
	public String ValidUser(@RequestHeader(value = "Authorization", defaultValue = "noAuth") String auth) {
		String status = checkAuth(auth);
		if (status.equals("Success")) {
			String time = "{\"RESPONSE: Token Authenticated \" : " + currentTime() + "\"}\"";
			return time;
		} else
			return status;
	}

	public String currentTime() {
		currentTime Ctime = new currentTime();
		return Ctime.getCurrentTime();
	}

	@RequestMapping(method = RequestMethod.POST, value = "/note")
	public ResponseEntity<Note> createNote(@RequestBody Note nt,
			@RequestHeader(value = "Authorization", defaultValue = "noAuth") String auth) {
		try {
			String status = checkAuth(auth);
			if (status.equals("Success")) {
				if (nt.getTitle().isEmpty() || nt.getContent().isEmpty()) {
					throw new Exception();
				}
				nt.setEmailID(email);
				
				Note note = noteDao.Save(nt);
				return ResponseEntity.status(HttpStatus.CREATED).body(note);
			} else
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		} catch (Exception ex) {
			ex.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
	}

	@RequestMapping(method = RequestMethod.GET, value = "/note")
	public ResponseEntity<List<Note>> getAllNote(
			@RequestHeader(value = "Authorization", defaultValue = "noAuth") String auth) {
		String status = checkAuth(auth);
		if (status.equals("Success")) {
			return ResponseEntity.status(HttpStatus.OK).body(noteDao.findByemailID(email));
		} else
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	}

	@RequestMapping(method = RequestMethod.GET, value = "/note/{id}")
	public ResponseEntity<Note> getNote(@PathVariable(value = "id") String noteId,
			@RequestHeader(value = "Authorization", defaultValue = "noAuth") String auth) {
		try {
			String status = checkAuth(auth);

			if (status.equals("Success")) {
				Note nt = noteDao.findNoteUnderEmailList(noteId, email);
				if (nt == null) {
					throw new NoSuchElementException();
				}

				return ResponseEntity.status(HttpStatus.OK).body(nt);
			} else
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		} catch (NoSuchElementException ex) {
			ex.printStackTrace();
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
	}

	@RequestMapping(method = RequestMethod.PUT, value = "/note/{id}")
	public ResponseEntity<Note> updateNote(@PathVariable(value = "id") String noteId, @RequestBody Note nt,
			@RequestHeader(value = "Authorization", defaultValue = "noAuth") String auth) {
		try {
			String status = checkAuth(auth);
			if (status.equals("Success")) {
				Note originalNote = noteDao.findNoteUnderEmailList(noteId, email);
				if (originalNote == null) {
					throw new NoSuchElementException();
				}
				if (nt.getTitle().isEmpty() && nt.getContent().isEmpty()) {
					throw new Exception();
				}

				if (nt.getTitle() != null)
					originalNote.setTitle(nt.getTitle());

				if (nt.getContent() != null)
					originalNote.setContent(nt.getContent());

				originalNote.setEmailID(email);

				Note updateNote = noteDao.Save(originalNote);

				if (updateNote == null) {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
				}

				return ResponseEntity.status(HttpStatus.OK).body(updateNote);
			} else
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		} catch (NoSuchElementException ex) {
			ex.printStackTrace();
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		} catch (Exception ex) {
			ex.printStackTrace();
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		}
	}

	@RequestMapping(method = RequestMethod.DELETE, value = "/note/{id}")
	public ResponseEntity<Note> deleteNote(@PathVariable(value = "id") String noteId,
			@RequestHeader(value = "Authorization", defaultValue = "noAuth") String auth) {
		try {
			String status = checkAuth(auth);
			if (status.equals("Success")) {
				if (noteDao.DeleteNoteUnderEmailList(noteId, email)) {
						attachmentlist = attachDao.findBynoteID(noteId);
						for (attachment i : attachmentlist) {
							attachDao.deleteattachment(i.getAttachmentID());
							
							if (!fileStorageService.DeleteFile(i.getUrl()))
								throw new NoSuchElementException();
						}
					return ResponseEntity.status(HttpStatus.OK).build();
					/*if (attachDao.DeleteattachmentUnderNoteID(noteId)) {
						return ResponseEntity.status(HttpStatus.OK).build();
					}else {
						throw new NoSuchElementException();
					}*/
				}
				else {
					throw new NoSuchElementException();
				}
			} else
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		} catch (NoSuchElementException ex) {
			ex.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
	}

	@RequestMapping(method = RequestMethod.POST, value = "/note/{id}/attachments")
	public ResponseEntity<attachment> attach(@PathVariable(value = "id") String noteId,
			@RequestHeader(value = "Authorization", defaultValue = "noAuth") String auth,
			@RequestParam("file") MultipartFile file) {
		try {
			String status = checkAuth(auth);
			if (status.equals("Success")) {

				attachment aT = new attachment();

				
				Note nt = noteDao.findNoteUnderEmailList(noteId, email);
				if (nt == null) {
					throw new NoSuchElementException();
				}
				
				aT.setNote(nt);

				aT.setNoteID(noteId);

				String fileName = fileStorageService.storeFile(file);

				String fileDownloadUri = fileStorageService.getFileStorageLocation()+ "/" + file.getOriginalFilename();
						
				aT.setUrl(fileDownloadUri);

				aT.setmD(new metaData(fileName, fileDownloadUri, file.getContentType(), file.getSize()));
				
				aT = attachDao.Save(aT);
				
				return ResponseEntity.status(HttpStatus.OK).body(aT);
			} else
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		} catch (NoSuchElementException ex) {
			ex.printStackTrace();
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		} catch (Exception ex) {
			ex.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		} 
	}

	@RequestMapping(method = RequestMethod.GET, value = "/note/{id}/attachments")
	public ResponseEntity<List<attachment>> getAllNoteAttachments(
			@RequestHeader(value = "Authorization", defaultValue = "noAuth") String auth,
			@PathVariable(value = "id") String noteId) {
		String status = checkAuth(auth);
		if (status.equals("Success")) {
			return ResponseEntity.status(HttpStatus.OK).body(attachDao.findBynoteID(noteId));
		} else
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	}

	@RequestMapping(method = RequestMethod.PUT, value = "/note/{id}/attachments/{idattachments}")
	public ResponseEntity<attachment> updateNoteAttachments(@PathVariable(value = "id") String noteId,
			@PathVariable(value = "idattachments") String attachmentid,
			@RequestHeader(value = "Authorization", defaultValue = "noAuth") String auth,
			@RequestParam("file") MultipartFile file) {
		try {
			String status = checkAuth(auth);
			if (status.equals("Success")) {
				Note nt = noteDao.findNoteUnderEmailList(noteId, email);
				if (nt == null) {
					throw new NoSuchElementException();
				}

				attachment aT = attachDao.findattachmentUnderAttachmentList(attachmentid, noteId);

				if (aT == null) {
					throw new NoSuchElementException();
				}
				
				if (!fileStorageService.DeleteFile(aT.getUrl()))
					throw new NoSuchElementException(); 
				
				String fileName = fileStorageService.storeFile(file);

				String fileDownloadUri = fileStorageService.getFileStorageLocation()+ "/" + file.getOriginalFilename();

				//aT.setAttachmentID(attachmentid);
				
				aT.setNote(nt);
				
				aT.setUrl(fileDownloadUri);

				aT.setmD(new metaData(fileName, fileDownloadUri, file.getContentType(), file.getSize()));

				attachment updatedaT = attachDao.Save(aT);
				
				if (updatedaT == null) {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
				}
				return ResponseEntity.status(HttpStatus.OK).body(updatedaT);
			} else
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		} catch (NoSuchElementException ex) {
			ex.printStackTrace();
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		}
	}

	@RequestMapping(method = RequestMethod.DELETE, value = "/note/{id}/attachments/{idattachments}")
	public ResponseEntity<Note> deleteNoteAttachment(@PathVariable(value = "id") String noteId,
			@PathVariable(value = "idattachments") String attachmentid,
			@RequestHeader(value = "Authorization", defaultValue = "noAuth") String auth) {
		try {
			String status = checkAuth(auth);
			if (status.equals("Success")) {
				
				Note nt = noteDao.findNoteUnderEmailList(noteId, email);
				if (nt == null) {
					throw new NoSuchElementException();
				}
				
				attachment aT = attachDao.attachmentById(attachmentid).get();
				if (aT == null) {
					throw new NoSuchElementException();
				}
				
				if (!fileStorageService.DeleteFile(aT.getUrl()))
					throw new NoSuchElementException(); 
				
				if (attachDao.DeleteattachmentUnderNoteList(attachmentid, noteId))
					return ResponseEntity.status(HttpStatus.OK).build();
				else
					throw new NoSuchElementException();
			} else
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		} catch (NoSuchElementException ex) {
			ex.printStackTrace();
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		}
	}

	public String checkAuth(String auth) {
		String[] encodedValue = auth.split(" ");
		System.out.println("Auth Value:" + auth);
		String authValue = encodedValue[1];
		System.out.println("auth Value:" + authValue);
		byte[] decoded = Base64.decodeBase64(authValue.getBytes());

		String decodedValue = new String(decoded);
		System.out.println("Decoded Value:" + decodedValue);
		if (decodedValue.contains(":")) {
			String[] credentialValue;
			credentialValue = decodedValue.split(":");
			if (credentialValue.length < 2) {
				return "{\"RESPONSE\" : \"Credentials should not be empty\"}";
			}
			email = credentialValue[0];
			password = credentialValue[1];
		} else {
			return "{\"RESPONSE\" : \"Please register\"}";
		}
		System.out.println("email : " + email + "/t" + "password : " + password);

		// check for empty strings
		if (email.isEmpty() || password.isEmpty()) {
			return "{\"RESPONSE\" : \"Enter valid Credentials\"}";
		}
		userDetails = new register(email, password);

		if (!checkVaildEmailAddr(email) || !checkAlreadyPresent(userDetails) || !checkPassword(userDetails)) {
			return "{\"RESPONSE\" : \"Invalid credentials\"}";
		}
		return "Success";
	}

}
