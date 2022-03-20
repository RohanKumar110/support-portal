package dev.rohankumar.supportportal.resource;

import dev.rohankumar.supportportal.domain.User;
import dev.rohankumar.supportportal.exception.ExceptionHandling;
import dev.rohankumar.supportportal.model.UserPrincipal;
import dev.rohankumar.supportportal.service.UserService;
import dev.rohankumar.supportportal.utility.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import static dev.rohankumar.supportportal.constant.SecurityConstant.JWT_TOKEN_HEADER;

@RestController
@RequestMapping(path = {"/","/user"})
public class UserResource extends ExceptionHandling {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public UserResource(UserService userService,
                        AuthenticationManager authenticationManager,
                        JwtTokenProvider jwtTokenProvider) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestBody User user){
        authenticate(user.getUsername(),user.getPassword());
        User loginUser = userService.findByUsername(user.getUsername());
        UserPrincipal userPrincipal = new UserPrincipal(loginUser);
        HttpHeaders jwtHeader = getJwtHeader(userPrincipal);
        return new ResponseEntity<>(loginUser,jwtHeader,HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user){
        User savedUser = userService.register(user.getFirstName(),user.getLastName(),user.getUsername(),user.getEmail());
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }

    private HttpHeaders getJwtHeader(UserPrincipal userPrincipal) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(JWT_TOKEN_HEADER,jwtTokenProvider.generateToken(userPrincipal));
        return headers;
    }

    private void authenticate(String username,String password){
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username,password));
    }
}