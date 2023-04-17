package hexlet.code.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;

import static javax.persistence.GenerationType.IDENTITY;
import static javax.persistence.TemporalType.TIMESTAMP;

@Entity
@Getter
@Setter
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Email should be valid")
    @Column(name = "email", unique = true)
    private String email;

    @NotBlank(message = "First name cannot be empty")
    @Column(name = "first_Name")
    private String firstName;

    @NotBlank(message = "Last name cannot be empty")
    @Column(name = "last_Name")
    private String lastName;

    @NotBlank(message = "Password cannot be empty")
    @Size(min = 3, max = 500, message = "Password must be between 3 and 500 characters")
    @JsonIgnore
    @Column(name = "password")
    private String password;

    @CreationTimestamp
    @Temporal(TIMESTAMP)
    @Column(name = "created_At")
    private Date createdAt;

    public User(final Long id) {
        this.id = id;
    }

}