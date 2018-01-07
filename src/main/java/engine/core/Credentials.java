package engine.core;

import java.io.Serializable;

public class Credentials implements Serializable{
    /**
     * Attributes
     */
    private String login;
    private String password;

    /**
     * Constructors
     */
    public Credentials() {
        login = "";
        password = "";
    }

    public Credentials(String login, String password) {
        this.login = login;
        this.password = password;
    }

    /**
     * Getters
     */
    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    /**
     * Setters
     */
    public void setLogin(String login) {
        this.login = login;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Overrides
     */
    @Override
    public String toString() {
        return "[" + login + ", " + password + "] ";
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (this.getClass() != object.getClass()) return false;

        Credentials credentials = (Credentials) object;
        return credentials.getLogin().equals(login) && credentials.getPassword().equals(password);
    }
}
