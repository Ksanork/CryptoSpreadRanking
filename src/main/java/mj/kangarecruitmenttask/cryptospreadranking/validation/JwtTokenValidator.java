package mj.kangarecruitmenttask.cryptospreadranking.validation;

public class JwtTokenValidator {

    public static boolean validateToken(String token) {
        // could be extended to parse JWT token
        return "Bearer ABC123".equals(token);
    }
}
