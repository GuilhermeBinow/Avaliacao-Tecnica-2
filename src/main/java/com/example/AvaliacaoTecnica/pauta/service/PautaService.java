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
        logger("Get lista de pautas ");
        return pautaRepository.findAll();
    }

    public Optional<Pauta> getPautaPorID(Long id){
        logger("Get Pauta por ID");
        return pautaRepository.findById(id);
    }

    public Pauta cadastrarPauta(int numeroAssociados, String nomePauta){
        logger("Cadastro de Pautas");
        Pauta novaPauta= new Pauta(
                numeroAssociados,
                nomePauta,
                "Fechada"

        );
        pautaRepository.save(novaPauta);

        return novaPauta;
    }

    public boolean checaPautaParaDeletar(Long id){
        logger("Checar Pauta antes de deletar");
        if (!checarPautaExiste(id)){
            throw new IllegalStateException(
                    ("Pauta "+ id + " inexistente")
            );
        }
        else{
            deletarPauta(id);
            return true;
        }
    }

    public void deletarPauta(Long id){
        logger("Deletar Pauta");
        pautaRepository.deleteById(id);
        List<Associados> associadosObject= associadosRepository.findAll();
        for (Associados valor: associadosObject){
            if ((Objects.equals(valor.getIdPauta(), id))){
                associadosRepository.deleteById(valor.getIdAssociado());
            }
        }
    }

    public boolean checarPautaExiste(Long id){
        logger("Checar existencia da Pauta");
        return pautaRepository.existsById(id);
    }

    public Pauta aberturaDePautas(Long id, int tempo){
        logger("Checar se Pauta pode ser aberta");
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
        logger("zerar votos da pauta antes de abrir");
        Pauta pautaObject= getPautaPorID(id)
                .orElseThrow(() -> new IllegalStateException("Pauta Inexistente"));

        pautaObject.setVotoNao(0);
        pautaObject.setVotoSim(0);
    }

    public boolean checaSeVotanteNaoVotouNaPauta(Long id, Associados associadoVotando){
        logger("checar se associado já votou na pauta inserida");
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
                return true;
        }
        else
            throw new IllegalStateException("Pauta indisponivel para voto");
    }

    public void associaContagemDeVotos(Pauta pautaObject, Associados associadoVotando){
        logger("adicionar o voto à pauta");
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
        logger("comparar se status da pauta é X");
        if(pauta.getStatusPauta().equals(status))
            return true;

        else
            return false;
    }

    public void temporizadorParaFecharPauta(int tempo, Long id){
        logger("fechar automaticamente a pauta apos periodo de tempo");
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
        logger("fechar pauta");
        pautaTemporaria.setStatusPauta("Concluida");
        pautaRepository.save(pautaTemporaria);
    }

    public List<String> retornaVotosAposFechamentoDaPauta(Long id){
        logger("retornar votos após pauta ser concluida");
        Pauta pautaObject= getPautaPorID(id)
                .orElseThrow(() -> new IllegalStateException("Pauta de Id "+id +" não existe"));

        if (!comparaStatusPauta(pautaObject,"Concluida"))
            throw new IllegalStateException("Pauta ainda não foi concluida");

        List<String> listaDeVotos= new ArrayList<>();
        listaDeVotos.add("Votos Sim: " +pautaObject.getVotoSim());
        listaDeVotos.add("Votos Não: " +pautaObject.getVotoNao());
        return listaDeVotos;

    }

    public void logger(String chamado){
        System.out.println("-------------------------------------------O Método para "+ chamado+ " foi chamado----------------------------------------");
        return;
    }

}
