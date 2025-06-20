package roomescape.reservationtime.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Stream;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import roomescape.global.exception.NotFoundException;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.dto.ReservationRequestDto;
import roomescape.reservation.domain.dto.ReservationResponseDto;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.reservation.service.ReservationService;
import roomescape.reservationtime.ReservationTimeTestDataConfig;
import roomescape.reservationtime.domain.ReservationTime;
import roomescape.reservationtime.domain.dto.ReservationTimeRequestDto;
import roomescape.reservationtime.domain.dto.ReservationTimeResponseDto;
import roomescape.reservationtime.exception.AlreadyReservedTimeException;
import roomescape.reservationtime.exception.DuplicateReservationTimeException;
import roomescape.reservationtime.fixture.ReservationTimeFixture;
import roomescape.reservationtime.repository.ReservationTimeRepository;
import roomescape.theme.domain.Theme;
import roomescape.theme.repository.ThemeRepository;
import roomescape.user.domain.Role;
import roomescape.user.domain.User;
import roomescape.user.fixture.UserFixture;
import roomescape.user.repository.UserRepository;

@SpringBootTest(webEnvironment = WebEnvironment.NONE,
        classes = {ReservationTimeTestDataConfig.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ReservationTimeServiceTest {

    @Autowired
    private ReservationTimeService service;
    @Autowired
    private ReservationService reservationService;
    @Autowired
    private ThemeRepository themeRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private ReservationTimeTestDataConfig testDataConfig;
    @Autowired
    private UserRepository userRepository;

    @Mock
    ReservationTimeRepository mockReservationTimeRepository;
    @Mock
    ReservationRepository mockReservationRepository;
    @Mock
    ThemeRepository mockThemeRepository;
    @InjectMocks
    ReservationTimeService reservationTimeService;
    @Autowired
    private ReservationTimeRepository reservationTimeRepository;


    private void deleteByIdAll() {
        jdbcTemplate.update("delete from reservation_time");
    }

    @Nested
    @DisplayName("저장된 모든 예약 시간 불러오는 기능")
    class findAll {

        @DisplayName("레포지토리에서 반환된 예약 시간 수만큼 결과를 반환한다")
        @ParameterizedTest
        @MethodSource("provideCounts")
        void findAll_success_whenDataExists2(int count) {
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

        private final ReservationTime reservationTime = new ReservationTime(1L, LocalTime.of(11, 11, 11));
        private final ReservationTimeRequestDto reservationTimeRequestDto = new ReservationTimeRequestDto(LocalTime.of(11, 11, 11));

        @DisplayName("유효한 입력일 시 repository.save() 로직이 실행된다")
        @Test
        void add_success_whenValidInput1() {
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
                    .thenThrow(new DataIntegrityViolationException(")"));

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
            service.deleteById(testDataConfig.getSavedId());

            // when
            List<ReservationTimeResponseDto> resDtos = service.findAll();

            // then
            Assertions.assertThat(resDtos).hasSize(0);
        }

        @DisplayName("존재하지 않는 id로 요청 시 예외가 발생한다")
        @Test
        void deleteById_throwException_whenIdNotFound() {
            // given
            // when
            // then
            Assertions.assertThatCode(
                    () -> service.deleteById(Long.MAX_VALUE)
            ).isInstanceOf(NotFoundException.class);
        }

        @DisplayName("예약에서 사용 중인 시간 삭제 시 예외가 발생한다")
        @Test
        void deleteById_throwException_whenUsingInReservation() {
            // given
            Theme theme = themeRepository.save(new Theme("name1", "dd", "tt"));
            User savedUser = userRepository.save(UserFixture.create(Role.ROLE_MEMBER, "n1", "e1", "p1"));

            reservationService.add(
                    new ReservationRequestDto(
                            LocalDate.now().plusMonths(3),
                            testDataConfig.getSavedId(),
                            theme.getId()
                    ), savedUser);

            // when, then
            Assertions.assertThatCode(
                    () -> service.deleteById(testDataConfig.getSavedId())
            ).isInstanceOf(AlreadyReservedTimeException.class);
        }
    }
}
