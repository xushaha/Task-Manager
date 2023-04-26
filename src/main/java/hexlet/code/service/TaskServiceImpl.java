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

import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;


@Service
@Transactional
@AllArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final UserService userService;
    private final TaskStatusService taskStatusService;
    private final LabelRepository labelRepository;


    @Override
    public Task createNewTask(TaskDto taskDTO) {
        Task task = new Task();
        fromDto(taskDTO, task);
        return taskRepository.save(task);
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
        Task task = getTaskById(id);
        fromDto(taskDto, task);
        return taskRepository.save(task);
    }

    @Override
    public void deleteTaskById(Long id) {
        taskRepository.deleteById(id);
    }

    private void fromDto(TaskDto taskDTO, Task task) {
        final User author = userService.getCurrentUser();
        final TaskStatus taskStatus = taskStatusService.getTaskStatusById(taskDTO.getTaskStatusId());
        final Long executorId = taskDTO.getExecutorId();
        if (executorId != null) {
            task.setExecutor(userService.getUserById(executorId));
        }
        if (taskDTO.getLabelIds() != null) {
            final Set<Label> labels = new HashSet<>(labelRepository.findAllById(taskDTO.getLabelIds()));
            task.setLabels(labels);
        }
        task.setName(taskDTO.getName());
        task.setDescription(taskDTO.getDescription());
        task.setAuthor(author);
        task.setTaskStatus(taskStatus);
    }

}
