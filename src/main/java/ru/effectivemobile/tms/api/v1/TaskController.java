package ru.effectivemobile.tms.api.v1;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Range;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import ru.effectivemobile.tms.dto.task.TaskCreationDto;
import ru.effectivemobile.tms.dto.task.TaskOutgoingDto;
import ru.effectivemobile.tms.dto.task.TaskPatchDto;

import java.util.List;

@Tag(name = "tasks", description = "REST API for task management")
@RequestMapping("/api/v1/tasks")
@ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content())
public interface TaskController {

    @Operation(summary = "Get task by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "404", description = "Not found, the specified id does not exist", content = @Content())
    })
    @GetMapping("/{id}")
    ResponseEntity<TaskOutgoingDto> showById(@PathVariable Long id);

    @Operation(summary = "Get all available tasks")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "204", description = "No tasks currently", content = @Content())
    })
    @GetMapping
    // Swagger UI treats Pageable as a single request parameter, so I needed to list every Pageable component here
    ResponseEntity<List<TaskOutgoingDto>> showAll(@RequestParam(defaultValue = "priority,desc") @Pattern(regexp = "\\w+,(asc|desc|ASC|DESC)") String sort,
                                                  @RequestParam(defaultValue = "5") @Range(min = 1, max = 50) int size,
                                                  @RequestParam(defaultValue = "0") @Min(0) int page,
                                                  @RequestParam(required = false) Long authorId,
                                                  @RequestParam(required = false) Long executorId);

    @Operation(summary = "Create new task")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created", headers = {@Header(name = "Location", description = "Location of created resource")}),
            @ApiResponse(responseCode = "400", description = "Bad Request, invalid format of the request. See response message for more information"),
    })
    @PostMapping
    ResponseEntity<?> create(@Valid @RequestBody TaskCreationDto task, UriComponentsBuilder ucb, Authentication authentication);

    @Operation(summary = "Patch task by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "No content, successful patch"),
            @ApiResponse(responseCode = "400", description = "Bad Request, invalid format of the request. See response message for more information"),
            @ApiResponse(responseCode = "403", description = "Forbidden, only author and executor are allowed to patch task")
    })
    @PatchMapping("/{id}")
    ResponseEntity<Void> patchById(@PathVariable Long id, @RequestBody TaskPatchDto task, Authentication authentication);

    @Operation(summary = "Delete task by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "No content, successful deletion"),
            @ApiResponse(responseCode = "403", description = "Forbidden, only author is allowed to delete task"),
            @ApiResponse(responseCode = "404", description = "Not found, the specified id does not exist")
    })
    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteById(@PathVariable Long id, Authentication authentication);

}
