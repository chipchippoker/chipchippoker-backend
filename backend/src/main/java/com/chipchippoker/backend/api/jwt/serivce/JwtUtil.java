package com.chipchippoker.backend.api.jwt.serivce;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.chipchippoker.backend.api.jwt.model.dto.TokenResponse;
import com.chipchippoker.backend.common.dto.ErrorBase;
import com.chipchippoker.backend.common.exception.InvalidException;
import com.chipchippoker.backend.common.exception.UnAuthorizedException;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class JwtUtil {

	@Value("${jwt.secretKey}")
	private String secretKey;

	@Value("${jwt.access.expiration}")
	private Long accessTokenExpirationPeriod;

	@Value("${jwt.refresh.expiration}")
	private Long refreshTokenExpirationPeriod;

	@Value("${jwt.access.header}")
	private String accessHeader;

	@Value("${jwt.refresh.header}")
	private String refreshHeader;

	/*
	JWT 생성
	@param userNum
	@return String
	 */
	public String createAccessToken(Long id) {
		Date now = new Date();
		return Jwts.builder()
			.setHeaderParam("type", "jwt")
			.claim("id", id)
			.setSubject(accessHeader)
			.setExpiration(new Date(now.getTime() + accessTokenExpirationPeriod))
			.signWith(SignatureAlgorithm.HS256, secretKey) //signature 부분
			.compact();
	}

	public String createRefreshToken(Long id) {
		return Jwts.builder()
			.setSubject(refreshHeader)
			.claim("id", id)
			.setExpiration(new Date(new Date().getTime() + refreshTokenExpirationPeriod))
			.signWith(SignatureAlgorithm.HS256, secretKey)
			.compact(); //signature 부분
	}

	public String getJwt(String header) {
		HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest();
		return request.getHeader(header).split(" ")[1];
	}

	/*
	JWT에서 userIdx 추출
	@return int
	@throws BaseException
	 */
	public Long getId() {
		//1. JWT 추출
		String accessToken = getJwt("access-token");
		System.out.println("accessToken = " + accessToken);
		if (accessToken == null || accessToken.isEmpty()) {
			String refreshToken = getJwt("refresh-token");
			if (isValidateToken(refreshToken)) {
				throw new UnAuthorizedException(ErrorBase.E401_UNAUTHORIZED_ACCESSTOKEN);
			} else {
				throw new InvalidException(ErrorBase.E400_INVALID_TOKEN);
			}

		} else {
			if (isValidateToken(accessToken)) {
				return Jwts.parser()
					.setSigningKey(secretKey)
					.parseClaimsJws(accessToken).getBody().get("id", Long.class);
			} else {
				String refreshToken = getJwt("refresh-token");
				if (isValidateToken(refreshToken)) {
					throw new UnAuthorizedException(ErrorBase.E401_UNAUTHORIZED_ACCESSTOKEN);
				} else {
					throw new InvalidException(ErrorBase.E400_INVALID_TOKEN);
				}
			}
		}
	}

	public TokenResponse reissuanceTokens() {
		String refreshToken = getJwt("refresh-token");
		Long id = Jwts.parser()
			.setSigningKey(secretKey)
			.parseClaimsJws(refreshToken).getBody().get("id", Long.class);
		String newAccessToken = createAccessToken(id);
		String newRefreshToken = createRefreshToken(id);
		return new TokenResponse(newAccessToken, newRefreshToken);
	}

	public boolean isValidateToken(String token) {
		try {
			Jwts.parser()
				.setSigningKey(secretKey)
				.parseClaimsJws(token);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}
