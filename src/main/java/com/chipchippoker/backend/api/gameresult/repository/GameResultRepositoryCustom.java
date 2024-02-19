package com.chipchippoker.backend.api.gameresult.repository;

import java.util.List;

import com.chipchippoker.backend.common.collection.GameResult;

public interface GameResultRepositoryCustom {

	public List<GameResult> findRecentPlayList(String nickname);
}
