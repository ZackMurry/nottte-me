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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @CrossOrigin
    @PostMapping("/test")
    public String testResponse(@RequestBody AuthenticationRequest authenticationRequest) {
        return authenticationRequest.getUsername() + "; " + authenticationRequest.getPassword();
    }

    @CrossOrigin
    @PostMapping("/authenticate")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) throws Exception {
        try {
            //encoding password because it's already encoded in the database
            String encodedPassword = encoder.encode(authenticationRequest.getPassword());

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword())
            );
        } catch (BadCredentialsException e) {
            System.out.println("bad credentials");
            throw new Exception("Incorrect username or password", e);
        } catch (AuthenticationException e) {
            System.out.println("authentication exception");
            throw new Exception("Bad authentication attempt", e);
        } catch (IllegalArgumentException e) {
            System.out.println("password cannot be null");
            throw new Exception("password cannot be null", e);
        }
        final UserDetails userDetails = userDetailsService
                .loadUserByUsername(authenticationRequest.getUsername());

        final String jwt = jwtTokenUtil.generateToken(userDetails);
        return ResponseEntity.ok(new AuthenticationResponse(jwt));
    }

}
