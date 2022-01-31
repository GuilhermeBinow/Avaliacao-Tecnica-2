package com.example.AvaliacaoTecnica.pauta.object;

import javax.persistence.*;

@Entity
@Table(name = "Associados")
public class Associados {


    private Long idPauta;

    @Id
    private Long idAssociado;

    private String voto;

    public Associados() {
    }

    public Long getIdAssociado() {
        return idAssociado;
    }

    public void setIdAssociado(Long idAssociado) {
        this.idAssociado = idAssociado;
    }

    public Associados(Long idAssociado, String voto) {
        this.idAssociado = idAssociado;
        this.voto = voto;
    }

    public String getVoto() {
        return voto;
    }

    public void setVoto(String voto) {
        this.voto = voto;
    }

    public Long getIdPauta() {
        return idPauta;
    }

    public void setIdPauta(Long idPauta) {
        this.idPauta = idPauta;
    }
}
