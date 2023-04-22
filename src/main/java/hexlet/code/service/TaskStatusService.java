package hexlet.code.service;

import hexlet.code.dto.TaskStatusDto;
import hexlet.code.model.TaskStatus;

import java.util.List;


public interface TaskStatusService {

    List<TaskStatus> getAllTaskStatuses();

    TaskStatus createNewTaskStatus(TaskStatusDto taskStatusDto);

    TaskStatus updateTaskStatus(Long id, TaskStatusDto taskStatusDto);

    TaskStatus getTaskStatusById(Long id);

    void deleteTaskStatusById(Long id);

}
