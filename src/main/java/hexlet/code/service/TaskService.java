package hexlet.code.service;

import com.querydsl.core.types.Predicate;
import hexlet.code.dto.TaskDto;
import hexlet.code.model.Task;


public interface TaskService {

    Task createNewTask(TaskDto taskDto);

    Iterable<Task> getAllTasks(Predicate predicate);

    Task getTaskById(Long id);

    Task updateTask(Long id, TaskDto taskDto);

    void deleteTaskById(Long id);

}
