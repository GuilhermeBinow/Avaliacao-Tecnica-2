package com.example.AvaliacaoTecnica;


import com.example.AvaliacaoTecnica.pauta.object.Associados;
import com.example.AvaliacaoTecnica.pauta.object.Pauta;
import com.example.AvaliacaoTecnica.pauta.repository.AssociadosRepository;
import com.example.AvaliacaoTecnica.pauta.repository.PautaRepository;
import com.example.AvaliacaoTecnica.pauta.service.PautaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class PautaServiceTesting {

    @Mock
    private PautaRepository pautaRepository;
    @Mock
    private AssociadosRepository associadosRepository;

    private PautaService underTest;



    @BeforeEach
    void setup(){
        underTest= new PautaService(pautaRepository,associadosRepository);
    }

    @Test
    void getListaDePautasTest(){
        //when
        underTest.getListaDePautas();
        //then
        verify(pautaRepository).findAll();
    }

    @Test
    void getPautaPorIDTest(){
        //when
        underTest.getPautaPorID(1L);
        //then
        verify(pautaRepository).findById(1L);
    }

    @Test
    void cadastrarPautaTest(){
        //given
        Pauta novaPauta= new Pauta(
                1,
                "PautaTeste",
                "Fechada"
        );
        //when
        underTest.cadastrarPauta(1, "PautaTeste");

        //then
        ArgumentCaptor<Pauta> pautaArgumentCaptor=
                ArgumentCaptor.forClass(Pauta.class);
        verify(pautaRepository)
                .save(pautaArgumentCaptor.capture());

        Pauta capturedPauta = pautaArgumentCaptor.getValue();
        assertThat(capturedPauta.getStatusPauta()).isEqualTo(novaPauta.getStatusPauta());
        assertThat(capturedPauta.getNomeDaPauta()).isEqualTo(novaPauta.getNomeDaPauta());
        assertThat(capturedPauta.getNumeroDeAssociados()).isEqualTo(novaPauta.getNumeroDeAssociados());

    }

    @Test
    void checarPautaParaDeletar(){
        //given
        Pauta novaPauta= new Pauta(
                1,
                "PautaTeste",
                "Fechada"
        );
        novaPauta.setId(1L);
        //when
        given(pautaRepository.existsById(1L)).willReturn(true);

        underTest.checaPautaParaDeletar(1L);
        verify(pautaRepository).deleteById(1L);

    }

    @Test
    void aberturaDePautasTest() throws InterruptedException {
        //given
        Pauta novaPauta= new Pauta(
                1,
                "PautaTeste",
                "Fechada"
        );
        novaPauta.setId(1L);
        //when
        given(pautaRepository.findById(1L)).willReturn(Optional.of(novaPauta));

        underTest.aberturaDePautas(1L,0);

        //then

        Thread.sleep(1000);
        verify(pautaRepository, times(2)).save(novaPauta);
    }

    @Test
    void checaSeVotanteNaoVotouNaPautaTest(){
        //given
        Pauta novaPauta= new Pauta(
                2,
                "PautaTeste",
                "Aberta"
        );
        Associados associadoVotandoSim= new Associados(
                1L,
                "Sim"
        );
        Associados associadoVotandoNao= new Associados(
                2L,
                "Nao"
        );
        associadoVotandoSim.setIdPauta(1L);
        associadoVotandoNao.setIdPauta(1L);
        novaPauta.setId(1L);
        novaPauta.setVotoNao(0);
        novaPauta.setVotoSim(0);
        //when
        given(pautaRepository.findById(1L)).willReturn(Optional.of(novaPauta));
        given(associadosRepository.findAll()).willReturn(Collections.emptyList());

        underTest.checaSeVotanteNaoVotouNaPauta(1L,associadoVotandoSim);
        underTest.checaSeVotanteNaoVotouNaPauta(1L,associadoVotandoNao);

        verify(pautaRepository,times(2)).save(any());
        verify(associadosRepository, times(2)).save(any());
    }


    @Test
    void retornaVotosAposFechamentoDaPautaTest(){
        //given
        Pauta novaPauta= new Pauta(
                2,
                "PautaTeste",
                "Concluida"
        );
        novaPauta.setVotoSim(1);
        novaPauta.setVotoNao(1);
        novaPauta.setId(1L);
        //when
        given(pautaRepository.findById(1L)).willReturn(Optional.of(novaPauta));
        List<String> listaTeste= new ArrayList<>();
        listaTeste.add("Votos Sim: 1");
        listaTeste.add("Votos Não: 1");
        List<String> test= underTest.retornaVotosAposFechamentoDaPauta(1L);

        //then
        assertEquals(test,listaTeste);

    }





}
