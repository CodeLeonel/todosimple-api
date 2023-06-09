package com.hugoleonel.todosimple.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hugoleonel.todosimple.models.Task;
import com.hugoleonel.todosimple.models.projection.TaskProjection;

@Repository
public interface TaskRepository extends JpaRepository<Task,Long>{
    
    //JPQL
    @Query(value = "SELECT t FROM Task t WHERE t.user.id = :id")

    //SQL
    //@Query(value = "SELECT * FROM task t WHERE t.user_id = :id", nativeQuery = true)
    List<TaskProjection> findByUser_Id(@Param("id") Long id);

}
