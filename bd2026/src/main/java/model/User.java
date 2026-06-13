package model;

public class User {
    private String username;
    private String senha;

    public User(String username, String senha){
        this.username = username;
        this.senha = senha;
    }

    // Set
    public void setUsername(String username){
        this.username = username;
    }
    public void setSenha(String senha){
        this.senha = senha;
    }

    // Get
    public String getUsername(){
        return username;
    }
    public String getSenha(){
        return senha;
    }
}