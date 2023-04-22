package hexlet.code.service;

import com.querydsl.core.types.Predicate;
import hexlet.code.dto.TaskDto;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.TaskRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@Transactional
@AllArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final UserService userService;
    private final TaskStatusService taskStatusService;

    // POST /api/tasks - создание новой задачи
    @Override
    public Task createNewTask(TaskDto taskDto) {
        final Task newTask = fromDto(taskDto);
        return taskRepository.save(newTask);
    }

    // GET /api/tasks - получение списка задач
    public Iterable<Task> getAllTasks(Predicate predicate) {
        return taskRepository.findAll(predicate);
    }


    // GET /api/tasks/{id} - получение задачи по идентификатору
    @Override
    public Task getTaskById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("The task with this id does not exist"));
    }

    // PUT /api/tasks/{id} - обновление задачи
    @Override
    public Task updateTask(Long id, TaskDto taskDto) {
        final Task task = taskRepository.findById(id).get();
        merge(task, taskDto);
        return taskRepository.save(task);
    }

    // DELETE /api/tasks/{id} - удаление задачи
    @Override
    public void deleteTaskById(Long id) {
        taskRepository.deleteById(id);
    }

    private Task fromDto(final TaskDto taskDto) { //to add labels

        final User author = userService.getCurrentUser();
        Long taskStatusId = taskDto.getTaskStatusId();
        final TaskStatus taskStatus = taskStatusService.getTaskStatusById(taskStatusId);
        final Long executorId = taskDto.getExecutorId();
        Task task = new Task();

        if (executorId != null) {
            task.setExecutor(userService.getUserById(executorId));
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
        task.setName(newTask.getName());
        task.setDescription(newTask.getDescription());
        task.setExecutor(newTask.getExecutor());
        task.setTaskStatus(newTask.getTaskStatus());
        task.setLabels(newTask.getLabels());
    }
}
