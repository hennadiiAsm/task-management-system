package ru.effectivemobile.tms.api.v1;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.effectivemobile.tms.dto.AuthRequestDto;
import ru.effectivemobile.tms.dto.JwtResponseDto;
import ru.effectivemobile.tms.dto.RefreshTokenRequestDto;

@Tag(name = "authentication", description = "Authentication using provided credentials")
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Authenticated, response body contains JWT and refresh token"),
        @ApiResponse(responseCode = "400", description = "Bad Request, invalid format of the request. See response message for more information"),
        @ApiResponse(responseCode = "401", description = "Wrong credentials")
})
public interface AuthController {

    @Operation(summary = "Authentication with email and password")
    @PostMapping("/api/v1/login")
    JwtResponseDto login(@Valid @RequestBody AuthRequestDto authRequestDto);

    @Operation(summary = "Authentication with refresh token")
    @PostMapping("/api/v1/refresh")
    JwtResponseDto refreshToken(@Valid @RequestBody RefreshTokenRequestDto dto);

}
