package hexlet.code.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskDto {

    @NotBlank
    private String name;

    private String description;

    @NotNull
    private Long taskStatusId;

    private Long executorId;

    private List<Long> labelIds;

}
