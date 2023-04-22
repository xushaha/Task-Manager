package hexlet.code.service;

import hexlet.code.dto.LabelDto;
import hexlet.code.model.Label;

import java.util.List;

public interface LabelService {

    List<Label> getAllLabels();

    // POST /api/labels - создание новой метки
    Label createNewLabel(LabelDto labelDto);

    Label getLabelById(Long id);

    Label updateLabel(Long id, LabelDto labelDto);

    void deleteLabelById(Long id);


}
