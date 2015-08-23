package com.andrewusanin.pojo.db;

public class Database extends BaseDatabase {

    private String url;
    private String user;
    private String password;

    private Database() { }

    private Database(final String url, final String user, final String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    public static Database createDatabase(final String url, final String user, final String password) {
        return new Database(url, user, password);
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public String getUser() {
        return user;
    }

    @Override
    public String getPassword() {
        return password;
    }
}
