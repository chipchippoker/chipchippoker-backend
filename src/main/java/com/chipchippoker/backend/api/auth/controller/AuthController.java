package com.chipchippoker.backend.api.auth.controller;

import java.security.NoSuchAlgorithmException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chipchippoker.backend.api.auth.model.dto.Token;
import com.chipchippoker.backend.api.auth.model.dto.request.AuthorizationCodeRequest;
import com.chipchippoker.backend.api.auth.model.dto.request.DuplicationConfirmIdRequest;
import com.chipchippoker.backend.api.auth.model.dto.request.DuplicationConfirmNicknameRequest;
import com.chipchippoker.backend.api.auth.model.dto.request.LoginRequest;
import com.chipchippoker.backend.api.auth.model.dto.request.SignupRequest;
import com.chipchippoker.backend.api.auth.model.dto.response.AuthorizationCodeAlreadyAccountResponse;
import com.chipchippoker.backend.api.auth.model.dto.response.AuthorizationCodeMoreInformationResponse;
import com.chipchippoker.backend.api.auth.model.dto.response.AuthorizationInformationResponse;
import com.chipchippoker.backend.api.auth.model.dto.response.LoginResponse;
import com.chipchippoker.backend.api.auth.model.dto.response.SignupResponse;
import com.chipchippoker.backend.api.auth.model.dto.response.SimpleLoginResponse;
import com.chipchippoker.backend.api.auth.service.AuthService;
import com.chipchippoker.backend.common.dto.ApiResponse;
import com.chipchippoker.backend.common.dto.ErrorBase;
import com.chipchippoker.backend.common.exception.InvalidException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "Auth API Controller", description = "인증 인가를 처리하는 Auth Controller")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
@Slf4j
public class AuthController {
	private final AuthService authService;

	@PostMapping("/signup")
	public ResponseEntity<ApiResponse<SignupResponse>> signup(@RequestBody @Valid SignupRequest signupRequest) throws
		NoSuchAlgorithmException {
		return ResponseEntity.ok(ApiResponse.success(authService.signup(signupRequest)));
	}

	@PostMapping("/login")
	public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody LoginRequest loginRequest) throws
		NoSuchAlgorithmException {
		return ResponseEntity.ok(ApiResponse.success(authService.login(loginRequest)));
	}

	@PostMapping("/duplication/id")
	@Operation(operationId = "IsDuplicateId", summary = "아이디 중복 조회", description = "사용자 아이디가 중복인지 조회한다.")
	public ResponseEntity<ApiResponse<Boolean>> isDuplicateMemberId(
		@RequestBody DuplicationConfirmIdRequest duplicationConfirmIdRequest) {
		return ResponseEntity.ok(
			ApiResponse.success(authService.isDuplicateMemberId(duplicationConfirmIdRequest.getMemberId())));
	}

	@PostMapping("/duplication/nickname")
	public ResponseEntity<ApiResponse<Boolean>> isDuplicateNickname(
		@RequestBody DuplicationConfirmNicknameRequest duplicationConfirmNicknameRequest) {
		return ResponseEntity.ok(
			ApiResponse.success(authService.isDuplicateNickname(duplicationConfirmNicknameRequest.getNickname())));
	}

	@Operation(summary = "인가코드 전달")
	@PostMapping("/authorization")
	public ResponseEntity<ApiResponse<AuthorizationInformationResponse>> passAuthorizationCode(
		@RequestBody AuthorizationCodeRequest authorizationCodeRequest) {
		String authorizationCode = authorizationCodeRequest.getAuthorizationCode();
		if (authorizationCode == null || authorizationCode.isEmpty()) {
			throw new InvalidException(ErrorBase.E400_INVALID_AUTHORIZATION_CODE);
		}
		Token token = authService.getToken(authorizationCode);
		if (token == null) {
			throw new InvalidException(ErrorBase.E400_INVALID_AUTH_TOKEN);
		} else {
			Long kakaoSocialId = authService.getKakaoSocialId(token.getAccess_token());

			if (authService.validateExistsUser(kakaoSocialId)) {
				SimpleLoginResponse loginResponse = authService.kakaoLogin(token);
				AuthorizationCodeAlreadyAccountResponse authorizationCodeAlreadyAccountResponse = new AuthorizationCodeAlreadyAccountResponse(
					200, "로그인을 완료했습니다.",
					loginResponse.getAccessToken(), loginResponse.getRefreshToken(), loginResponse.getIcon(),
					loginResponse.getNickname());
				return new ResponseEntity<>(ApiResponse.success(authorizationCodeAlreadyAccountResponse),
					HttpStatus.valueOf(200));
			} else {
				AuthorizationCodeMoreInformationResponse authorizationCodeMoreInformationResponse = new AuthorizationCodeMoreInformationResponse(
					202, "추가 정보가 필요합니다.", authService.encryptionToken(token.getAccess_token()));
				return new ResponseEntity<>(ApiResponse.success(authorizationCodeMoreInformationResponse),
					HttpStatus.valueOf(202));
			}
		}
	}
}
