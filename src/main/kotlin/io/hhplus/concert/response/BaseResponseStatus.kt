package io.hhplus.concert.response

enum class BaseResponseStatus(
    val isSuccess: Boolean,
    val message: String
) {
    SUCCESS(true, "요청에 성공하였습니다."),
    NOT_FOUND_USER(false, "유저를 찾을 수 없습니다."),
    NOT_FOUND_TOKEN(false, "토큰을 찾을 수 없습니다."),
    NOT_FOUND_CONCERT(false, "콘서트를 찾을 수 없습니다."),
    NOT_FOUND_CONCERT_SCHEDULE(false, "콘서트 스케줄을 찾을 수 없습니다."),
    NOT_FOUND_SEAT(false, "좌석을 찾을 수 없습니다."),
    NOT_VALID_TOKEN(false, "유효한 토큰이 아닙니다."),
    ALREADY_RESERVED(false, "이미 예약했습니다."),
    NOT_FOUND_POINT(false, "유저 포인트를 찾을 수 없습니다."),
    NOT_FOUND_RESERVATION(false, "예약을 찾을 수 없습니다."),
    NOT_HOLDING_SEAT(false, "좌석을 점유하고 있지 않습니다."),
    NOT_ENOUGH_POINT(false, "포인트가 충분하지 않습니다."),
    ALREADY_HOLDING_SEAT(false, "이미 예약이 진행중인 좌석입니다."),
    UNAVAILABLE(false, "사용 불가능합니다.")
}