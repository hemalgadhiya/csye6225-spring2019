package io.webApp.springbootstarter.notes;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import io.webApp.springbootstarter.notes.Note;

public interface NoteRepository extends JpaRepository<Note,String>{

	List<Note> findByemailID(String emailID);
	
}
