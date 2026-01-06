package com.salmontaker.sniffy.auth.controller;

import com.salmontaker.sniffy.auth.dto.request.LoginRequest;
import com.salmontaker.sniffy.auth.exception.UnauthenticatedException;
import com.salmontaker.sniffy.auth.service.AuthService;
import com.salmontaker.sniffy.user.dto.response.UserResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.CookieSerializer.CookieValue;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.List;

@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final CookieSerializer cookieSerializer;

    @PostMapping("/login")
    public void login(@Valid @RequestBody LoginRequest request,
                      HttpServletRequest httpRequest) {
        UserResponse user = authService.login(request);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user.getId(), null, List.of(new SimpleGrantedAuthority("ROLE_USER")));

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);

        SecurityContextHolder.setContext(context);

        HttpSession session = httpRequest.getSession(true);
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);

        if (request.getRememberMe()) {
            session.setMaxInactiveInterval((int) Duration.ofDays(7).toSeconds());
            httpRequest.setAttribute("REMEMBER", true);
        }
    }

    @PostMapping("logout")
    public void logout(HttpServletRequest httpRequest,
                       HttpServletResponse httpResponse) {
        HttpSession session = httpRequest.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        cookieSerializer.writeCookieValue(new CookieValue(httpRequest, httpResponse, ""));

        SecurityContextHolder.clearContext();
    }

    @GetMapping("status")
    public void status(HttpServletRequest httpRequest) {
        HttpSession session = httpRequest.getSession(false);
        if (session == null) {
            throw new UnauthenticatedException();
        }

        SecurityContext context = (SecurityContext)
                session.getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);

        if (context == null || context.getAuthentication() == null || !context.getAuthentication().isAuthenticated()) {
            throw new UnauthenticatedException();
        }
    }
}
