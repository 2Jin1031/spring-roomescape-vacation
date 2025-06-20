package roomescape.waiting.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.fixture.ReservationFixture;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.reservationtime.domain.ReservationTime;
import roomescape.reservationtime.fixture.ReservationTimeFixture;
import roomescape.reservationtime.repository.ReservationTimeRepository;
import roomescape.theme.domain.Theme;
import roomescape.theme.repository.ThemeRepository;
import roomescape.user.domain.Role;
import roomescape.user.domain.User;
import roomescape.user.fixture.UserFixture;
import roomescape.user.repository.UserRepository;
import roomescape.waiting.domain.Waiting;
import roomescape.waiting.domain.WaitingWithRank;
import roomescape.waiting.fixture.WaitingFixture;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class WaitingRepositoryTest {

    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private ReservationTimeRepository reservationTimeRepository;
    @Autowired
    private WaitingRepository waitingRepository;
    @Autowired
    private ThemeRepository themeRepository;
    @Autowired
    private UserRepository userRepository;

    private ReservationTime savedReservationTime;
    private Theme savedTheme;
    private User savedUser;
    private LocalDate date = LocalDate.now().plusDays(1);

    @BeforeEach
    void beforeEach() {
        savedReservationTime = reservationTimeRepository.save(ReservationTimeFixture.create(LocalTime.of(14, 14)));
        savedTheme = themeRepository.save(new Theme("name1", "dd", "tt"));
        savedUser = userRepository.save(UserFixture.create(Role.ROLE_MEMBER, "n1", "e1", "p1"));
    }

    private Reservation createReservationDefault() {
        return ReservationFixture.createByBookedStatus(date, savedReservationTime, savedTheme, savedUser);
    }

    @Nested
    @DisplayName("예약 대기 목록과 순위 조회 기능")
    class findWaitingsWithRankByMemberId {

        @DisplayName("회원 ID로 예약 대기 목록과 순위를 조회한다")
        @Test
        void findWaitingsWithRankByMemberId_success_byMemberId() {
            // given
            Reservation reservation = createReservationDefault();
            Reservation savedReservation = reservationRepository.save(reservation);

            Waiting waiting1 = WaitingFixture.createByReservation(savedReservation);
            Waiting waiting2 = WaitingFixture.createByReservation(savedReservation);
            waitingRepository.save(waiting1);
            waitingRepository.save(waiting2);

            // when
            List<WaitingWithRank> waitings = waitingRepository.findWaitingsWithRankByMemberId(savedUser.getId());

            // then
            org.junit.jupiter.api.Assertions.assertAll(
                    () -> Assertions.assertThat(waitings).hasSize(2),
                    () -> Assertions.assertThat(waitings.get(0).rank()).isEqualTo(0L),
                    () -> Assertions.assertThat(waitings.get(1).rank()).isEqualTo(1L),
                    () -> Assertions.assertThat(waitings.get(0).waiting().getMember().getId())
                            .isEqualTo(savedUser.getId())
            );
        }
    }

    @Nested
    @DisplayName("예약 정보로 가장 빠른 대기 조회")
    class FindOneByReservationInfoDescTest {

        @DisplayName("예약 정보(date, timeId, themeId)에 해당하는 Waiting 중 가장 id가 작은 것을 조회한다")
        @Test
        void findOneByReservationInfoDesc_success() {
            // given
            Reservation reservation = createReservationDefault();
            Reservation savedReservation = reservationRepository.save(reservation);

            Waiting waiting1 = waitingRepository.save(WaitingFixture.createByReservation(savedReservation));
            Waiting waiting2 = waitingRepository.save(WaitingFixture.createByReservation(savedReservation));

            // when
            Waiting found = waitingRepository.findOneByReservationInfoDesc(
                    savedReservation.getDate(),
                    savedReservation.getReservationTime().getId(),
                    savedReservation.getTheme().getId()
            ).orElseThrow();

            // then
            Assertions.assertThat(found.getId()).isEqualTo(
                    List.of(waiting1, waiting2).stream()
                            .map(Waiting::getId)
                            .min(Long::compareTo)
                            .orElseThrow()
            );
        }
    }

    @Nested
    @DisplayName("id로 waiting 객체 존재 여부 판단")
    class existsById {

        @DisplayName("존재하는 id로 요청했을 때 true를 반환한다")
        @Test
        void existsById_true_byExistingId() {
            // given
            Reservation reservation = createReservationDefault();
            Reservation savedReservation = reservationRepository.save(reservation);

            Waiting waiting = waitingRepository.save(WaitingFixture.createByReservation(savedReservation));

            // when
            boolean actual = waitingRepository.existsById(waiting.getId());
            // then
            Assertions.assertThat(actual).isTrue();
        }

        @DisplayName("존재하지 않는 id로 요청했을 때 false를 반환한다")
        @Test
        void existsById_false_byNonExistingId() {
            // given

            // when
            boolean actual = waitingRepository.existsById(Long.MAX_VALUE);
            // then
            Assertions.assertThat(actual).isFalse();
        }
    }
}
