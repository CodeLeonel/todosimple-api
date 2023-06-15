package com.hugoleonel.todosimple.services;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hugoleonel.todosimple.models.Task;
import com.hugoleonel.todosimple.models.User;
import com.hugoleonel.todosimple.repositories.TaskRepository;
import com.hugoleonel.todosimple.services.exceptions.DataBindingViolationException;
import com.hugoleonel.todosimple.services.exceptions.ObjectNotFoundException;

@Service
public class TaskService {
    
    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserService userService;

    public Task findById(Long id) {

        Optional<Task> task = this.taskRepository.findById(id);
        return task.orElseThrow(() -> new ObjectNotFoundException(
            "Tarefa não encontrada Id: " + id + ", Tipo: " + Task.class.getName()));
    
    }

    public List<Task> findAllByUserId(Long userId) {
        
        this.userService.findById(userId);
        
        List<Task> tasks = this.taskRepository.findByUser_Id(userId);
        
        return tasks;

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

}
