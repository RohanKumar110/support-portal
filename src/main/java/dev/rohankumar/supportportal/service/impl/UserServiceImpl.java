package dev.rohankumar.supportportal.service.impl;

import dev.rohankumar.supportportal.domain.User;
import dev.rohankumar.supportportal.exception.domain.EmailExistException;
import dev.rohankumar.supportportal.exception.domain.EmailNotFoundException;
import dev.rohankumar.supportportal.exception.domain.UserNotFoundException;
import dev.rohankumar.supportportal.exception.domain.UsernameExistException;
import dev.rohankumar.supportportal.model.UserPrincipal;
import dev.rohankumar.supportportal.repository.UserRepository;
import dev.rohankumar.supportportal.service.UserService;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import static dev.rohankumar.supportportal.constant.UserServiceConstant.*;
import static dev.rohankumar.supportportal.enumeration.Role.ROLE_USER;

@Service
@Transactional
public class UserServiceImpl implements UserService, UserDetailsService {

    private final Logger LOG;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final LoginAttemptService loginAttemptService;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           LoginAttemptService loginAttemptService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.loginAttemptService = loginAttemptService;
        LOG = LoggerFactory.getLogger(UserServiceImpl.class);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found by username: "+username));
        validateLoginAttempt(user);
        user.setLastLoginDateDisplay(user.getLastLoginDate());
        user.setLastLoginDate(new Date());
        userRepository.save(user);
        UserPrincipal userPrincipal = new UserPrincipal(user);
        LOG.info("User found by username: "+username);
        return userPrincipal;
    }

    @Override
    public User register(String firstName, String lastName, String username, String email) {
        validateNewUsernameAndEmail(StringUtils.EMPTY,username,email);
        User user = new User();
        user.setUserId(generateUserId());
        String password = generatePassword();
        String encodedPassword = encodePassword(password);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUsername(username);
        user.setEmail(email);
        user.setJoinDate(new Date());
        user.setPassword(encodedPassword);
        user.setActive(true);
        user.setNotLocked(true);
        user.setRole(ROLE_USER.name());
        user.setAuthorities(ROLE_USER.getAuthorities());
        user.setProfileImageUrl(getTemporaryProfileImageUrl());
        User savedUser = userRepository.save(user);
        LOG.info("New User Password {}",password);
        return savedUser;
    }

    @Override
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(NO_USER_FOUND_BY_USERNAME+username));
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findUserByEmail(email)
                .orElseThrow(() -> new EmailNotFoundException(NO_USER_FOUND_BY_EMAIL+email));
    }

    private String generateUserId() {
        return RandomStringUtils.randomNumeric(10);
    }

    private String generatePassword() {
        return RandomStringUtils.randomAlphanumeric(10);
    }

    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    private String getTemporaryProfileImageUrl(){
        return ServletUriComponentsBuilder.fromCurrentContextPath().path(DEFAULT_USER_IMAGE_PATH).toUriString();
    }

    private void validateLoginAttempt(User user)  {
        if(user.isNotLocked()){
            user.setNotLocked(!loginAttemptService.hasExceededMaxAttempts(user.getUsername()));
        }else{
            loginAttemptService.evictUserFromLoginAttemptCache(user.getUsername());
        }
    }

    private User validateNewUsernameAndEmail(String currentUsername,String newUsername, String email) {

        Optional<User> userByUsername = userRepository.findUserByUsername(newUsername);
        Optional<User> userByEmail = userRepository.findUserByEmail(email);

        if(StringUtils.isNotEmpty(currentUsername)){
            User currentUser = userRepository.findUserByUsername(currentUsername)
                    .orElseThrow(() -> new UserNotFoundException(NO_USER_FOUND_BY_USERNAME + currentUsername));
            if(userByUsername.isPresent() && !currentUser.getId().equals(userByUsername.get().getId())){
                throw new UsernameExistException(USERNAME_ALREADY_EXISTS);
            }
            if(userByEmail.isPresent() && !currentUser.getId().equals(userByEmail.get().getId())){
                throw new EmailExistException(EMAIL_ALREADY_EXISTS);
            }
            return currentUser;
        }else{
            if(userByUsername.isPresent()){
                throw new UsernameExistException(USERNAME_ALREADY_EXISTS);
            }
            if(userByEmail.isPresent()){
                throw new EmailExistException(EMAIL_ALREADY_EXISTS);
            }
            return null;
        }
    }
}
