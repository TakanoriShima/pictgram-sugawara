package com.example.pictgram.controller;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.pictgram.entity.Favorite;
import com.example.pictgram.entity.Topic;
import com.example.pictgram.entity.User;
import com.example.pictgram.entity.UserInf;
import com.example.pictgram.form.TopicForm;
import com.example.pictgram.repository.FavoriteRepository;
import com.example.pictgram.repository.TopicRepository;
import com.example.pictgram.repository.UserRepository;

@Controller
public class FavoritesController {

	@Autowired
	private MessageSource messageSource;
	@Autowired
	private FavoriteRepository repository;
	@Autowired
	private TopicsController topicsController;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private TopicRepository topicRepository;

	@GetMapping("/favorites")
	public String index(Principal principal, Model model) throws IOException {
		Authentication authentication = (Authentication) principal;
		UserInf userInf = (UserInf) authentication.getPrincipal();
		Long userId = userInf.getUserId();
		User user = userRepository.findById(userId).get();
		List<Favorite> favorites = repository.findByUserOrderByUpdatedAtDesc(user);
		List<TopicForm> list = new ArrayList<>();
		for (Favorite favorite : favorites) {
			Topic topic = favorite.getTopic();
			TopicForm form = topicsController.getTopic(user, topic);
			list.add(form);
		}
		model.addAttribute("list", list);

		return "topics/index";
	}

	@PostMapping("/favorite")
	public String create(Principal principal, @RequestParam("topic_id") long topicId, RedirectAttributes redirAttrs,
			Locale locale) {
		Authentication authentication = (Authentication) principal;
		UserInf userInf = (UserInf) authentication.getPrincipal();
		Long userId = userInf.getUserId();
		User user = userRepository.findByUserId(userId).get();
		Topic topic = topicRepository.findById(topicId).get();
		List<Favorite> results = repository.findByUserAndTopic(user, topic);
		if (results.size() == 0) {
			Favorite favorite = new Favorite();
			favorite.setUser(user);
			favorite.setTopic(topic);
			repository.saveAndFlush(favorite);

			redirAttrs.addFlashAttribute("hasMessage", true);
			redirAttrs.addFlashAttribute("class", "alert-info");
			redirAttrs.addFlashAttribute("message",
					messageSource.getMessage("favorites.create.flash", new String[] {}, locale));
		}

		return "redirect:/topics";
	}

	@DeleteMapping("/favorite")
	@Transactional
	public String destroy(Principal principal, @RequestParam("topic_id") long topicId, RedirectAttributes redirAttrs,
			Locale locale) {
		Authentication authentication = (Authentication) principal;
		UserInf userInf = (UserInf) authentication.getPrincipal();
		Long userId = userInf.getUserId();
		User user = userRepository.findByUserId(userId).get();
		Topic topic = topicRepository.findById(topicId).get();
		List<Favorite> results = repository.findByUserAndTopic(user, topic);
		if (results.size() == 1) {
			repository.deleteByUserAndTopic(user, topic);

			redirAttrs.addFlashAttribute("hasMessage", true);
			redirAttrs.addFlashAttribute("class", "alert-info");
			redirAttrs.addFlashAttribute("message",
					messageSource.getMessage("favorites.destroy.flash", new String[] {}, locale));
		}
		return "redirect:/topics";
	}
}