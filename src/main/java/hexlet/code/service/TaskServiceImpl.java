package hexlet.code.service;

import com.querydsl.core.types.Predicate;
import hexlet.code.dto.TaskDto;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.LabelRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final UserService userService;
    private final UserService labelService;
    private final TaskStatusService taskStatusService;
    private final LabelRepository labelRepository;

    @Override
    public Task createNewTask(TaskDto taskDto) {
        final Task newTask = fromDto(taskDto);
        return taskRepository.save(newTask);
    }


    public Iterable<Task> getAllTasks(Predicate predicate) {
        return taskRepository.findAll(predicate);
    }

    @Override
    public Task getTaskById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("The task with this ID does not exist"));
    }

    @Override
    public Task updateTask(Long id, TaskDto taskDto) {
        final Task task = getTaskById(id);
        merge(task, taskDto);
        return taskRepository.save(task);
    }

    @Override
    public void deleteTaskById(Long id) {
        taskRepository.deleteById(id);
    }

    private Task fromDto(final TaskDto taskDto) {
        final User author = userService.getCurrentUser();

        Long taskStatusId = taskDto.getTaskStatusId();
        final TaskStatus taskStatus = taskStatusService.getTaskStatusById(taskStatusId);
        final Long executorId = taskDto.getExecutorId();
        Task task = new Task();

        if (executorId != null) {
            task.setExecutor(userService.getUserById(executorId));
        }

        if (taskDto.getLabelIds() != null) {
            final List<Label> labels = labelRepository.findAllById(taskDto.getLabelIds());
            task.setLabels(labels);
        }
        task.setName(taskDto.getName());
        task.setDescription(taskDto.getDescription());
        task.setTaskStatus(taskStatus);
        task.setAuthor(author);

        return task;

    }

    private void merge(final Task task, final TaskDto taskDto) {
        final Task newTask = fromDto(taskDto);
        task.setName(newTask.getName());
        task.setDescription(newTask.getDescription());
        task.setAuthor(newTask.getAuthor());
        task.setExecutor(newTask.getExecutor());
        task.setTaskStatus(newTask.getTaskStatus());
        task.setLabels(newTask.getLabels());
    }
}
