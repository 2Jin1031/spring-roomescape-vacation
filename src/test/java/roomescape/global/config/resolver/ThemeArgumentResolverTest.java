package roomescape.global.config.resolver;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;
import roomescape.reservationtime.domain.ReservationTime;
import roomescape.theme.domain.Theme;
import roomescape.theme.domain.ThemeParam;
import roomescape.theme.exception.NotFoundThemeException;
import roomescape.theme.fixture.ThemeFixture;
import roomescape.theme.repository.ThemeRepository;

@ExtendWith(MockitoExtension.class)
class ThemeArgumentResolverTest {

    @Mock
    NativeWebRequest mockWebRequest;
    @Mock
    HttpServletRequest mockRequest;
    @Mock
    ThemeRepository mockThemeRepository;

    @InjectMocks
    ThemeArgumentResolver themeArgumentResolver;

    private String parameterField;

    private void dummyMethod(@ThemeParam Theme theme, Theme noAnnotation, @ThemeParam ReservationTime reservationTime) {
    }

    private Method method;

    @BeforeEach
    void setUp() throws Exception {
        method = ThemeArgumentResolverTest.class.getDeclaredMethod("dummyMethod", Theme.class, Theme.class,
                ReservationTime.class);
        parameterField = themeArgumentResolver.getThemeIdParameterField();
    }

    @Nested
    @DisplayName("MethodParameter의 상태 판별")
    class supportsParameter {

        @DisplayName("@ThemeParam이면서 Theme 객체이면 true를 반환한다")
        @Test
        void supportsParameter_true_byExistsThemeParam() throws Exception {
            // given
            MethodParameter paramWithAnnotation = new MethodParameter(method, 0);

            // when
            boolean result = themeArgumentResolver.supportsParameter(paramWithAnnotation);

            // then
            assertThat(result).isTrue();
        }

        @DisplayName("@ThemeParam이 없으면 false를 반환한다")
        @Test
        void supportsParameter_false_byNoThemeParam() throws Exception {
            // given
            MethodParameter paramWithoutAnnotation = new MethodParameter(method, 1);

            // when
            boolean result = themeArgumentResolver.supportsParameter(paramWithoutAnnotation);

            // then
            assertThat(result).isFalse();
        }

        @DisplayName("@ThemeParam 인데 Theme 객체가 아닐 경우 false를 반환한다")
        @Test
        void supportsParameter_false_byNoThemeClass() {
            // given
            MethodParameter paramWithoutAnnotation = new MethodParameter(method, 2);

            // when
            boolean result = themeArgumentResolver.supportsParameter(paramWithoutAnnotation);

            // then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("파라미터에서 themeId를 받아 Theme 객체 반환")
    class resolveArgument {

        @DisplayName("존재하는 themeId일 경우 Theme 객체를 반환한다")
        @Test
        void resolveArgument_success_byValidThemeId() throws Exception {
            // given
            MethodParameter parameter = new MethodParameter(method, 0);

            Long id = 1L;
            when(mockRequest.getParameter(parameterField))
                    .thenReturn(id.toString());
            when(mockWebRequest.getNativeRequest())
                    .thenReturn(mockRequest);
            Theme theme = ThemeFixture.create("name1", "description1", "thumbnail1");
            when(mockThemeRepository.findById(id))
                    .thenReturn(Optional.of(theme));

            // when
            Object result = themeArgumentResolver.resolveArgument(parameter, null, mockWebRequest, null);

            // then
            assertThat(result).isEqualTo(theme);
        }

        @DisplayName("존재하지 않는 themeId일 경우 예외가 발생한다: NotFoundThemeException.class")
        @Test
        void resolveArgument_throwsException_byNonExistsThemeId() {
            // given
            MethodParameter parameter = new MethodParameter(method, 0);

            Long id = 999L;
            when(mockRequest.getParameter(parameterField))
                    .thenReturn(id.toString());
            when(mockWebRequest.getNativeRequest())
                    .thenReturn(mockRequest);
            when(mockThemeRepository.findById(id))
                    .thenReturn(Optional.empty());

            // when & then
            Assertions.assertThatThrownBy(
                    () -> themeArgumentResolver.resolveArgument(parameter, null, mockWebRequest, null)
            ).isInstanceOf(NotFoundThemeException.class);
        }
    }
}
