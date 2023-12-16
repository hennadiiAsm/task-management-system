package ru.effectivemobile.tms.dto.task;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

public record TaskCreationDto(@Length(min = 2, max = 50) String title,
                              @Length(min = 2, max = 500) String description,
                              @Range(min = 1, max = 10) int priority,
                              long executorId) {

}
