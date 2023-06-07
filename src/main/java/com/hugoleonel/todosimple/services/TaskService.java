package com.hugoleonel.todosimple.services;

import java.lang.module.FindException;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;

import com.hugoleonel.todosimple.models.Task;
import com.hugoleonel.todosimple.models.User;
import com.hugoleonel.todosimple.repositories.TaskRepository;

public class TaskService {
    
    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserService userService;

    public Task finById(Long id) {

        Optional<Task> task = this.taskRepository.findById(id);
        return task.orElseThrow(() -> new RuntimeException(
            "Tarefa não encontrada Id: " + id + ", Tipo: " + Task.class.getName()));
    
    }

    @Transactional
    public Task create(Task task) {
        User user = this.userService.findById(task.getUser().getId());
        task.setId(null);
        task.setUser(user);
        task = this.taskRepository.save(task);
        return task;
    }

    public Task update(Task task) {
        Task newTask = finById(task.getId());
        newTask.setDescription(task.getDescription());
        return this.taskRepository.save(newTask);
    }

    public void delete(Long id){
        finById(id);
        try {
            this.taskRepository.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException("Não é possível deletar, pois há entidades relacionadas");
        }
    }

}
