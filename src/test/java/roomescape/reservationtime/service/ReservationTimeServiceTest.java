package roomescape.reservationtime.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.reservationtime.domain.ReservationTime;
import roomescape.reservationtime.domain.dto.ReservationTimeRequestDto;
import roomescape.reservationtime.domain.dto.ReservationTimeResponseDto;
import roomescape.reservationtime.exception.AlreadyReservedTimeException;
import roomescape.reservationtime.exception.DuplicateReservationTimeException;
import roomescape.reservationtime.exception.NotFoundReservationTimeException;
import roomescape.reservationtime.fixture.ReservationTimeFixture;
import roomescape.reservationtime.repository.ReservationTimeRepository;

@ExtendWith(MockitoExtension.class)
class ReservationTimeServiceTest {

    private final ReservationTime reservationTime = new ReservationTime(1L, LocalTime.of(11, 11, 11));

    @Mock
    ReservationTimeRepository mockReservationTimeRepository;
    @Mock
    ReservationRepository mockReservationRepository;
    @InjectMocks
    ReservationTimeService reservationTimeService;

    @Nested
    @DisplayName("저장된 모든 예약 시간 불러오는 기능")
    class findAll {

        @DisplayName("레포지토리에서 반환된 예약 시간 수만큼 결과를 반환한다")
        @ParameterizedTest
        @MethodSource("provideCounts")
        void findAll_success_whenDataExists(int count) {
            // given
            List<ReservationTime> returnValue = ReservationTimeFixture.createMockMultiple(count);

            when(mockReservationTimeRepository.findAll())
                    .thenReturn(returnValue);

            // when & then
            Assertions.assertThat(reservationTimeService.findAll()).hasSize(count);
            verify(mockReservationTimeRepository, times(1)).findAll();
        }

        static Stream<Arguments> provideCounts() {
            return Stream.of(
                    Arguments.of(0),
                    Arguments.of(1),
                    Arguments.of(2),
                    Arguments.of(10)
            );
        }
    }

    @Nested
    @DisplayName("예약 시간 추가 기능")
    class add {

        private final ReservationTimeRequestDto reservationTimeRequestDto = new ReservationTimeRequestDto(LocalTime.of(11, 11, 11));

        @DisplayName("유효한 입력일 시 repository.save() 로직이 실행된다")
        @Test
        void add_success_whenValidInput() {
            // given
            when(mockReservationTimeRepository.save(any(ReservationTime.class)))
                    .thenReturn(reservationTime);

            // when
            ReservationTimeResponseDto responseDto = reservationTimeService.add(reservationTimeRequestDto);

            // then
            Assertions.assertThat(responseDto).isNotNull();
            verify(mockReservationTimeRepository, times(1)).save(any(ReservationTime.class));
        }

        @DisplayName("이미 등록되어 있는 예약 시간으로 추가 요청 시 예외 발생한다")
        @Test
        void add_throwException_byDuplicationReservationTime() {
            // given
            when(mockReservationTimeRepository.save(any()))
                    .thenThrow(new DataIntegrityViolationException(any()));

            // when & then
            Assertions.assertThatThrownBy(
                    () -> reservationTimeService.add(reservationTimeRequestDto)
            ).isInstanceOf(DuplicateReservationTimeException.class);
        }
    }

    @Nested
    @DisplayName("예약 시간 삭제 기능")
    class deleteById {

        @DisplayName("존재하는 id로 요청 시 예약 시간이 삭제된다.")
        @Test
        void deleteById_success_withValidId() {
            // given
            when(mockReservationTimeRepository.findById(any()))
                    .thenReturn(Optional.of(reservationTime));
            doNothing()
                    .when(mockReservationTimeRepository).deleteById(any());
            when(mockReservationRepository.existsByReservationTime(any()))
                    .thenReturn(false);

            // when
            Long id = 1L;
            reservationTimeService.deleteById(id);

            // then
            verify(mockReservationTimeRepository).findById(id);
            verify(mockReservationTimeRepository).deleteById(id);
        }

        @DisplayName("존재하지 않는 id로 요청 시 예외가 발생한다")
        @Test
        void deleteById_throwException_whenIdNotFound() {
            // given
            when(mockReservationTimeRepository.findById(any()))
                    .thenReturn(Optional.empty());

            // when & then
            Long id = 1L;
            Assertions.assertThatThrownBy(
                    () -> reservationTimeService.deleteById(id)
            ).isInstanceOf(NotFoundReservationTimeException.class);

            verify(mockReservationTimeRepository, times(1)).findById(id);
            verify(mockReservationTimeRepository, never()).deleteById(id);
        }

        @DisplayName("예약에서 사용 중인 시간 삭제 시 예외가 발생한다")
        @Test
        void deleteById_throwException_whenUsingInReservation() {
            // given
            when(mockReservationTimeRepository.findById(any()))
                    .thenReturn(Optional.of(reservationTime));
            when(mockReservationRepository.existsByReservationTime(any()))
                    .thenReturn(true);

            // when & then
            Long id = 1L;
            Assertions.assertThatThrownBy(
                    () -> reservationTimeService.deleteById(id)
            ).isInstanceOf(AlreadyReservedTimeException.class);

            verify(mockReservationTimeRepository, times(1)).findById(id);
            verify(mockReservationTimeRepository, never()).deleteById(any());
        }
    }
}
