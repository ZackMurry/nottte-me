package com.zackmurry.nottteme.jwt;

import com.zackmurry.nottteme.models.AuthenticationRequest;
import com.zackmurry.nottteme.models.AuthenticationResponse;
import com.zackmurry.nottteme.services.NottteUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

/**
 * rest controller for authentication
 */
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/v1/jwt")
@RestController
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private NottteUserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtTokenUtil;

    @CrossOrigin
    @PostMapping("/test")
    public String testResponse(@RequestBody AuthenticationRequest authenticationRequest) {
        return authenticationRequest.getUsername() + "; " + authenticationRequest.getPassword();
    }

    @CrossOrigin
    @PostMapping("/authenticate")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) throws Exception {
        System.out.println(authenticationRequest.getUsername());
        try {
            System.out.println("one");
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword())
            );
            System.out.println("try");
        } catch (BadCredentialsException e) {
            System.out.println("bad credentials");
            throw new Exception("Incorrect username or password", e);
        } catch (AuthenticationException e) {
            System.out.println("authentication exception");
            throw new Exception("Bad authentication attempt", e);
        }
        final UserDetails userDetails = userDetailsService
                .loadUserByUsername(authenticationRequest.getUsername());

        final String jwt = jwtTokenUtil.generateToken(userDetails);
        System.out.println(jwt);
        return ResponseEntity.ok(new AuthenticationResponse(jwt));
    }

}
