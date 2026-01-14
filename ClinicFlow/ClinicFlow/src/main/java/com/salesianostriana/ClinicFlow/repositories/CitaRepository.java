package com.salesianostriana.ClinicFlow.repositories;

import com.salesianostriana.ClinicFlow.models.Cita;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface CitaRepository extends JpaRepository<Cita, Long> {

    Page<Cita> findByPacienteId(Long id, Pageable pageable);
    Page<Cita> findByEstado(String estado, Pageable pageable);
    Page<Cita> findByFechaYhoraBetween(LocalDateTime desde, LocalDateTime hasta, Pageable pageable);

    boolean existsByProfesionalIdAndFechaYhora(Long profesionalId, LocalDateTime fechaYhora);
    @Query("""
        select c
        from Cita c
        join fetch c.paciente
        where c.profesional.id = :profesionalId
          and c.fechaYhora >= :inicio
          and c.fechaYhora < :fin
        order by c.fechaYhora
    """)
    List<Cita> agendaDiariaProfesional(@Param("profesionalId") Long profesionalId,
                                       @Param("inicio") LocalDateTime inicio,
                                       @Param("fin") LocalDateTime fin);
}
