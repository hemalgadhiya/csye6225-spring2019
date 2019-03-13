package io.webApp.springbootstarter.attachments;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.webApp.springbootstarter.notes.Note;

@Entity
@Table(name="attachment")
@EntityListeners(AuditingEntityListener.class)
public class attachment {

	@Id
    @Column(name = "attachmentId")
    @GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
	private String attachmentID;
	
	@Column(name="url")
	private String url;
	
	@Column(name="noteID")
	@JsonIgnore
	private String noteID;
	
	@Column(name = "MetaData", length = 2048)
	@JsonIgnore
	private String mD;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "NOTE_ID", nullable = false)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JsonIgnore
	private Note note;

	public String getAttachmentID() {
		return attachmentID;
	}

	public void setAttachmentID(String attachmentID) {
		this.attachmentID = attachmentID;
	}

	public String getmD() {
		return mD;
	}

	public void setmD(metaData mD) {
		this.mD = mD.toString();
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getNoteID() {
		return noteID;
	}

	public void setNoteID(String noteID) {
		this.noteID = noteID;
	}

	@Override
	public String toString() {
		return "[\"id\":\"" + attachmentID + ", \"url\":\"" + url + "]";
	}

	public Note getNote() {
		return note;
	}

	public void setNote(Note note) {
		this.note = note;
	}

}
