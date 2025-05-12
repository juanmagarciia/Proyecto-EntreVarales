package es.entreVarales.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import es.entreVarales.model.Paso;


@Repository
public interface PasoRepository extends JpaRepository<Paso, Long> {
	}

	

