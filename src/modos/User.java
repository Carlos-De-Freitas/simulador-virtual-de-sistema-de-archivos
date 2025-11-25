/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modos;

/**
 *
 * @author 58412
 */
public class User {

    private final String username;
    private final UserMode mode;

    public User(String username, UserMode mode) {
        this.username = username;
        this.mode = mode;
    }

    public String getUsername() {
        return username;
    }

    public UserMode getMode() {
        return mode;
    }

    public boolean isAdmin() {
        return mode == UserMode.ADMIN;
    }
}
