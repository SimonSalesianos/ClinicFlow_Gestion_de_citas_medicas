package com.salesianostriana.ClinicFlow.repositories;

import com.salesianostriana.ClinicFlow.models.Consulta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConsultaRepository extends JpaRepository<Consulta, Long> {

    Optional<Consulta> findByCitaId(Long citaId);

}
