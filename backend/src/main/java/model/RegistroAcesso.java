package model;

import java.sql.Date;
import java.sql.Time;

public class RegistroAcesso {
    private int idAcesso;
    private Date dataAcesso;
    private Time horaAcesso;
    private String tipoAcao;
    private String usernameAutor;
    private int idDatasetAcessada;
    private int numVersaoAcessada;

    public RegistroAcesso(Date dataAcesso, Time horaAcesso, String tipoAcao, String usernameAutor, int idDatasetAcessada, int numVersaoAcessada) {
        this.dataAcesso = dataAcesso;
        this.horaAcesso = horaAcesso;
        this.tipoAcao = tipoAcao;
        this.usernameAutor = usernameAutor;
        this.idDatasetAcessada = idDatasetAcessada;
        this.numVersaoAcessada = numVersaoAcessada;
    }

    // Set
    public void setIdAcesso(int idAcesso) {
        this.idAcesso = idAcesso;
    }
    public void setDataAcesso(Date dataAcesso) {
        this.dataAcesso = dataAcesso;
    }
    public void setHoraAcesso(Time horaAcesso) {
        this.horaAcesso = horaAcesso;
    }
    public void setTipoAcao(String tipoAcao) {
        this.tipoAcao = tipoAcao;
    }
    public void setUsernameAutor(String usernameAutor) {
        this.usernameAutor = usernameAutor;
    }
    public void setIdDatasetAcessada(int idDatasetAcessada) {
        this.idDatasetAcessada = idDatasetAcessada;
    }
    public void setNumVersaoAcessada(int numVersaoAcessada) {
        this.numVersaoAcessada = numVersaoAcessada;
    }

    // Get
    public int getIdAcesso() {
        return idAcesso;
    }
    public Date getDataAcesso() {
        return dataAcesso;
    }
    public Time getHoraAcesso() {
        return horaAcesso;
    }
    public String getTipoAcao() {
        return tipoAcao;
    }
    public String getUsernameAutor() {
        return usernameAutor;
    }
    public int getIdDatasetAcessada() {
        return idDatasetAcessada;
    }
    public int getNumVersaoAcessada() {
        return numVersaoAcessada;
    }
}