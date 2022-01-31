package com.example.AvaliacaoTecnica.integrationtest;

import com.example.AvaliacaoTecnica.pauta.controller.PautaController;
import com.example.AvaliacaoTecnica.pauta.object.Associados;
import com.example.AvaliacaoTecnica.pauta.object.Pauta;
import com.example.AvaliacaoTecnica.pauta.service.PautaService;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@WebMvcTest(PautaController.class)
public class PautaControllerTest {

    @MockBean
    private PautaService pautaService;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private MockMvc mockMvc;

    List<Pauta> pautaList;
    @Before
    void setUp(){
        Pauta pautaFechada= new Pauta(
                3,
                "PautaFechadaTeste",
                "Fechada"


        );
        Pauta pautaAberta= new Pauta(
                4,
                "PautaAbertaTeste",
                "Aberta"

        );
        Pauta pautaConcluida= new Pauta(
                4,
                "PautaConcluidaTeste",
                "Concluida"

        );
        pautaConcluida.setVotoNao(0);
        pautaConcluida.setVotoSim(0);
        pautaList.add(pautaAberta);
        pautaList.add(pautaFechada);
        pautaList.add(pautaConcluida);
    }


    @Test
    void criaMockMVC(){
        assertNotNull(mockMvc);
    }

    @Test
    void getPautas() throws Exception {
        when(pautaService.getListaDePautas()).thenReturn(pautaList);

        boolean result= mockMvc
                        .perform(MockMvcRequestBuilders.get(createURLWithPort("pautas")))
                        .andExpect(MockMvcResultMatchers.status().isOk())
                        .equals(pautaList);


    }

    @Test
    void getPautaX() throws Exception {
        Pauta pautaAberta= new Pauta(
                4,
                "PautaAbertaTeste",
                "Aberta"

        );
        when(pautaService.getPautaPorID(1L)).thenReturn(Optional.of(pautaAberta));


        boolean result= mockMvc
                .perform(MockMvcRequestBuilders.get(createURLWithPort("pauta/")).param("Id","1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .equals(pautaAberta);


    }

    @Test
    void contagemDeVotos() throws Exception {
        List<String> lista= new ArrayList<>();
        lista.add("Votos Sim: 1");
        lista.add("Votos Não: 1");
        when(pautaService.retornaVotosAposFechamentoDaPauta(1L)).thenReturn(lista);

            mockMvc.perform(MockMvcRequestBuilders.post(createURLWithPort("pautas/votos/"))
                    .param("Id", "1"))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .equals(lista);
    }

    @Test
    void cadastrarPauta() throws Exception {
        Pauta pautaFechada= new Pauta(
                2,
                "PautaTeste",
                "Fechada"

        );
        when(pautaService.cadastrarPauta(2,"pautaTeste")).thenReturn(pautaFechada);


        boolean result= mockMvc
                .perform(MockMvcRequestBuilders.post(createURLWithPort("pautas/cadastro/")).param("NumeroAssociados","2")
                        .param("NomePauta", "PautaTeste"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .equals(pautaFechada);


    }

    @Test
    void deletarPauta() throws Exception {
        Pauta pautaFechada= new Pauta(
                2,
                "PautaTeste",
                "Fechada"

        );
        when(pautaService.checaPautaParaDeletar(1L)).thenReturn(true);

        boolean result= mockMvc
                .perform(MockMvcRequestBuilders.delete(createURLWithPort("pautas/excluir/")).param("Id","1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .equals(true);


    }

    @Test
    void abrirPauta() throws Exception {
        Pauta pautaAberta= new Pauta(
                2,
                "PautaTeste",
                "Aberta"

        );
        when(pautaService.aberturaDePautas(1L, 0)).thenReturn(pautaAberta);

        boolean result= mockMvc
                .perform(MockMvcRequestBuilders.put(createURLWithPort("pautas/iniciar/")).param("Id","1")
                        .param("TempoDeVotacao", "0"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .equals(pautaAberta);


    }

    @Test
    void addVotoPauta() throws Exception {
        Pauta pautaAberta= new Pauta(
                2,
                "PautaTeste",
                "Aberta"

        );
        pautaAberta.setVotoSim(1);
        pautaAberta.setVotoNao(1);
        when(pautaService.checaSeVotanteNaoVotouNaPauta(1L,new Associados())).thenReturn(true);

        boolean result= mockMvc
                .perform(MockMvcRequestBuilders.put(createURLWithPort("pautas/votar/")).param("IdPauta","1")
                        .param("IdAssociado", "1")
                        .param("Voto", "Sim"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .equals(true);


    }




    private String createURLWithPort(String uri) {
        return "http://localhost:8080/" + uri;
    }

}
