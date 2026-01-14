package com.salesianostriana.ClinicFlow.repositories;

import com.salesianostriana.ClinicFlow.models.Consulta;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConsultaRepository extends JpaRepository<Consulta, Long> {
}
