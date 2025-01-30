package com.example.pictgram.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "comments")
@Data
@EqualsAndHashCode(callSuper = false)
public class Comment extends AbstractEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "comment_id_seq")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
    @ManyToOne //多対1
    @JoinColumn(name = "topicId")
    private Topic topic;
    
    @ManyToOne //多対1
    @JoinColumn(name = "userId")
    private User user;

	@Column(nullable = false, length = 1000)
	private String description;
}
