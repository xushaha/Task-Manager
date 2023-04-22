package hexlet.code.service;

import com.querydsl.core.types.Predicate;
import hexlet.code.dto.TaskDto;
import hexlet.code.model.Task;

import java.util.List;

public interface TaskService {

    Task createNewTask(TaskDto taskDto);

    Iterable<Task> getAllTasks(Predicate predicate);

    Task getTaskById(Long id);

    Task updateTask(Long id, TaskDto taskDto);

    void deleteTaskById(Long id);

}
