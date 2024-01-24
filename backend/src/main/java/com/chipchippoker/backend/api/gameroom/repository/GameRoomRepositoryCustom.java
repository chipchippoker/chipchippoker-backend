package com.chipchippoker.backend.api.gameroom.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.chipchippoker.backend.common.entity.GameRoom;

public interface GameRoomRepositoryCustom {
	Page<GameRoom> findBySearchOption(String type, String title, Boolean isTwo, Boolean isThree, Boolean isFour,
		Boolean isEmpty, Pageable pageable);

	List<GameRoom> findByStartFriendlyMatchingSearchOption(Integer totalParticipantCnt);
}
