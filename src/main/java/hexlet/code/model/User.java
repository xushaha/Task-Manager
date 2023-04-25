package hexlet.code.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static javax.persistence.GenerationType.IDENTITY;
import static javax.persistence.TemporalType.TIMESTAMP;

@Entity
@Data
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Invalid email")
    @Column(name = "email", unique = true)
    private String email;

    @NotBlank(message = "First name cannot be empty")
    @Column(name = "first_name")
    private String firstName;

    @NotBlank(message = "Last name cannot be empty")
    @Column(name = "last_name")
    private String lastName;

    @NotBlank(message = "Password cannot be blank")
    @Size(min = 3, max = 255, message = "The password must be 3 to 255 characters long")
    @JsonIgnore
    @Column(name = "password")
    private String password;

    @CreationTimestamp
    @Temporal(TIMESTAMP)
    @Column(name = "created_at")
    private Date createdAt;

    public User(final Long id) {
        this.id = id;
    }

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY,
            mappedBy = "author")
    private List<Task> listTaskAuthor;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY,
            mappedBy = "executor")
    private List<Task> listTaskExecutor;

}
