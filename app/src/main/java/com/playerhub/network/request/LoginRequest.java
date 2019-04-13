package com.playerhub.network.request;

import java.io.Serializable;

/**
 * Created by administrator on 25/03/18.
 */

public class LoginRequest implements Serializable {

    private String email;
    private String password;
    private String secret;

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
