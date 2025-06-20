package roomescape.global.config.resolver;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import roomescape.theme.domain.Theme;
import roomescape.theme.domain.ThemeParam;
import roomescape.theme.exception.NotFoundThemeException;
import roomescape.theme.repository.ThemeRepository;

@Component
public class ThemeArgumentResolver implements HandlerMethodArgumentResolver {

    private static final String THEME_ID_PARAMETER_FIELD = "themeId";
    private final ThemeRepository themeRepository;

    public ThemeArgumentResolver(ThemeRepository themeRepository) {
        this.themeRepository = themeRepository;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(ThemeParam.class) && parameter.getParameterType().equals(Theme.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, @Nullable ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, @Nullable WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest nativeRequest = (HttpServletRequest) webRequest.getNativeRequest();
        String themeIdParam = nativeRequest.getParameter(THEME_ID_PARAMETER_FIELD);
        if (themeIdParam == null) {
            throw new MissingServletRequestParameterException(THEME_ID_PARAMETER_FIELD, "Long");
        }
        Long id = Long.parseLong(themeIdParam);

        return themeRepository.findById(id)
                .orElseThrow(NotFoundThemeException::new);
    }

    public String getThemeIdParameterField() {
        return THEME_ID_PARAMETER_FIELD;
    }
}
