package com.example.pictgram.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.pictgram.entity.Topic;
import com.example.pictgram.entity.User;
import com.example.pictgram.entity.User.Authority;
import com.example.pictgram.entity.UserInf;
import com.example.pictgram.form.TopicForm;
import com.example.pictgram.form.UserForm;
import com.example.pictgram.repository.UserRepository;

@Controller
public class UsersController {

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private UserRepository repository;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private TopicsController controller;

	@GetMapping("/users/new")
	public String newUser(Model model) {
		model.addAttribute("form", new UserForm());
		return "users/new";
	}

	@PostMapping("/user")
	public String create(@Validated @ModelAttribute("form") UserForm form, BindingResult result, Model model,
			RedirectAttributes redirAttrs, Locale locale) {
		String name = form.getName();
		String email = form.getEmail();
		String password = form.getPassword();

		if (repository.findByUsername(email) != null) {
			FieldError fieldError = new FieldError(result.getObjectName(), "email",
					messageSource.getMessage("users.create.error.1", new String[] {}, locale));
			result.addError(fieldError);
		}
		if (result.hasErrors()) {
			model.addAttribute("hasMessage", true);
			model.addAttribute("class", "alert-danger");
			model.addAttribute("message", messageSource.getMessage("users.create.flash.1", new String[] {}, locale));
			return "users/new";
		}

		User entity = new User(email, name, passwordEncoder.encode(password), Authority.ROLE_USER);
		repository.saveAndFlush(entity);

		model.addAttribute("hasMessage", true);
		model.addAttribute("class", "alert-info");
		model.addAttribute("message", "ユーザー登録が完了しました。");

		return "layouts/complete";
	}
	@GetMapping("/users/{id}")
	public String show(@PathVariable("id") Long id, Model model) {
		
		User user = repository.getById(id);
		UserInf user_inf = (UserInf) user;

		List<Topic> topic_entities = user.getTopics();
		List<TopicForm> list = new ArrayList<>();
		for (Topic entity : topic_entities) {
			TopicForm form;
			try {
				form = controller.getTopic(user_inf, entity);
				list.add(form);
			} catch (IOException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
		}
		model.addAttribute("list", list);
		
		model.addAttribute("user", user);
		return "users/show";

	}
}