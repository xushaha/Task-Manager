package hexlet.code.controller;

import com.querydsl.core.types.Predicate;
import hexlet.code.dto.TaskDto;
import hexlet.code.model.Task;
import hexlet.code.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static hexlet.code.controller.TaskController.TASK_CONTROLLER_PATH;
import static org.springframework.http.HttpStatus.CREATED;

@Tag(name = "Task controller")
@RestController
@RequestMapping("${base-url}" + TASK_CONTROLLER_PATH)
@AllArgsConstructor
public class TaskController {

    private final TaskService taskService;
    public static final String TASK_CONTROLLER_PATH = "/tasks";
    public static final String ID = "/{id}";
    private static final String ONLY_OWNER_BY_ID = """
                @userRepository.findById(#id).get().getEmail() == authentication.getName()
            """;

    // POST /api/tasks - создание новой задачи
    @Operation(summary = "Create new task")
    @ApiResponse(responseCode = "201", description = "Task created")
    @PostMapping
    @ResponseStatus(CREATED)
    public Task createTask(
            @Parameter(schema = @Schema(implementation = TaskDto.class))
            @RequestBody @Valid final TaskDto taskDto) {
        return taskService.createNewTask(taskDto);
    }

    // GET /api/tasks - получение списка задач
    @Operation(summary = "Get tasks by filtration")
    @ApiResponse(responseCode = "200", description = "Tasks by filtration was successfully found")
    @GetMapping
    public Iterable<Task> getAllTasks(@QuerydslPredicate(root = Task.class) Predicate predicate) {
        return taskService.getAllTasks(predicate);
    }

    // GET /api/tasks/{id} - получение задачи по идентификатору
    @Operation(summary = "Get task by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Task was found"),
        @ApiResponse(responseCode = "404", description = "Task with this ID does not exist")
    })
    @GetMapping(ID)
    public Task getTaskById(@PathVariable final Long id) {
        return taskService.getTaskById(id);
    }

    // PUT /api/tasks/{id} - обновление задачи
    @Operation(summary = "Update task by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Task updated"),
        @ApiResponse(responseCode = "404", description = "Task with this ID not found")
    })
    @PutMapping(ID)
    @PreAuthorize(ONLY_OWNER_BY_ID)
    public Task updateTask(@PathVariable("id") final Long id,
                                       @RequestBody @Valid final TaskDto taskDto) {
        return taskService.updateTask(id, taskDto);
    }

    // DELETE /api/tasks/{id} - удаление задачи
    @Operation(summary = "Delete a task by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Task deleted"),
        @ApiResponse(responseCode = "404", description = "Task with that ID is not found")
    })
    @DeleteMapping(ID)
    @PreAuthorize(ONLY_OWNER_BY_ID)
    public void deleteTask(@PathVariable("id") final Long id) {
        taskService.deleteTaskById(id);
    }
}
