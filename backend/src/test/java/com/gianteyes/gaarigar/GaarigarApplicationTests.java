package com.gianteyes.gaarigar;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.gianteyes.gaarigar.security.CustomAuthenticationFilter;
import com.gianteyes.gaarigar.user.*;
import org.junit.jupiter.api.*;
import org.springframework.mock.web.*;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
class GaarigarApplicationTests {
    final String secret="test-only-key-at-least-thirty-two-characters";
    UserService users=mock(UserService.class);
    @AfterEach void clear(){SecurityContextHolder.clearContext();}
    @Test void rejectsLegacyHardcodedSigningKey() throws Exception {
        var request=new MockHttpServletRequest();request.addHeader("Authorization","Bearer "+token("secret",true));
        var response=new MockHttpServletResponse();new CustomAuthenticationFilter(secret,users).doFilter(request,response,new MockFilterChain());assertEquals(401,response.getStatus());
    }
    @Test void refreshTokenCannotBeUsedAsAccessToken() throws Exception {
        var user=new UserModel();user.setPhone("+199955501001");user.setUserType(UserType.CUSTOMER);when(users.getUserByPhone(user.getPhone())).thenReturn(Optional.of(user));
        var request=new MockHttpServletRequest();request.addHeader("Authorization","Bearer "+token(secret,false));var response=new MockHttpServletResponse();
        new CustomAuthenticationFilter(secret,users).doFilter(request,response,new MockFilterChain());assertEquals(401,response.getStatus());
    }
    @Test void roleComesFromDatabaseAndInactiveUsersAreRejected() throws Exception {
        var user=new UserModel();user.setPhone("+199955501001");user.setUserType(UserType.CUSTOMER);when(users.getUserByPhone(user.getPhone())).thenReturn(Optional.of(user));
        var request=new MockHttpServletRequest();request.addHeader("Authorization","Bearer "+token(secret,true));var response=new MockHttpServletResponse();
        new CustomAuthenticationFilter(secret,users).doFilter(request,response,new MockFilterChain());assertEquals("CUSTOMER",SecurityContextHolder.getContext().getAuthentication().getAuthorities().iterator().next().getAuthority());
        user.setIsActive(false);response=new MockHttpServletResponse();new CustomAuthenticationFilter(secret,users).doFilter(request,response,new MockFilterChain());assertEquals(401,response.getStatus());
    }
    String token(String key,boolean role){var b=JWT.create().withSubject("+199955501001").withIssuer("auth0").withExpiresAt(new Date(System.currentTimeMillis()+60000));if(role)b.withClaim("role","ADMIN");return b.sign(Algorithm.HMAC256(key));}
}
