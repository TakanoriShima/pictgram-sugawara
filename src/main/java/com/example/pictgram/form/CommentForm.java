package com.example.pictgram.form;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import com.example.pictgram.entity.User;

import lombok.Data;

@Data
public class CommentForm {
	private Long id;

	private Long topicId;
	
	private User user;

	@NotEmpty
	@Size(max = 1000)
	private String description;
}
