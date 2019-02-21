package io.webApp.springbootstarter.attachments;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.webApp.springbootstarter.attachments.attachment;
import io.webApp.springbootstarter.attachments.attachmentRepository;

@Service
public class attachmentDao {

	@Autowired
	attachmentRepository attachmentRepo;

	// TableCreation tableCreation;

	// Save a attachment

	public attachment Save(attachment nt) {
		return attachmentRepo.save(nt);
	}

	// get a all attachment
	public List<attachment> findAll() {
		return attachmentRepo.findAll();
	}

	// get a attachment by ID
	public Optional<attachment> attachmentById(String attachmentid) {
		return attachmentRepo.findById(attachmentid);
	}

	// delete a attachment by Id
	public void deleteattachment(String attachmentid) {
		attachmentRepo.deleteById(attachmentid);
	}

	public List<attachment> findBynoteID(String noteID) {
		return attachmentRepo.findBynoteID(noteID);
	}

	public attachment findattachmentUnderAttachmentList(String attachmentID, String noteID) {
		List<attachment> attachmentList = findBynoteID(noteID);
		for (attachment i : attachmentList) {
			if (i.getAttachmentID().equals(attachmentID))
				return i;
		}
		return null;
	}

	public boolean DeleteattachmentUnderNoteList(String attachmentID, String noteID) {
		List<attachment> attachmentList = findBynoteID(noteID);
		for (attachment i : attachmentList) {
			if (i.getAttachmentID().equals(attachmentID)) {
				deleteattachment(i.getAttachmentID());
				return true;
			}
		}
		return false;
	}
}
