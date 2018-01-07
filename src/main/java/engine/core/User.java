package engine.core;

import java.util.Random;

public class User {
    /**
     * Attributes
     */
    private String fullName;
    private Credentials credentials;

    /**
     * Constructors
     */
    public User() {
        fullName = "";
        credentials = new Credentials();
    }

    public User(String fullName, Credentials credentials) {
        this.fullName = fullName;
        this.credentials = credentials;
    }

    public User(String login, String password) {
        this.fullName = login + "_" + Integer.toString(10000 + new Random().nextInt(89999));
        this.credentials = new Credentials(login, password);
    }

    public User(String fullName, String login, String password) {
        this.fullName = fullName;
        this.credentials = new Credentials(login, password);
    }

    /**
     * Getters
     */
    public String getFullName() {
        return fullName;
    }

    public Credentials getCredentials() {
        return credentials;
    }

    /**
     * Setters
     */
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setCredentials(Credentials credentials) {
        this.credentials = credentials;
    }

    /**
     * Overrides
     */
    @Override
    public String toString() {
        return fullName + " [" + credentials.getLogin() + ", " + credentials.getPassword() + "] ";
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (this.getClass() != object.getClass()) return false;

        User user = (User) object;
        return user.getCredentials().equals(credentials) && user.getFullName().equals(fullName);
    }
}
