package hexlet.code.service;

import hexlet.code.model.Label;
import hexlet.code.dto.LabelDto;
import hexlet.code.repository.LabelRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
@AllArgsConstructor
public class LabelServiceImpl implements LabelService {

    private final LabelRepository labelRepository;

    // GET /api/labels - получение списка меток
    @Override
    public List<Label> getAllLabels() {
        return labelRepository.findAll();
    }

    // POST /api/labels - создание новой метки
    @Override
    public Label createNewLabel(LabelDto labelDto) {
        final Label label = new Label();
        label.setName(labelDto.getName());
        return labelRepository.save(label);
    }

    // GET /api/labels/{id} - получение метки по идентификатору
    @Override
    public Label getLabelById(Long id) {
        return labelRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("The label with this id does not exist"));
    }

    // PUT /api/labels/{id} - обновление метки
    @Override
    public Label updateLabel(Long id, LabelDto labelDto) {
        final Label labelToUpdate = labelRepository.findById(id).get();
        labelToUpdate.setName(labelDto.getName());
        return labelRepository.save(labelToUpdate);

    }

    // DELETE /api/labels/{id} - удаление метки
    @Override
    public void deleteLabelById(Long id) {
        labelRepository.deleteById(id);

    }
}
