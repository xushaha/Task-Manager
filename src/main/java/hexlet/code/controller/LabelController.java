package hexlet.code.controller;

import hexlet.code.dto.LabelDto;
import hexlet.code.model.Label;
import hexlet.code.service.LabelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
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

import java.util.List;

import static hexlet.code.controller.LabelController.LABEL_CONTROLLER_PATH;
import static org.springframework.http.HttpStatus.CREATED;

@Tag(name = "Label controller")
@AllArgsConstructor
@RestController
@RequestMapping("${base-url}" + LABEL_CONTROLLER_PATH)
public class LabelController {
    public static final String LABEL_CONTROLLER_PATH = "/labels";
    public static final String ID = "/{id}";
    private static final String ONLY_OWNER_BY_ID = """
                @userRepository.findById(#id).get().getEmail() == authentication.getName()
            """;

    private final LabelService labelService;

    // POST /api/labels - создание новой метки
    @Operation(summary = "Create new label")
    @ApiResponse(responseCode = "201", description = "label created")
    @PostMapping
    @ResponseStatus(CREATED)
    public Label createTaskStatus(@RequestBody @Valid final LabelDto labelDto) {
        return labelService.createNewLabel(labelDto);
    }

    // GET /api/labels - получение списка меток
    @Operation(summary = "Get list of labels")
    @ApiResponses(@ApiResponse(responseCode = "200",
            description = "List of tasks was successfully loaded",
            content =
            @Content(schema = @Schema(implementation = Label.class))))
    @GetMapping
    public List<Label> getAllLabels() {
        return labelService.getAllLabels();
    }

    // GET /api/labels/{id} - получение метки по идентификатору
    @Operation(summary = "Get label by id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Task was found"),
        @ApiResponse(responseCode = "404", description = "Task with this id does not exist")
    })
    @GetMapping(ID)
    public Label getLabelById(@PathVariable final Long id) {
        return labelService.getLabelById(id);
    }

    // PUT /api/labels/{id} - обновление метки
    @Operation(summary = "Update label by id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Task updated"),
        @ApiResponse(responseCode = "404", description = "Task with this id not found")
    })
    @PutMapping(ID)
    @PreAuthorize(ONLY_OWNER_BY_ID)
    public Label updateLabel(@PathVariable("id") final Long id,
                             @RequestBody @Valid final LabelDto labelDto) {
        return labelService.updateLabel(id, labelDto);
    }

    // DELETE /api/labels/{id} - удаление метки
    @Operation(summary = "Delete a label by id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Label deleted"),
        @ApiResponse(responseCode = "404", description = "Label with that id is not found")
    })
    @DeleteMapping(ID)
    @PreAuthorize(ONLY_OWNER_BY_ID)
    public void deleteLabel(@PathVariable("id") final long id) {
        labelService.deleteLabelById(id);
    }

}
