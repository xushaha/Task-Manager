package hexlet.code.controller;

import hexlet.code.dto.TaskStatusDto;
import hexlet.code.model.TaskStatus;
import hexlet.code.service.TaskStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
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

import java.util.List;

import static hexlet.code.controller.TaskStatusController.TASK_STATUS_CONTROLLER_PATH;
import static org.springframework.http.HttpStatus.CREATED;

@Tag(name = "Task status controller")
@AllArgsConstructor
@RestController
@RequestMapping("${base-url}" + TASK_STATUS_CONTROLLER_PATH)
public class TaskStatusController {

    private final TaskStatusService taskStatusService;

    public static final String TASK_STATUS_CONTROLLER_PATH = "/statuses";
    public static final String ID = "/{id}";

    // POST /api/statuses - создание нового статуса
    @Operation(summary = "Create new task status")
    @ApiResponse(responseCode = "201", description = "Task status created")
    @PostMapping
    @ResponseStatus(CREATED)
    public TaskStatus createTaskStatus(@RequestBody @Valid final TaskStatusDto taskStatusDto) {
        return taskStatusService.createNewTaskStatus(taskStatusDto);
    }

    // GET /api/statuses - получение списка статусов
    @Operation(summary = "Get list of task statuses")
    @ApiResponses(@ApiResponse(responseCode = "200",
        description = "List of task statuses was successfully loaded",
            content =
        @Content(schema = @Schema(implementation = TaskStatus.class))))
        @GetMapping
    public List<TaskStatus> getAllTaskStatuses() {
        return taskStatusService.getAllTaskStatuses();
    }

    // GET /api/statuses/{id} - получение статуса по идентификатору
    @Operation(summary = "Get task status by id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Task status was found"),
        @ApiResponse(responseCode = "404", description = "Task status with the id does not exist")
    })
    @GetMapping(ID)
    public TaskStatus getTaskStatusById(@PathVariable final Long id) {
        return taskStatusService.getTaskStatusById(id);
    }


    // PUT /api/statuses/{id} - обновление статуса
    @Operation(summary = "Update task status by id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Task status updated"),
        @ApiResponse(responseCode = "404", description = "Task status with the id not found")
    })
    @PutMapping(ID)
    public TaskStatus updateTaskStatus(@PathVariable("id") final Long id,
                                       @RequestBody @Valid final TaskStatusDto taskStatusDto) {
        return taskStatusService.updateTaskStatus(id, taskStatusDto);
    }



    // DELETE /api/statuses/{id} - удаление статуса
    @Operation(summary = "Delete a task status by id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Task status deleted"),
        @ApiResponse(responseCode = "404", description = "Task status with that id is not found")
    })
    @DeleteMapping(ID)
    public void deleteTaskStatus(@PathVariable("id") final long id) {
        taskStatusService.deleteTaskStatusById(id);
    }

}
