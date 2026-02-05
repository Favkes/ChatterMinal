package org.network.model;

public class CreateAccountRequestDto {
    public String Username;
    public String Password;

    public CreateAccountRequestDto(char[] Username, char[] password) {
        this.Username = new String(Username);
        this.Password = new String(password);
    }


}