package es.entreVarales.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.entreVarales.model.User;
import es.entreVarales.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User registerUser(User user) {
        // Puedes encriptar la contraseña aquí si deseas
        return userRepository.save(user);
    }

    public User loginUser(String username, String password) {
        return userRepository.findByUsername(username)
                .filter(u -> u.getPassword().equals(password))
                .orElse(null);
    }
}
