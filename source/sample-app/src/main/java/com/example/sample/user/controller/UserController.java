package com.example.sample.user.controller;

import com.example.sample.global.common.ApiResult;
import com.example.sample.global.common.annotation.CurrentUserId;
import com.example.sample.user.application.UserQueryService;
import com.example.sample.user.application.UserService;
import com.example.sample.user.application.dto.LoginResult;
import com.example.sample.user.application.dto.UserInfoResult;
import com.example.sample.user.controller.dto.LoginRequest;
import com.example.sample.user.controller.dto.LoginResponse;
import com.example.sample.user.controller.dto.PasswordChangeRequest;
import com.example.sample.user.controller.dto.SignUpRequest;
import com.example.sample.user.controller.dto.UpdateUserRequest;
import com.example.sample.user.controller.dto.UserInfoResponse;
import com.example.sample.user.controller.mapper.UserWebMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "User API", description = "회원 가입, 로그인 및 유저 관리")
public class UserController {
    private final UserService userService;
    private final UserQueryService userQueryService;
    private final UserWebMapper userWebMapper;


    /**
     * 회원가입
     *
     * @param request
     * @return
     */
    @Operation(summary = "회원가입", description = "아이디, 비밀번호, 이름을 받아 새로운 사용자를 등록합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원가입 성공"),
            @ApiResponse(responseCode = "400", description = "입력값 검증 실패"),
            @ApiResponse(responseCode = "409", description = "이미 존재하는 아이디")
    })
    @PostMapping("/signup")
    public ApiResult<Void> signUp(@RequestBody @Valid SignUpRequest request) {
        userService.signUp(userWebMapper.toCommand(request));
        return ApiResult.ok();
    }

    /**
     * 로그인
     *
     * @param request
     * @return
     */
    @Operation(summary = "로그인", description = "아이디와 비밀번호로 인증 후 토큰을 발급받습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공 (토큰 발급 완료)"),
            @ApiResponse(responseCode = "400", description = "입력값 형식 오류"),
            @ApiResponse(responseCode = "401", description = "아이디 또는 비밀번호 불일치")
    })
    @PostMapping("/login")
    public ApiResult<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        LoginResult result = userService.login(userWebMapper.toCommand(request));
        return ApiResult.ok(LoginResponse.from(result));
    }

    /**
     * 회원정보 조회
     *
     * @param userId
     * @return
     */
    @Operation(summary = "회원 정보 조회", description = "현재 로그인한 사용자의 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @GetMapping("/info")
    public ApiResult<UserInfoResponse> getUserInfo(@CurrentUserId String userId) {
        UserInfoResult result = userQueryService.getUserInfo(userId);
        return ApiResult.ok(UserInfoResponse.from(result));
    }

    /**
     * 회원정보 수정
     *
     * @param username
     * @param request
     * @return
     */
    @Operation(summary = "회원 정보 수정", description = "현재 로그인한 사용자의 이름(displayName) 또는 이메일(email)을 수정합니다.\\\\n입력하지 않은 필드는 변경되지 않습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "정보 수정 성공"),
            @ApiResponse(responseCode = "400", description = "입력값 검증 실패 (이름 길이, 이메일 형식 등)"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    @PatchMapping("/info")
    public ApiResult<Void> updateInfo(
            @CurrentUserId String username,
            @RequestBody @Valid UpdateUserRequest request) {
        userService.updateInfo(userWebMapper.toCommand(username, request));
        return ApiResult.ok();
    }

    /**
     * 회원탈퇴
     *
     * @param username
     * @return
     */
    @Operation(summary = "회원 탈퇴", description = "현재 로그인한 사용자를 탈퇴(Soft Delete) 처리합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "탈퇴 성공"),
            @ApiResponse(responseCode = "409", description = "이미 탈퇴한 회원")
    })
    @DeleteMapping
    public ApiResult<Void> withdraw(@CurrentUserId String username) {
        userService.withdraw(username);
        return ApiResult.ok();
    }

    /**
     *비밀번호 변경
     * @param username
     * @param request
     * @return
     */
    @Operation(summary = "비밀번호 변경", description = "현재 비밀번호를 확인한 후 새로운 비밀번호로 변경합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "비밀번호 변경 성공"),
            @ApiResponse(responseCode = "401", description = "현재 비밀번호 불일치"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    @PatchMapping("/password")
    public ApiResult<Void> changePassword(
            @CurrentUserId String username,
            @RequestBody @Valid PasswordChangeRequest request) {
        userService.changePassword(userWebMapper.toCommand(username, request));
        return ApiResult.ok();
    }


}