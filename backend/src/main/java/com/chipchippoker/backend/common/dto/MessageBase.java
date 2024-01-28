package com.chipchippoker.backend.common.dto;

import static org.springframework.http.HttpStatus.*;

import org.springframework.http.HttpStatusCode;

import lombok.Getter;

@Getter
public enum MessageBase {
	/**
	 * 200 OK (성공한 요청)
	 */
	S200(OK, false, "MS000", "성공"),
	S200_GAME_ROOM_CREATED(OK, false, "MS001", "게임방이 생성되었습니다."),
	S200_GAME_ROOM_NEW_MEMBER_ENTER(OK, false, "MS002", "새로운 멤버가 게임방에 입장했습니다."),
	S200_GAME_ROOM_MEMBER_EXIT(OK, false, "MS003", "회원이 게임방에서 나갔습니다."),
	S200_GAME_ROOM_BAN_MEMBER(OK, false, "MS004", "방장이 게임방에서 회원을 강퇴했습니다."),
	S200_GAME_ROOM_BANED_MEMBER(OK, false, "MS005", "방장이 나를 강퇴했습니다."),
	S200_GAME_ROOM_MEMBER_READY(OK, false, "MS006", "회원이 준비를 마쳤습니다."),
	S200_GAME_ROOM_IN_PLAY_INFO(OK, false, "MS007", "진행중인 게임의 메시지입니다."),
	S200_GAME_ROOM_ROUND_END(OK, false, "MS008", "라운드가 종료되었습니다."),

	/**
	 * 400 Bad Request (잘못된 요청)
	 */
	E400_CAN_NOT_BAN(BAD_REQUEST, false, "ME001", "방장만 강제퇴장을 요청할 수 있습니다."),
	E400_CAN_NOT_START_NOT_READY(BAD_REQUEST, false, "ME002", "모두 준비상태가 아닙니다."),
	E400_CAN_NOT_START_NOT_ROOM_MANAGER(BAD_REQUEST, false, "ME003", "방장이 아니라 시작할 수 없습니다."),
	E400_CAN_NOT_BET_ROUND_MISMATCH(BAD_REQUEST, false, "ME004", "베팅 라운드가 잘못되었습니다."),
	E400_CAN_NOT_BET_TURN_MISMATCH(BAD_REQUEST, false, "ME005", "해당 멤버의 베팅 턴이 아닙니다."),
	E400_CAN_NOT_BET_BET_COIN_MISMATCH(BAD_REQUEST, false, "ME006", "베팅이 불가능 한 코인 개수입니다."),
	;

	private final HttpStatusCode statusCode;
	private final boolean sendNotification;
	private final String code;
	private final String message;

	MessageBase(HttpStatusCode statusCode, boolean sendNotification, String code, String message) {
		this.statusCode = statusCode;
		this.sendNotification = sendNotification;
		this.code = code;
		this.message = message;
	}

	public int getStatus() {
		return statusCode.value();
	}
}