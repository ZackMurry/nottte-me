package com.zackmurry.nottteme.jwt;

import com.zackmurry.nottteme.models.auth.AuthenticationRequest;
import com.zackmurry.nottteme.models.auth.AuthenticationResponse;
import com.zackmurry.nottteme.services.NottteUserDetailsService;
import io.jsonwebtoken.MalformedJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    @PostMapping("/authenticate")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) throws Exception {

        if(authenticationRequest.getPassword() == null || authenticationRequest.getPassword().length() > 40) return new ResponseEntity<HttpStatus>(HttpStatus.LENGTH_REQUIRED);

        try {
            //password gets encoded in the authenticate method
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
        } catch (MalformedJwtException e) {
            System.out.println("malformed jwt");
            throw new Exception("jwt malformed", e);
        }
        final UserDetails userDetails = userDetailsService
                .loadUserByUsername(authenticationRequest.getUsername());

        final String jwt = jwtTokenUtil.generateToken(userDetails);
        return ResponseEntity.ok(new AuthenticationResponse(jwt));
    }

}
