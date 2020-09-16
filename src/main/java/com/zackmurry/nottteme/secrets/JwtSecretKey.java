package com.zackmurry.nottteme.secrets;

/**
 * @hidden from git
 * used for JWT encoding
 */
public final class JwtSecretKey {

    private static final String SECRET_KEY = System.getenv("NOTTTE_JWT_SECRET_KEY"); //todo change key for production

    public static String getSecretKey() {
        return SECRET_KEY;
    }

}
