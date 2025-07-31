package mj.kangarecruitmenttask.cryptospreadranking.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mj.kangarecruitmenttask.cryptospreadranking.config.SecurityConfig;
import mj.kangarecruitmenttask.cryptospreadranking.validation.JwtTokenValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthorizationFilterTest {

    private JwtAuthorizationFilter filter;

    private HttpServletRequest request;
    private HttpServletResponse response;
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {
        filter = new JwtAuthorizationFilter();
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        filterChain = mock(FilterChain.class);
        SecurityContextHolder.clearContext();
    }

    @Test
    void whenTokenIsValidThenUserShouldBeAuthenticated() throws IOException, ServletException {
        String token = "Bearer valid.token.value";
        when(request.getHeader("Authorization")).thenReturn(token);

        try (MockedStatic<JwtTokenValidator> mockedStatic = Mockito.mockStatic(JwtTokenValidator.class)) {
            mockedStatic.when(() -> JwtTokenValidator.validateToken(token)).thenReturn(true);

            filter.doFilterInternal(request, response, filterChain);

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            assertThat(authentication).isNotNull();
            assertThat(authentication.getPrincipal()).isEqualTo(SecurityConfig.STANDARD_USER);
            assertThat(authentication.getAuthorities()).isEmpty();

            verify(filterChain).doFilter(request, response);
        }
    }

    @Test
    void whenTokenIsInvalidThenUserShouldBeNotAuthenticated() throws IOException, ServletException {
        String token = "Bearer invalid.token";
        when(request.getHeader("Authorization")).thenReturn(token);

        try (MockedStatic<JwtTokenValidator> mockedStatic = Mockito.mockStatic(JwtTokenValidator.class)) {
            mockedStatic.when(() -> JwtTokenValidator.validateToken(token)).thenReturn(false);

            filter.doFilterInternal(request, response, filterChain);

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            assertThat(authentication).isNull();

            verify(filterChain).doFilter(request, response);
        }
    }

    @Test
    void whenTokenIsMissingThenDoNotAuthenticate() throws IOException, ServletException {
        when(request.getHeader("Authorization")).thenReturn(null);

        filter.doFilterInternal(request, response, filterChain);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertThat(authentication).isNull();

        verify(filterChain).doFilter(request, response);
    }
}
