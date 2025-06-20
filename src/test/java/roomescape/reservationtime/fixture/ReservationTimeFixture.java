package roomescape.reservationtime.fixture;

import static org.mockito.Mockito.mock;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.IntStream;
import roomescape.reservationtime.domain.ReservationTime;
import roomescape.reservationtime.domain.dto.ReservationTimeRequestDto;
import roomescape.reservationtime.domain.dto.ReservationTimeResponseDto;

public class ReservationTimeFixture {

    public static ReservationTimeRequestDto createRequestDto(LocalTime time) {
        return new ReservationTimeRequestDto(time);
    }

    public static ReservationTime create(LocalTime time) {
        ReservationTimeRequestDto requestDto = createRequestDto(time);
        return requestDto.toEntity();
    }

    public static ReservationTime createMock() {
        return mock(ReservationTime.class);
    }

    public static ReservationTimeResponseDto createResponseDto(ReservationTime reservationTime) {
        return ReservationTimeResponseDto.of(reservationTime);
    }

    public static List<ReservationTime> createMockMultiple(int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> createMock())
                .toList();
    }
}
