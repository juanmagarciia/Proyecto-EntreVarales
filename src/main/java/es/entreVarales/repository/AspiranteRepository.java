package es.entreVarales.repository;

import es.entreVarales.model.Aspirantes;
import es.entreVarales.model.Costalero;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AspiranteRepository extends JpaRepository<Aspirantes, String> {
}