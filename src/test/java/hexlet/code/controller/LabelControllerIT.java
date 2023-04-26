package hexlet.code.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import hexlet.code.config.SpringConfigForIT;
import hexlet.code.dto.LabelDto;
import hexlet.code.model.Label;
import hexlet.code.repository.LabelRepository;
import hexlet.code.utils.TestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static hexlet.code.config.SpringConfigForIT.TEST_PROFILE;
import static hexlet.code.controller.UserController.ID;
import static hexlet.code.utils.TestUtils.BASE_LABEL_URL;
import static hexlet.code.utils.TestUtils.TEST_EMAIL;
import static hexlet.code.utils.TestUtils.asJson;
import static hexlet.code.utils.TestUtils.fromJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ActiveProfiles(TEST_PROFILE)
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = SpringConfigForIT.class)
public class LabelControllerIT {

    @Autowired
    private LabelRepository labelRepository;
    @Autowired
    private TestUtils utils;

    @BeforeEach
    public void before() throws Exception {
        utils.regDefaultUser();
    }

    @AfterEach
    public void clear() {
        utils.clearDataBase();
    }

    @Test
    public void testCreateNewLabel() throws Exception {
        assertEquals(0, labelRepository.count());
        utils.createLabel("new label").andExpect(status().isCreated());
        assertEquals(1, labelRepository.count());
    }

    @Test
    public void twiceCreateLabelFails() throws Exception {
        utils.createLabel("new label").andExpect(status().isCreated());
        utils.createLabel("new label").andExpect(status().isUnprocessableEntity());
        assertEquals(1, labelRepository.count());
    }

    @Test
    public void testCreateEmptyLabelFails() throws Exception {
        assertEquals(0, labelRepository.count());
        utils.createLabel("");
        assertEquals(0, labelRepository.count());
    }

    @Test
    public void testGetLabelById() throws Exception {
        utils.createLabel("new label");
        final Label expectedLabel = labelRepository.findAll().get(0);

        final var response = utils.perform(
                        get(BASE_LABEL_URL + ID, expectedLabel.getId()),
                        expectedLabel.getName()
                ).andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final Label label = TestUtils.fromJson(response.getContentAsString(), new TypeReference<>() {
        });

        assertEquals(expectedLabel.getId(), label.getId());
        assertEquals(expectedLabel.getName(), label.getName());
    }

    @Test
    public void testGetLabelByIdFails() throws Exception {
        utils.createLabel("new label").andExpect(status().isCreated());
        assertThat(labelRepository.count()).isEqualTo(1);
        Long labelId = labelRepository.findAll().get(0).getId() + 1;

        var request = get(BASE_LABEL_URL + TestUtils.ID, labelId);
        utils.perform(request, TEST_EMAIL)
                .andExpect(status().isNotFound());

        assertEquals(1, labelRepository.count());
    }

    @Test
    public void testGetAllLabels() throws Exception {
        utils.createLabel("urgent");
        utils.createLabel("long term");

        final var response = utils.perform(get(BASE_LABEL_URL), TEST_EMAIL)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final List<Label> labels = fromJson(response
                .getContentAsString(), new TypeReference<>() {
                });

        assertThat(labels).hasSize(2);
    }


    @Test
    public void updateLabel() throws Exception {
        utils.createLabel("new label");
        final Label expectedLabel = labelRepository.findAll().get(0);
        LabelDto newLabelDto = new LabelDto("updated label");

        final var responsePut = utils.perform(
                        put(BASE_LABEL_URL + ID, expectedLabel.getId())
                                .content(asJson(newLabelDto))
                                .contentType(APPLICATION_JSON), TEST_EMAIL)
                .andExpect(status().isOk())
                .andReturn().
                getResponse();

        Label updatedLabel = fromJson(responsePut.getContentAsString(), new TypeReference<>() {
        });

        assertTrue(labelRepository.existsById(updatedLabel.getId()));
        assertThat(updatedLabel.getName()).isEqualTo("updated label");

    }

    @Test
    public void deleteLabel() throws Exception {
        assertThat(labelRepository.count()).isEqualTo(0);
        utils.createLabel("new label");
        assertThat(labelRepository.count()).isEqualTo(1);

        Long labelId = labelRepository.findAll().get(0).getId();

        utils.perform(delete(BASE_LABEL_URL + ID, labelId), TEST_EMAIL)
                .andExpect(status().isOk());
        assertThat(labelRepository.count()).isEqualTo(0);

    }

    @Test
    public void testDeleteLabelFails() throws Exception {
        utils.createLabel("new label").andExpect(status().isCreated());
        assertThat(labelRepository.count()).isEqualTo(1);

        Long labelId = labelRepository.findAll().get(0).getId() + 1;

        utils.perform(delete(BASE_LABEL_URL + ID, labelId), TEST_EMAIL)
                .andExpect(status().isNotFound());

        assertEquals(1, labelRepository.count());
    }

}
