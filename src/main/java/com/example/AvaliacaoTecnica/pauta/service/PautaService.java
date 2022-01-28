package com.example.AvaliacaoTecnica.pauta.service;

import com.example.AvaliacaoTecnica.pauta.object.Associados;
import com.example.AvaliacaoTecnica.pauta.object.Pauta;
import com.example.AvaliacaoTecnica.pauta.repository.PautaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PautaService {

    private final PautaRepository pautaRepository;

    @Autowired
    public PautaService(PautaRepository pautaRepository) {
        this.pautaRepository = pautaRepository;
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
                    TemporizadordePauta(tempo, pautaObject);
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

    public void ContaVotosParaPautaAberta(Long id){
        Pauta pautaObject= GetPautaPorID(id)
                .orElseThrow(() -> new IllegalStateException("Pauta Inexistente"));

        ArrayList<Associados> listaAssociados= new ArrayList<>();

    }

    public void TemporizadordePauta(int tempo, Pauta pauta){
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        pauta.setStatusPauta("Concluida");
                        pautaRepository.save(pauta);
                    }
                },
                tempo
        );

    }


}
