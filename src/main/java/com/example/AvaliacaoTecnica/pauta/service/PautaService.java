package com.example.AvaliacaoTecnica.pauta.service;

import com.example.AvaliacaoTecnica.pauta.object.Associados;
import com.example.AvaliacaoTecnica.pauta.object.Pauta;
import com.example.AvaliacaoTecnica.pauta.repository.AssociadosRepository;
import com.example.AvaliacaoTecnica.pauta.repository.PautaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

//TODO trocar invalidstate por erro de http request ou afins

@Service
public class PautaService {

    private Pauta pautaTemporaria;

    private final PautaRepository pautaRepository;

    private final AssociadosRepository associadosRepository;

    @Autowired
    public PautaService(PautaRepository pautaRepository, AssociadosRepository associadosRepository) {
        this.pautaRepository = pautaRepository;
        this.associadosRepository = associadosRepository;
    }

    public List<Pauta> GetListaDePautas(){
        return pautaRepository.findAll();
    }

    public Optional<Pauta> GetPautaPorID(Long id){
        return pautaRepository.findById(id);
    }

    public void CadastrarPauta(int numeroAssociados, String nomePauta){
        Pauta novaPauta= new Pauta(
                numeroAssociados,
                nomePauta,
                "Fechada"

        );
        pautaRepository.save(novaPauta);

    }

    public void ChecaPautaParaDeletar(Long id){

        if (!ChecarPautaExiste(id)){
            throw new IllegalStateException(
                    ("Pauta "+ id + " inexistente")
            );
        }
        else{
            DeletarPauta(id);
        }
    }

    public void DeletarPauta(Long id){
        pautaRepository.deleteById(id);
    }

    public boolean ChecarPautaExiste(Long id){
        return pautaRepository.existsById(id);
    }

    public void AberturaDePautas(Long id, int tempo){

            //verifica se a pauta existe, se está fechada e inicia o timer para fecha-la após o tempo determinado no request

            Pauta pautaObject= GetPautaPorID(id)
                    .orElseThrow(() -> new IllegalStateException("Pauta Inexistente"));


                if (pautaObject.getStatusPauta().equals("Fechada")){
                    pautaObject.setStatusPauta("Aberta");
                    ZeraVotosDePautas(id);
                    pautaRepository.save(pautaObject);
                    TemporizadorParaFecharPauta(tempo, pautaObject.getId());
                }
                else{
                    throw new IllegalStateException("Pauta em andamento ou concluida");
                }
            }



    public void ZeraVotosDePautas(Long id){
        Pauta pautaObject= GetPautaPorID(id)
                .orElseThrow(() -> new IllegalStateException("Pauta Inexistente"));

        pautaObject.setVotoNao(0);
        pautaObject.setVotoSim(0);
    }

    public void ChecaSeVotanteNaoVotouNaPauta(Long id, Associados associadoVotando){

        Pauta pautaObject= GetPautaPorID(id)
                .orElseThrow(() -> new IllegalStateException("Pauta Inexistente"));

        if (associadoVotando.getIdAssociado()>pautaObject.getNumeroDeAssociados())
            throw new IllegalStateException("Numero maximo de votos atingido");

        List<Associados> associadosObject= associadosRepository.findAll();
        boolean existeNaLista= false;

        if(ComparaStatusPauta(pautaObject,"Aberta")){
            for (Associados valor: associadosObject){
                if ((Objects.equals(valor.getIdPauta(), pautaObject.getId()) &&
                        Objects.equals(valor.getIdAssociado(), associadoVotando.getIdAssociado()))){
                            existeNaLista=true;
                }
                if (existeNaLista){
                    throw new IllegalStateException("Votante ja teve voto registrado");
                }
            }
                AssociaContagemDeVotos(pautaObject,associadoVotando);

        }
        else
            throw new IllegalStateException("Pauta indisponivel para voto");
    }

    public void AssociaContagemDeVotos(Pauta pautaObject, Associados associadoVotando){
        if (associadoVotando.getVoto().equalsIgnoreCase("Sim")){
            pautaObject.somaVotoSim();
            pautaTemporaria=pautaObject;
            pautaRepository.save(pautaObject);
            associadoVotando.setIdPauta(pautaObject.getId());
            associadosRepository.save(associadoVotando);
        }
        else if (associadoVotando.getVoto().equalsIgnoreCase("Nao")||
                associadoVotando.getVoto().equalsIgnoreCase("Não")){
                    pautaObject.somaVotoNao();
                    pautaTemporaria=pautaObject;
                    pautaRepository.save(pautaObject);
                    associadoVotando.setIdPauta(pautaObject.getId());
                    associadosRepository.save(associadoVotando);
        }
        else
            throw new IllegalStateException("Voto Invalido");
    }

    public boolean ComparaStatusPauta(Pauta pauta, String status){
        if(pauta.getStatusPauta().equals(status))
            return true;

        else
            return false;
    }

    public void TemporizadorParaFecharPauta(int tempo, Long id){
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        FechaPauta(id);
                    }
                },
                tempo
        );

    }
    public void FechaPauta(Long id){
        pautaTemporaria.setStatusPauta("Concluida");
        pautaRepository.save(pautaTemporaria);
    }

    public List<String> RetornaVotosAposFechamentoDaPauta(Long id){
        Pauta pautaObject= GetPautaPorID(id)
                .orElseThrow(() -> new IllegalStateException("Pauta de Id "+id +" não existe"));;

        if (!ComparaStatusPauta(pautaObject,"Concluida"))
            throw new IllegalStateException("Pauta ainda não foi concluida");

        List<String> listaDeVotos= new ArrayList<>();
        listaDeVotos.add("Votos Sim: " +pautaObject.getVotoSim());
        listaDeVotos.add("Votos Não: " +pautaObject.getVotoNao());
        return listaDeVotos;

    }

}
