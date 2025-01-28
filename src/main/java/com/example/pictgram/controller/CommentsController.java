package com.example.pictgram.controller;

import java.security.Principal;
import java.util.Locale;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.pictgram.entity.Comment;
import com.example.pictgram.entity.Topic;
import com.example.pictgram.entity.User;
import com.example.pictgram.entity.UserInf;
import com.example.pictgram.form.CommentForm;
import com.example.pictgram.repository.CommentRepository;
import com.example.pictgram.repository.TopicRepository;
import com.example.pictgram.repository.UserRepository;

@Controller
public class CommentsController {

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private CommentRepository repository;
	
	@Autowired
	private TopicRepository topicRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@GetMapping("/topics/{topicId}/comments/new")
	public String newComment(@PathVariable("topicId") long topicId, Model model) {
		CommentForm form = new CommentForm();
		form.setTopicId(topicId);
		model.addAttribute("form", form);

		return "comments/new";
	}

	@PostMapping("/topics/{topicId}/comment")
	public String create(Principal principal, @PathVariable("topicId") long topicId, @Validated @ModelAttribute("form") CommentForm form,
			BindingResult result, Model model, RedirectAttributes redirAttrs, Locale locale) {
		if (result.hasErrors()) {
			model.addAttribute("hasMessage", true);
			model.addAttribute("class", "alert-danger");
			model.addAttribute("message", messageSource.getMessage("comments.create.flash.1", new String[] {}, locale));
			return "comments/new";
		}
		Comment entity = modelMapper.map(form, Comment.class);
		Authentication authentication = (Authentication) principal;
		UserInf user_inf = (UserInf) authentication.getPrincipal();
		String username = user_inf.getUsername();
		User user = userRepository.findByUsername(username);
		
//		entity.setTopicId(topicId);
		Topic topic = topicRepository.findById(topicId).get();
		entity.setTopic(topic);
		entity.setUser(user);
		
		repository.saveAndFlush(entity);

		redirAttrs.addFlashAttribute("hasMessage", true);
		redirAttrs.addFlashAttribute("class", "alert-info");
		redirAttrs.addFlashAttribute("message",
				messageSource.getMessage("comments.create.flash.2", new String[] {}, locale));

		return "redirect:/topics";
	}

}
