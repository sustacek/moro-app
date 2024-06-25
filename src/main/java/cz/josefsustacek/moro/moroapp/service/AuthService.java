package cz.josefsustacek.moro.moroapp.service;

public interface AuthService {
    /**
     * Returns the ID of the user if username + password match, otherwise
     * throws an exception
     * @param username
     * @param password
     * @return
     */
    long verifyLoginCredentials(String username, String password);
}
