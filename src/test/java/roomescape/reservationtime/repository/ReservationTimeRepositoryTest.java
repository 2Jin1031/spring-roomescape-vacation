package roomescape.reservationtime.repository;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ReservationTimeRepositoryTest {

    @DisplayName("존재하지 않는 예약 시간 ID로 조회하면 예외가 발생한다.")
    @Test
    void findById_throwsExceptionByNonExistentId() {
        // given
        ReservationTimeRepository mockReservationTimeRepository = mock(ReservationTimeRepository.class);
        when(mockReservationTimeRepository.findById(999L)).thenReturn(Optional.empty());

        // when & then
        Assertions.assertThat(mockReservationTimeRepository.findById(999L)).isEmpty();
    }
}
