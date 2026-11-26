package ch.no1hardy.service.front;


public record BaseResponse<T>(
        T data,
        GlobalData globalData
) {
}
