package recipes.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import recipes.persistence.User;
import recipes.persistence.UserRepository;

import java.util.Optional;

@RestController
public class RegisterController {
    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    /**
     * POST /api/register endpoint for user registration via JSON Object
     * The JSON object must specify two keys, username and password and their corresponding values.
     * The username should have 3 or more characters, and the password 8 or more characters
     * example: { "username":"user1", "password":"pass1" }
     * @param user
     * @return ResponseEntity with status HttpStatus OK if the data is valid, or BAD REQUEST if not valid or
     * if the user already exists
     */
    @PostMapping("/api/register")
    ResponseEntity<?> registerUser(@RequestBody User user) {
        validateUser(user);
        Optional<User> optionalUser = userRepository.findUserByUsername(user.getUsername());
        if (optionalUser.isPresent()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else {
            user.setRole("ROLE_USER");
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userRepository.save(user);
            return new ResponseEntity<>(HttpStatus.OK);
        }

    }

    /**
     * Checks if the user has a username of at least 3 characters
     * and a password of at least 8 characters
     * If the conditions are not satisfied it throws
     * ResponseStatusException with {@linkplain HttpStatus#BAD_REQUEST BAD_REQUEST}
     * @param user an instance of User
     * */
    public void validateUser(User user) {
        if (user == null) badReq();
        if (user.getUsername() == null || user.getUsername().length() < 3) badReq();
        if (user.getPassword() == null  || user.getPassword().trim().length() < 8) badReq();
    }
    public static void badReq() {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }
}

