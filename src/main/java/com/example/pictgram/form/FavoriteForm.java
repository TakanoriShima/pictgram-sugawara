package com.example.pictgram.form;

import com.example.pictgram.entity.Topic;
import com.example.pictgram.entity.User;

import lombok.Data;

@Data
public class FavoriteForm {

	private User user;

	private Topic topic;

	private TopicForm topicForm;
}