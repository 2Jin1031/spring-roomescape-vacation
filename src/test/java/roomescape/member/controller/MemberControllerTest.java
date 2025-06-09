package roomescape.member.controller;

import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import roomescape.admin.domain.dto.AdminReservationRequestDto;
import roomescape.global.auth.fixture.AuthFixture;
import roomescape.global.auth.domain.dto.TokenResponseDto;
import roomescape.global.auth.service.AuthService;
import roomescape.reservationtime.ReservationTimeTestDataConfig;
import roomescape.theme.ThemeTestDataConfig;
import roomescape.user.MemberTestDataConfig;
import roomescape.user.domain.User;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes =
        {MemberTestDataConfig.class, ThemeTestDataConfig.class, ReservationTimeTestDataConfig.class,})
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
class MemberControllerTest {

    @LocalServerPort
    private int port;


    @Autowired
    private ReservationTimeTestDataConfig reservationTimeTestDataConfig;
    @Autowired
    private ThemeTestDataConfig themeTestDataConfig;

    private static LocalDate date;
    private static User memberStatic;
    private static TokenResponseDto memberTokenResponseDto;

    @BeforeAll
    public static void setUp(@Autowired AuthService authService,
                             @Autowired MemberTestDataConfig memberTestDataConfig
    ) {
        date = LocalDate.now().plusDays(1);

        memberStatic = memberTestDataConfig.getSavedUser();

        memberTokenResponseDto = authService.login(
                AuthFixture.createTokenRequestDto(memberStatic.getEmail(), memberStatic.getPassword()));
    }

    @BeforeEach
    void configureRestAssured() {
        RestAssured.port = port;
    }

    @DisplayName("유저의 예약 리스트 조회 기능 : 성공 시 200 OK 반환")
    @Test
    void findAllReservationsByMember_success_byMember() {
        // given
        Long themeId = themeTestDataConfig.getSavedId();
        Long reservationTimeId = reservationTimeTestDataConfig.getSavedId();

        AdminReservationRequestDto dto = new AdminReservationRequestDto(date,
                themeId,
                reservationTimeId,
                memberStatic.getId());

        String token = memberTokenResponseDto.accessToken();

        // when
        // then
        RestAssured.given().log().all()
                .cookies("token", token)
                .contentType(ContentType.JSON)
                .body(dto)
                .when().get("/members/reservations-mine")
                .then().log().all()
                .statusCode(HttpStatus.OK.value());
    }
}
