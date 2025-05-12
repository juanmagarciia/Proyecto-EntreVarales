package es.entreVarales.repository;

import es.entreVarales.model.CuerpoCapataces;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CuerpoCapatacesRepository extends JpaRepository<CuerpoCapataces, String> {
}