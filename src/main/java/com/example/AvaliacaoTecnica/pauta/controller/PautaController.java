package com.example.AvaliacaoTecnica.pauta.controller;

import com.example.AvaliacaoTecnica.pauta.object.Associados;
import com.example.AvaliacaoTecnica.pauta.object.Pauta;
import com.example.AvaliacaoTecnica.pauta.service.PautaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController

public class PautaController {

    private final PautaService pautaService;

    @Autowired
    public PautaController(PautaService pautaService) {
        this.pautaService = pautaService;
    }

    @GetMapping
    public String BoasVindas()
    {
        return "Olá,\nPor favor, utilize a Collection para navegar pelas funções do programa";
    }

    @GetMapping(path = "pautas")
    public List<Pauta> ChecarPautas(){
        return pautaService.GetListaDePautas();
    }

    @GetMapping
    @RequestMapping("pauta/")
    public Optional<Pauta> ChecarPauta(@RequestParam("Id")Long id){
        return pautaService.GetPautaPorID(id);
    }

    @PostMapping
    @RequestMapping("pautas/cadastro/")
    public String CadastrarPauta(@RequestParam("NumeroAssociados")Integer numeroAssociados,
                                 @RequestParam("NomePauta") String nomePauta){
        pautaService.CadastrarPauta(numeroAssociados,nomePauta);
        return ("Pauta "+nomePauta+ " criada com sucesso.");
    }

    @PutMapping
    @RequestMapping("/pautas/iniciar/")
    public String AbrePauta(@RequestParam("Id") Long id,
                              @RequestParam(value = "TempoDeVotacao", required = false) Integer tempo) {

        if(tempo==null)
            tempo=60000;
        else
            tempo= tempo*60000;

        pautaService.AberturaDePautas(id, tempo);

        return "Pauta Aberta com sucesso";
    }

    @PutMapping
    @RequestMapping("/pautas/votar/")
    public String AdicionaVotos(@RequestParam("IdPauta") Long idPauta,
                              @RequestParam("IdAssociado") Long idAssociado,
                              @RequestParam("Voto") String voto){

        Associados associados= new Associados(idAssociado,voto);
        pautaService.ChecaSeVotanteNaoVotouNaPauta(idPauta,associados);

        return "Voto Adicionado";
    }

    @DeleteMapping
    @RequestMapping("/pautas/excluir")
    public String ExcluiPautas(@RequestParam("Id") Long id){
        pautaService.ChecaPautaParaDeletar(id);
        return ("Pauta "+ id + " deletada com sucesso");
    }

    @GetMapping
    @RequestMapping("/pautas/votos/")
    public List<String> ContabilizarVotos(@RequestParam("Id") Long id){
        return pautaService.RetornaVotosAposFechamentoDaPauta(id);
    }



}

