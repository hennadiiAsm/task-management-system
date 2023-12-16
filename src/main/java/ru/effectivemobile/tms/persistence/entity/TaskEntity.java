package ru.effectivemobile.tms.persistence.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import java.util.List;

@Entity
@Data
@Table(name = "tasks")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TaskEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Length(min = 2, max = 50)
    private String title;

    @Length(min = 2, max = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Range(min = 1, max = 10)
    private int priority;

    @ManyToOne
    private UserEntity author;

    @ManyToOne
    @JoinColumn(name = "executor_id", foreignKey = @ForeignKey(foreignKeyDefinition = """
                      FOREIGN KEY(executor_id)
                      REFERENCES users(id)
                      ON DELETE SET NULL
            """))
    private UserEntity executor;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "task", orphanRemoval = true)
    private List<CommentEntity> comments;

    public enum Status {
        PENDING,
        PROCESSING,
        FINISHED
    }
}
