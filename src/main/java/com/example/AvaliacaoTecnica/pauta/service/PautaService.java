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

    public List<Pauta> getListaDePautas(){
        return pautaRepository.findAll();
    }

    public Optional<Pauta> getPautaPorID(Long id){
        return pautaRepository.findById(id);
    }

    public Pauta cadastrarPauta(int numeroAssociados, String nomePauta){
        Pauta novaPauta= new Pauta(
                numeroAssociados,
                nomePauta,
                "Fechada"

        );
        pautaRepository.save(novaPauta);

        return novaPauta;
    }

    public void checaPautaParaDeletar(Long id){

        if (!checarPautaExiste(id)){
            throw new IllegalStateException(
                    ("Pauta "+ id + " inexistente")
            );
        }
        else{
            deletarPauta(id);
        }
    }

    public void deletarPauta(Long id){
        pautaRepository.deleteById(id);
    }

    public boolean checarPautaExiste(Long id){
        return pautaRepository.existsById(id);
    }

    public Pauta aberturaDePautas(Long id, int tempo){

            //verifica se a pauta existe, se está fechada e inicia o timer para fecha-la após o tempo determinado no request

            Pauta pautaObject= getPautaPorID(id)
                    .orElseThrow(() -> new IllegalStateException("Pauta Inexistente"));


                if (pautaObject.getStatusPauta().equals("Fechada")){
                    pautaObject.setStatusPauta("Aberta");
                    zeraVotosDePautas(id);
                    pautaRepository.save(pautaObject);
                    pautaTemporaria=pautaObject;
                    temporizadorParaFecharPauta(tempo, pautaObject.getId());
                }
                else{
                    throw new IllegalStateException("Pauta em andamento ou concluida");
                }
                return pautaObject;
            }



    public void zeraVotosDePautas(Long id){
        Pauta pautaObject= getPautaPorID(id)
                .orElseThrow(() -> new IllegalStateException("Pauta Inexistente"));

        pautaObject.setVotoNao(0);
        pautaObject.setVotoSim(0);
    }

    public void checaSeVotanteNaoVotouNaPauta(Long id, Associados associadoVotando){

        Pauta pautaObject= getPautaPorID(id)
                .orElseThrow(() -> new IllegalStateException("Pauta Inexistente"));

        if (associadoVotando.getIdAssociado()>pautaObject.getNumeroDeAssociados())
            throw new IllegalStateException("Numero maximo de votos atingido");

        List<Associados> associadosObject= associadosRepository.findAll();
        boolean existeNaLista= false;

        if(comparaStatusPauta(pautaObject,"Aberta")){
            for (Associados valor: associadosObject){
                if ((Objects.equals(valor.getIdPauta(), pautaObject.getId()) &&
                        Objects.equals(valor.getIdAssociado(), associadoVotando.getIdAssociado()))){
                            existeNaLista=true;
                }
                if (existeNaLista){
                    throw new IllegalStateException("Votante ja teve voto registrado");
                }
            }
                associaContagemDeVotos(pautaObject,associadoVotando);

        }
        else
            throw new IllegalStateException("Pauta indisponivel para voto");
    }

    public void associaContagemDeVotos(Pauta pautaObject, Associados associadoVotando){
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

    public boolean comparaStatusPauta(Pauta pauta, String status){
        if(pauta.getStatusPauta().equals(status))
            return true;

        else
            return false;
    }

    public void temporizadorParaFecharPauta(int tempo, Long id){
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        fechaPauta(id);
                    }
                },
                tempo
        );

    }
    public void fechaPauta(Long id){
        pautaTemporaria.setStatusPauta("Concluida");
        pautaRepository.save(pautaTemporaria);
    }

    public List<String> retornaVotosAposFechamentoDaPauta(Long id){
        Pauta pautaObject= getPautaPorID(id)
                .orElseThrow(() -> new IllegalStateException("Pauta de Id "+id +" não existe"));

        if (!comparaStatusPauta(pautaObject,"Concluida"))
            throw new IllegalStateException("Pauta ainda não foi concluida");

        List<String> listaDeVotos= new ArrayList<>();
        listaDeVotos.add("Votos Sim: " +pautaObject.getVotoSim());
        listaDeVotos.add("Votos Não: " +pautaObject.getVotoNao());
        return listaDeVotos;

    }

}
