package br.com.kiwi.app.task;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.kiwi.app.utils.Utils;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/task")
public class TaskController {
    @Autowired
    private iTaskRepository taskRepository;

    @PostMapping("/")
    public ResponseEntity<Object> create (@RequestBody TaskModel taskModel, HttpServletRequest request) {
        taskModel.setIdUser((UUID) request.getAttribute("idUser"));

        var currentDate = LocalDateTime.now();

        if (currentDate.isAfter(taskModel.getStartTime()) || currentDate.isAfter(taskModel.getEndTime())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("A data de início e de término devem ser posteriores à data atual");
        }

        if (taskModel.getStartTime().isAfter(taskModel.getEndTime())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("A data de início deve ser anterior à data de término");
        }

        var task = this.taskRepository.save(taskModel);
        return ResponseEntity.status(HttpStatus.OK).body(task);
    }

    @GetMapping("/")
    public List<TaskModel> list (HttpServletRequest request) {
        var userId = (UUID) request.getAttribute("idUser");
        var tasks = this.taskRepository.findByIdUser(userId);
        return tasks;
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> update (@RequestBody TaskModel taskModel, HttpServletRequest request, @PathVariable UUID id) {
        var idUser = (UUID) request.getAttribute("idUser");

        var task = this.taskRepository.findById(id).orElse(null);

        if (task == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Tarefa não encontrada");
        }

        if (!idUser.equals(task.getIdUser())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuário não tem permissão para alterar essa tarefa");
        }

        Utils.copyNonNullProperties(taskModel, task);

        var updatedTask = this.taskRepository.save(task);

        return ResponseEntity.status(HttpStatus.OK).body(updatedTask);
    }
}
