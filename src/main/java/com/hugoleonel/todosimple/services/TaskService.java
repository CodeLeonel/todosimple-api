package com.hugoleonel.todosimple.services;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hugoleonel.todosimple.models.Task;
import com.hugoleonel.todosimple.models.User;
import com.hugoleonel.todosimple.models.enums.ProfileEnum;
import com.hugoleonel.todosimple.repositories.TaskRepository;
import com.hugoleonel.todosimple.security.UserSpringSecurity;
import com.hugoleonel.todosimple.services.exceptions.AuthorizationException;
import com.hugoleonel.todosimple.services.exceptions.DataBindingViolationException;
import com.hugoleonel.todosimple.services.exceptions.ObjectNotFoundException;

@Service
public class TaskService {
    
    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserService userService;

    public Task findById(Long id) {

        Task task = this.taskRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException(
            "Tarefa não encontrada Id: " + id + ", Tipo: " + Task.class.getName()));

        UserSpringSecurity userSpringSecurity = UserService.authenticated();
        if(Objects.isNull(userSpringSecurity)
            || !userSpringSecurity.hasRole(ProfileEnum.ADMIN) && !userHasTask(userSpringSecurity, task))
            throw new AuthorizationException("Acesso negado!");

        return task;
    
    }

    public List<Task> findAllByUserId() {
        
        UserSpringSecurity userSpringSecurity = UserService.authenticated();

        if(Objects.isNull(userSpringSecurity))
            throw new AuthorizationException("Acesso negado!");
        
        List<Task> tasks = this.taskRepository.findByUser_Id(userSpringSecurity.getId());
        
        return tasks;

    }

    @Transactional
    public Task create(Task task) {
        UserSpringSecurity userSpringSecurity = UserService.authenticated();

        if(Objects.isNull(userSpringSecurity))
            throw new AuthorizationException("Acesso Negado!");
        
        User user = this.userService.findById(userSpringSecurity.getId());
        task.setId(null);
        task.setUser(user);
        task = this.taskRepository.save(task);
        return task;
    }

    public Task update(Task task) {
        Task newTask = findById(task.getId());
        newTask.setDescription(task.getDescription());
        return this.taskRepository.save(newTask);
    }

    public void delete(Long id){
        findById(id);
        try {
            this.taskRepository.deleteById(id);
        } catch (Exception e) {
            throw new DataBindingViolationException("Não é possível deletar, pois há entidades relacionadas");
        }
    }

    private Boolean userHasTask(UserSpringSecurity userSpringSecurity, Task task) {
        return task.getUser().getId().equals(userSpringSecurity.getId());
    }

}
