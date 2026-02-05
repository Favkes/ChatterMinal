package org.network;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class BasicCredentials {

    private String header;

    public BasicCredentials(char[] login, char[] password) {
        encode(login, password);
    }

    public void encode(char[] login, char[] password) {
        String loginString = new String(login);
        String passwordString = new String(password);
        Base64.Encoder base64Encoder = Base64.getEncoder();
        String credentials = loginString + ":" + passwordString;
        byte[] credentialsBytes = credentials.getBytes(StandardCharsets.UTF_8);
        header = base64Encoder.encodeToString(credentialsBytes);
    }

    public String getAuthorizationHeader() {
        return "Basic " + header;
    }


}