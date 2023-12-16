package ru.effectivemobile.tms.api.v1;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.util.UriComponentsBuilder;
import ru.effectivemobile.tms.dto.comment.CommentCreationDto;

@Tag(name = "comments", description = "Comments management")
@RequestMapping("/api/v1/tasks/{taskId}/comments")
public interface CommentController {

    @Operation(summary = "Create new comment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created", headers = {@Header(name = "Location", description = "Location of created resource")}),
            @ApiResponse(responseCode = "400", description = "Bad Request, invalid format of the request. See response message for more information"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping
    ResponseEntity<?> create(@PathVariable Long taskId, @Valid CommentCreationDto comment, UriComponentsBuilder ucb, Authentication authentication);

    @Operation(summary = "Delete comment by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "No content, successful deletion"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden, only author is allowed to delete comment"),
            @ApiResponse(responseCode = "404", description = "Not found, the specified id does not exist")
    })
    @DeleteMapping("/{commentId}")
    ResponseEntity<Void> deleteById(@PathVariable String taskId,@PathVariable Long commentId, Authentication authentication);

}
