package es.entreVarales.repository;


import es.entreVarales.model.User;
import es.entreVarales.model.User.Role;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);  // Buscamos el User por el nombre de usuario
    List<User> findByRole(Role role);
    
}