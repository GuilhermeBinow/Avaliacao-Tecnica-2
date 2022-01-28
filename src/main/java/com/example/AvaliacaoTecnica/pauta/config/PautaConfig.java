package com.example.AvaliacaoTecnica.pauta.config;

import com.example.AvaliacaoTecnica.pauta.object.Pauta;
import com.example.AvaliacaoTecnica.pauta.repository.PautaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class PautaConfig {

    @Bean
    CommandLineRunner commandLineRunner(
            PautaRepository repository){
        return args -> {
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

            repository.saveAll(List.of(pautaAberta, pautaFechada,pautaConcluida));
        };

    }
}
