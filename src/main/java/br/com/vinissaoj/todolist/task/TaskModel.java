package br.com.vinissaoj.todolist.task;


import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;

import br.com.vinissaoj.todolist.utils.Utils;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import lombok.var;


@Data
@Entity(name = "tb_tasks")

public class TaskModel { 
   
 
  @Id
  @GeneratedValue(generator = "UUID")
  private UUID id;
  private String description;

  @Column(length = 50)
  private String title;
  private LocalDateTime startAt;
  private LocalDateTime endAt;
  private String priority;
  
  private UUID idUser;


  @CreationTimestamp
  private LocalDateTime createAt;

 public void setTitle(String title) throws Exception {
   if(title.length() > 50) {
    throw new Exception("O campo title deve conter no máximo 50 caracteres");
  }
  this.title = title;
 }


@PutMapping("/{id}")
public ResponseEntity<Object>update(TaskController taskController, UUID id, HttpServletRequest request, Object newParam) {
    
  var task = taskController.taskRepository.findById(id).orElse(null);
   
  if(task ==null){
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
     .body("Tarefa não encontrada");
  }
  
  var idUser = request.getAttribute("idUser");

  if(!task.getIdUser().equals(idUser)) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
    .body("Usuário não tem permissão para alterar essa tarefa");
  }

  Utils.copyNonNullProperties(this, task);
  var taskUpdated = taskController.taskRepository.save(task);    
  
  return ResponseEntity.ok().body(taskUpdated);
   
}
}
