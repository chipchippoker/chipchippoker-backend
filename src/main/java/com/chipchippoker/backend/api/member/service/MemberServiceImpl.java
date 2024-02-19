package com.chipchippoker.backend.api.member.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import com.chipchippoker.backend.api.friend.repository.FriendRepository;
import com.chipchippoker.backend.api.friendrequest.repository.FriendRequestRepository;
import com.chipchippoker.backend.api.gameresult.repository.GameResultRepository;
import com.chipchippoker.backend.api.member.dto.model.ProfilePageResponse;
import com.chipchippoker.backend.api.member.dto.model.RecentPlayListResponse;
import com.chipchippoker.backend.api.member.repository.MemberRepository;
import com.chipchippoker.backend.common.collection.GameResult;
import com.chipchippoker.backend.common.dto.ErrorBase;
import com.chipchippoker.backend.common.entity.Friend;
import com.chipchippoker.backend.common.entity.FriendRequest;
import com.chipchippoker.backend.common.entity.Member;
import com.chipchippoker.backend.common.entity.Point;
import com.chipchippoker.backend.common.exception.InvalidException;
import com.chipchippoker.backend.common.exception.NotFoundException;
import com.chipchippoker.backend.common.util.jwt.JwtUtil;
import com.chipchippoker.backend.common.util.jwt.dto.TokenResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MemberServiceImpl implements MemberService {
	private final MemberRepository memberRepository;
	private final GameResultRepository gameResultRepository;
	private final FriendRepository friendRepository;
	private final FriendRequestRepository friendRequestRepository;
	private final JwtUtil jwtUtil;

	@Value("${kakao.app_admin_key}")
	private String kakaoAppAdminKey;

	@Override
	public ProfilePageResponse getProfilePage(Long id, String nickname) {
		Member member = memberRepository.findById(id)
			.orElseThrow(() -> new NotFoundException(ErrorBase.E404_NOT_EXISTS_MEMBER));
		Member searchMember = memberRepository.findByNickname(nickname)
			.orElseThrow(() -> new NotFoundException(ErrorBase.E404_NOT_EXISTS_MEMBER));
		boolean isMine = member.getNickname().equals(nickname);

		//최근 10게임의 대한 정보를 가져오는 리스트
		List<GameResult> list = gameResultRepository.findRecentPlayList(nickname);
		//최근 10개임의 정보를 원하는 응답값으로 매핑하는 메소드
		List<RecentPlayListResponse> recentPlayList = getRecentPlayList(list, nickname);

		Boolean isFriend = Boolean.FALSE;
		Friend friend = friendRepository.findByMemberAIdAndMemberBId(id, searchMember.getId());
		if (friend != null)
			isFriend = Boolean.TRUE;

		// 친구 요청을 보냈고, 대기 중인지 확인
		Boolean isSent = Boolean.FALSE;

		FriendRequest friendRequest = friendRequestRepository.findByFromMemberIdAndToMemberId(member.getId(),
			searchMember.getId());
		if (friendRequest != null && friendRequest.getStatus().equals("대기"))
			isSent = Boolean.TRUE;

		Integer myTotalRank = 0;
		List<Point> totalRankList = memberRepository.getTotalRank(0);
		for (int i = 0; i < totalRankList.size(); i++) {
			Point p = totalRankList.get(i);
			if (p.getMember().getNickname().equals(nickname)) {
				myTotalRank = i + 1;
			}
		}
		Integer myFriendRank = null;
		if (isMine) {
			myFriendRank = 1;
			Integer myPoint = searchMember.getPoint().getPointScore();
			List<Friend> friendRankList = memberRepository.getFriendRank(id, 0);
			for (int i = 0; i < friendRankList.size(); i++) {
				Friend f = friendRankList.get(i);
				if (f.getMemberB().getPoint().getPointScore() > myPoint) {
					myFriendRank++;
				}
			}
		}
		return memberRepository.getProfilePage(searchMember, myTotalRank, myFriendRank, isMine, isFriend, isSent,
			recentPlayList);

	}

	@Override
	public TokenResponse reissueToken(String originToken, Long id) {
		Member member = memberRepository.findById(id)
			.orElseThrow(() -> new NotFoundException(ErrorBase.E404_NOT_EXISTS_MEMBER));
		if (!member.getRefreshToken().equals(originToken)) {
			throw new InvalidException(ErrorBase.E400_INVALID_TOKEN);
		}
		String accessToken = jwtUtil.createAccessToken(member.getId(), member.getNickname());
		String refreshToken = jwtUtil.createRefreshToken(member.getId(), member.getNickname());
		member.updateRefreshToken(refreshToken);
		return new TokenResponse(accessToken, refreshToken);
	}

	private static List<RecentPlayListResponse> getRecentPlayList(List<GameResult> list, String nickname) {
		List<RecentPlayListResponse> recentPlayList = new ArrayList<>();
		for (GameResult gameResult : list) {
			Map<String, String> opponents = new HashMap<>();
			int changePoint = 0;
			for (int i = 0; i < gameResult.getMemberList().size(); i++) {
				if (gameResult.getGameMode().equals("경쟁")) {
					if (gameResult.getMemberList().get(i).equals(nickname)) {
						changePoint = Point.calculatePoint(gameResult.getResultCoinList().get(i));
					}
				}
				int resultCoin = gameResult.getResultCoinList().get(i);
				String outcome = resultCoin > 25 ? "win" : (resultCoin == 25 ? "draw" : "lose");
				opponents.put(gameResult.getMemberList().get(i), outcome);
			}
			RecentPlayListResponse recentPlayListResponse = RecentPlayListResponse.createRecentPlayListResponse(
				opponents, gameResult.getGameMode(), gameResult.getMemberList().size(), changePoint);
			recentPlayList.add(recentPlayListResponse);
		}
		return recentPlayList;
	}

	@Override
	public void logout(Long id) {
		Member member = memberRepository.findById(id)
			.orElseThrow(() -> new NotFoundException(ErrorBase.E404_NOT_EXISTS_MEMBER));
		member.updateRefreshToken(null);
	}

	@Override
	public void withdraw(Long id) {
		Member member = memberRepository.findById(id)
			.orElseThrow(() -> new NotFoundException(ErrorBase.E404_NOT_EXISTS_MEMBER));

		// 카카오 연동한 경우, 연동 해제
		if (member.getKakaoLinkState()) {
			RestClient restClient = RestClient.create();

			MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();
			formData.add("target_id_type", "user_id");
			formData.add("target_id", member.getKakaoSocialId());

			restClient.post()
				.uri("https://kapi.kakao.com/v1/user/unlink")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.header("Authorization", kakaoAppAdminKey)
				.body(formData)
				.retrieve();
		}

		memberRepository.deleteById(id);
	}

	@Override
	public void updateIcon(Long id, String icon) {
		Member member = memberRepository.findById(id)
			.orElseThrow(() -> new NotFoundException(ErrorBase.E404_NOT_EXISTS_MEMBER));
		member.updateIcon(icon);
	}
}
