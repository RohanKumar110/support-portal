package dev.rohankumar.supportportal.service;

import dev.rohankumar.supportportal.domain.User;

import java.util.List;

public interface UserService {

    User register(String firstName, String lastName, String username, String email);

    List<User> getUsers();

    User findByUsername(String username);

    User findByEmail(String email);
}
