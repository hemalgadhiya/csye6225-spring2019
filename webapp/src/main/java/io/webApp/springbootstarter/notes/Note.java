package io.webApp.springbootstarter.notes;


import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="note")
@EntityListeners(AuditingEntityListener.class)
public class Note {
	
    @Id
    @GeneratedValue(generator = MyGenerator.generatorName)
    @GenericGenerator(name = MyGenerator.generatorName, strategy = "uuid")
    
	@Column(name="id")
    private String id;
	@Column(name="title")
	private String title;
	@Column(name="content")
	private String content;
	@Column(name="created_on")
	@Temporal(TemporalType.TIMESTAMP)
	private Date created_on;
	@Column(name="last_updated_on")
	@Temporal(TemporalType.TIMESTAMP)
	@LastModifiedDate
	private Date last_updated_on;
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="emailID")
	@JsonIgnore
	private String emailID;
	
	@PrePersist
	protected void onCreate()
	{
		created_on = new Date();
		last_updated_on = new Date();
	}
	

	@PreUpdate
	public void getUpdateAt()
	{
		last_updated_on = new Date();
	}


	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
	}


	public String getContent() {
		return content;
	}


	public void setContent(String content) {
		this.content = content;
	}


	public Date getCreated_on() {
		return created_on;
	}


	public void setCreated_on(Date created_on) {
		this.created_on = created_on;
	}


	public Date getLast_updated_on() {
		return last_updated_on;
	}


	public void setLast_updated_on(Date last_updated_on) {
		this.last_updated_on = last_updated_on;
	}


	public String getEmailID() {
		return emailID;
	}


	public void setEmailID(String emailID) {
		this.emailID = emailID;
	}




}
