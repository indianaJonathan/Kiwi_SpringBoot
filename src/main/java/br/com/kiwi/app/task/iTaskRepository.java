package br.com.kiwi.app.task;

import java.util.UUID;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface iTaskRepository extends JpaRepository<TaskModel, UUID>{
    public List<TaskModel> findByIdUser(UUID idUser);
}
