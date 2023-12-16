package ru.effectivemobile.tms.persistence.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "users")
public class UserEntity {

    public static final String EMAIL_PATTERN = "^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Email(regexp = EMAIL_PATTERN)
    @Column(unique = true, updatable = false)
    private String email;

    @NotBlank
    @JsonIgnore
    private String password;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "owner", orphanRemoval = true)
    private RefreshTokenEntity refreshToken;

}
