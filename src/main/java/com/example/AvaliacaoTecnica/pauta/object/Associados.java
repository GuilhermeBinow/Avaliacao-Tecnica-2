package com.example.AvaliacaoTecnica.pauta.object;

import javax.persistence.*;

public class Associados {

    private Long id;

    private String voto;

    public Associados() {
    }

    public Associados(Long id, String voto) {
        this.id = id;
        this.voto = voto;
    }

    public String getVoto() {
        return voto;
    }

    public void setVoto(String voto) {
        this.voto = voto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
