package com.zackmurry.nottteme;

import com.zackmurry.nottteme.dao.user.UserDao;
import com.zackmurry.nottteme.entities.User;
import com.zackmurry.nottteme.jwt.JwtUtil;
import com.zackmurry.nottteme.secrets.JwtSecretKey;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.xml.bind.DatatypeConverter;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
public class UserDataAccessServiceTest {

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtTokenUtil;

    private String testUsername;
    private String testPassword;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @BeforeAll
    public void createTestUser() {
        testUsername = RandomStringUtils.randomAlphanumeric(12);

        //create a new test user if this user already exists
        if(userDao.accountExists(testUsername)) {
            createTestUser();
        } else {
            testPassword = RandomStringUtils.randomAlphanumeric(12);
            assertTrue(userDao.createUserAccount(testUsername, encoder.encode(testPassword)));
        }

    }

    @AfterAll
    public void deleteTestUser() {
        assertEquals(HttpStatus.OK, userDao.deleteAccount(testUsername));
    }

    @DisplayName("Test account exists")
    @Test
    public void accountCreationTest() {
        assertTrue(userDao.accountExists(testUsername));
    }

    @DisplayName("Test get account")
    @Test
    public void getAccountTest() {
        Optional<User> optionalTestAccount = userDao.getUserByUsername(testUsername);
        assertFalse(optionalTestAccount.isEmpty());

        User testAccount = optionalTestAccount.get();
        assertEquals(testUsername, testAccount.getUsername());
        assertNotEquals(testPassword, testAccount.getPassword(), "Password should be encoded.");
    }

    @DisplayName("Test authentication")
    @Test
    public void testAuthentication() {
        assertTrue(authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(testUsername, testPassword)).isAuthenticated(), "User should be authenticated if they enter the correct username and password.");
    }

    @DisplayName("Test jwt")
    @Test
    public void testJwt() {
        UserDetails userDetails = userDetailsService.loadUserByUsername(testUsername);
        String jwt = jwtTokenUtil.generateToken(userDetails);
        assertTrue(jwt.length() > 10, "JWT should be sufficiently long.");
        assertTrue(jwtTokenUtil.validateToken(jwt, userDetails), "JWT should be valid.");
        assertEquals(testUsername, jwtTokenUtil.extractUsername(jwt), "JWT name should match username.");
        String alteredJwt = jwt.substring(0, jwt.length()-1) + (jwt.endsWith("k") ? "a" : "k");
        assertThrows(SignatureException.class, () -> Jwts.parser().setSigningKey(DatatypeConverter.parseBase64Binary(JwtSecretKey.getSecretKey())).parseClaimsJws(alteredJwt), "JWT should be rejected if the signature doesn't match.");
        assertThrows(SignatureException.class, () -> Jwts.parser().setSigningKey(DatatypeConverter.parseBase64Binary(JwtSecretKey.getSecretKey() + "ADDED_VALUE")).parseClaimsJws(jwt), "JWT should be rejected if private key is changed.");
        assertDoesNotThrow(() -> Jwts.parser().setSigningKey(DatatypeConverter.parseBase64Binary(JwtSecretKey.getSecretKey())).parseClaimsJws(jwt), "JWT should not be rejected if it is not altered.");
    }
    
    @DisplayName("Purposely fail test")
    @Test
    public void testFail() {
        assertFalse(true);   
    }
    
}
