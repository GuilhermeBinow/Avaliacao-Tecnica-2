package com.example.AvaliacaoTecnica.pauta.repository;

import com.example.AvaliacaoTecnica.pauta.object.Pauta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PautaRepository extends JpaRepository<Pauta, Long> {


}
