package com.bit.eduventure.jwt;


import com.bit.eduventure.User.Service.UserDetailsServiceImpl;
import com.bit.eduventure.exception.errorCode.ErrorCode;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.file.AccessDeniedException;

//요청이 왔을 때 헤더에 담긴 JWT Token을 받아서 유효성 검사를 하고
//Token 안에 있는 username을 리턴하기 위한 필터 클래스
//SecurityConfiguration에 filter로 등록돼서 인증이 필요한 요청이 올때마다
//자동실행되도록 설정
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final UserDetailsServiceImpl userDetailsServiceImpl;
    private final JwtTokenProvider jwtTokenProvider;

    private final String REQUEST_NAME = "exception";
    //filter로 등록하면 자동으로 실행될 메소드
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        response.setCharacterEncoding("utf-8");

        try {
            //request에서 token꺼내오기
            //token 값이 있으면 토큰값이 담기고 토큰 값이 없으면 null이 담긴다.
            String token = parseBearerToken(request);
            System.out.println(token);
            System.out.println("여기는 토큰필터의 토큰이다");
            //토큰 검사 및 시큐리티 등록
            if (token != null && !token.equalsIgnoreCase("null")) {
                //유효성 검사 및 username가져오기
                String userId = jwtTokenProvider.validateAndGetUsername(token);

                UserDetails userDetails =
                        userDetailsServiceImpl.loadUserByUsername(userId);

                //유효성 검사 완료된 토큰 시큐리티에 인증된 사용자로 등록
                AbstractAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContext securityContext = SecurityContextHolder.getContext();
                securityContext.setAuthentication(authenticationToken);
                SecurityContextHolder.setContext(securityContext);
            }
            filterChain.doFilter(request, response);
        } catch (UnknownError e) {
            //토큰이 없을 경우
            request.setAttribute(REQUEST_NAME, ErrorCode.UNKNOWN_ERROR.getCode());
        } catch (MalformedJwtException e) {
            //변조된 토큰 에러
            request.setAttribute(REQUEST_NAME, ErrorCode.MALFORMED_JWT.getCode());
        } catch (ExpiredJwtException e) {
            //만료 에러
            request.setAttribute(REQUEST_NAME, ErrorCode.EXPIRED_TOKEN.getCode());
        } catch (AccessDeniedException e) {
            //권한 에러
            request.setAttribute(REQUEST_NAME, ErrorCode.ACCESS_DENIED.getCode());
        } catch (UnsupportedJwtException e) {
            //지원되지 않는 토큰인 경우
            request.setAttribute(REQUEST_NAME, ErrorCode.UNSUPPORTED_TOKEN.getCode());
        }  catch (Exception e) {
            //그 외의 오류들
            request.setAttribute(REQUEST_NAME, ErrorCode.EXCEPTION.getCode());
        }
    }


    private String parseBearerToken(HttpServletRequest request) {
        //넘어오는 토큰의 형태
        /*header: {
                       Authorization: "Bearer 토크값"
                   }
        */
        String bearerToken = request.getHeader("Authorization");

        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            //실제 token의 값만 리턴
            return bearerToken.substring(7);
        } else {
            return null;
        }
    }

}
