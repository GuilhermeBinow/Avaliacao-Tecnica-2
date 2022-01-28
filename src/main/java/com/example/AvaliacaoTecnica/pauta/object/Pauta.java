package com.example.AvaliacaoTecnica.pauta.object;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;

@Entity
@Table(name = "Pauta_info")
public class Pauta {

    @Id
    @SequenceGenerator(name = "idSeq", sequenceName = "idSeq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idSeq")

    private Long id;

    private Integer numeroDeAssociados;
    private String nomeDaPauta;

    //Referencia status da pauta, Pauta aberta significa janela de votacao aberta, pauta fechada significa que foi criada mas nao foi aberta janela de votacao, concluida significa que já foi votado
    private String StatusPauta;
    private Integer votoSim;
    private Integer votoNao;

//TODO implementar setters para votoSim e votoNao para somar 1 ao valor atual


    //Construtores, getters and setters
    public Pauta(Integer numeroDeAssociados, String nomeDaPauta, String statusPauta) {
        this.numeroDeAssociados = numeroDeAssociados;
        this.nomeDaPauta = nomeDaPauta;
        StatusPauta = statusPauta;
    }

    public Pauta(Integer numeroDeAssociados, String nomeDaPauta) {
        this.numeroDeAssociados = numeroDeAssociados;
        this.nomeDaPauta = nomeDaPauta;
    }

    public Pauta() {
    }

    public Integer getVotoSim() {
        return votoSim;
    }

    public Integer getVotoNao() {
        return votoNao;
    }

    public void setVotoSim(Integer votoSim) {
        this.votoSim = votoSim;
    }

    public void setVotoNao(Integer votoNao) {
        this.votoNao = votoNao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getNumeroDeAssociados() {
        return numeroDeAssociados;
    }

    public void setNumeroDeAssociados(Integer numeroDeAssociados) {
        this.numeroDeAssociados = numeroDeAssociados;
    }

    public String getNomeDaPauta() {
        return nomeDaPauta;
    }

    public void setNomeDaPauta(String nomeDaPauta) {
        this.nomeDaPauta = nomeDaPauta;
    }

    public String getStatusPauta() {
        return StatusPauta;
    }

    public void setStatusPauta(String statusPauta) {
        StatusPauta = statusPauta;
    }

}
