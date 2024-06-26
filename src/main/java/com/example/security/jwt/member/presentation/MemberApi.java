package com.example.security.jwt.member.presentation;

import com.example.security.jwt.member.application.dto.RegisterMemberFacadeRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "Member", description = "Member API")
@ApiResponses(value = {
        @ApiResponse(
                responseCode = "400",
                description = "BAD_REQUEST",
                content = @Content
        ),
        @ApiResponse(
                responseCode = "500",
                description = "INTERNAL_SERVER_ERROR",
                content = @Content
        ),
})
public interface MemberApi {

    @Operation(tags = {"Member"}, summary = "멤버 생성", description = """
            ## API 설명
            멤버를 생성합니다.
            """)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "NO_CONTENT"),
    })
    ResponseEntity<Void> signup(RegisterMemberFacadeRequestDto requestDto);
}
