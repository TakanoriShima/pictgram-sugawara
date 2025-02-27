package com.example.pictgram.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.pictgram.entity.Favorite;
import com.example.pictgram.entity.Topic;
import com.example.pictgram.entity.User;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

	public Optional<Favorite> findById(Long id);

	public List<Favorite> findByUserOrderByUpdatedAtDesc(User user);

	public List<Favorite> findByUserAndTopic(User user, Topic topic);

	public void deleteByUserAndTopic(User user, Topic topic);

}