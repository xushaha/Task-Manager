package hexlet.code.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.validation.constraints.NotBlank;
import java.util.Date;
import java.util.Set;

import static javax.persistence.GenerationType.IDENTITY;
import static javax.persistence.TemporalType.TIMESTAMP;

@Entity
@Data
@Table(name = "labels")
@NoArgsConstructor
@AllArgsConstructor
public class Label {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @NotBlank(message = "Label name cannot be empty")
    @Column(name = "name", unique = true)
    private String name;

    @CreationTimestamp
    @Temporal(TIMESTAMP)
    @Column(name = "created_at")
    private Date createdAt;

    @JsonIgnore
    @ManyToMany(mappedBy = "labels")
    private Set<Task> tasks;

}
