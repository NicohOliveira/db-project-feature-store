package model;

public class Dataset {
    private int id;
    private String nome;
    private String username_criador;

    public Dataset(int id, String nome, String username_criador){
        this.id = id;
        this.nome = nome;
        this.username_criador = username_criador;
    }

    // Set
    public void setId(int id){
        this.id = id;
    }
    public void setNome(String nome){
        this.nome = nome;
    }
    public void setUsernameCriador(String username_criador){
        this.username_criador = username_criador;
    }

    // Get
    public int getId(){
        return id;
    }
    public String getNome(){
        return nome;
    }
    public String getUsernameCriador(){
        return username_criador;
    }
}