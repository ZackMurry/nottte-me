package com.zackmurry.nottteme.filters;

import com.zackmurry.nottteme.jwt.JwtUtil;
import com.zackmurry.nottteme.services.NottteUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 *
 * @from https://www.youtube.com/watch?v=X80nJ5T7YpE
 *
 * a filter for jwt configuration
 *
 * once per request filter means that this runs once per request (crazy how it be like that)
 */
@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private NottteUserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * adds an additional filter before Spring Security returns a 403
     * compares the 'Authorization' header and allows authorization to a page if it is valid
     *
     * @param request incoming http request
     * @param response
     * @param chain
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        final String authorizationHeader = request.getHeader("Authorization"); //gets header from request with the name of "Authorization"

        String username = null;
        String jwt = null;

        //if authorization header is valid
        if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7); //substring(7) skips "Bearer "
            username = jwtUtil.extractUsername(jwt);
        }

        if(username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            if(jwtUtil.validateToken(jwt, userDetails)) {

                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                usernamePasswordAuthenticationToken
                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }

        }
        chain.doFilter(request, response);
    }

}