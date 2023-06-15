package com.hugoleonel.todosimple.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hugoleonel.todosimple.models.User;
import com.hugoleonel.todosimple.repositories.UserRepository;
import com.hugoleonel.todosimple.services.exceptions.DataBindingViolationException;
import com.hugoleonel.todosimple.services.exceptions.ObjectNotFoundException;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;

    public User findById(Long id) {
        Optional<User> user = this.userRepository.findById(id);

        return user.orElseThrow(() -> new ObjectNotFoundException(
            "Usuário não encontrado! Id: " + id + " Tipo: " + User.class.getName()
        ));
    }

    @Transactional
    public User create(User user) {
        user.setId(null);
        user = this.userRepository.save(user);
        return user;
    }

    @Transactional
    public User update(User user) {
        User userToUpdate = findById(user.getId());
        userToUpdate.setPassword(user.getPassword());
        return this.userRepository.save(userToUpdate);
    }

    public void delete(Long id) {
        findById(id);
        try {
            this.userRepository.deleteById(id);
        } catch (Exception e) {
            throw new DataBindingViolationException("Não é possível excluir pois há entidades relacionadas");
        }
    }

}
