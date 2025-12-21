package com.example.diabetes;

public class SessionManager {
    private static SessionManager instance;
    private User currentUser;

    private SessionManager() {}

    public static synchronized SessionManager getInstance() {
        if (instance == null) instance = new SessionManager();
        return instance;
    }

    public User getCurrentUser() { return currentUser; }

    public void setCurrentUser(User user) { this.currentUser = user; }

    public void clear() { this.currentUser = null; }
}
