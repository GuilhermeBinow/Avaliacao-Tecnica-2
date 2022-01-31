package com.example.AvaliacaoTecnica.pauta.repository;


import com.example.AvaliacaoTecnica.pauta.object.Associados;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssociadosRepository extends JpaRepository<Associados, Long> {

}
