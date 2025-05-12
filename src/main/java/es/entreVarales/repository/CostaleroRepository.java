package es.entreVarales.repository;

import es.entreVarales.model.Altura;
import es.entreVarales.model.Costalero;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CostaleroRepository extends JpaRepository<Costalero, String> {
	    List<Costalero> findByPaso_IdPaso(Integer idPaso);
	    List<Costalero> findByPaso_IdPasoAndTipoAltura(Integer idPaso, Altura.TipoAltura tipoAltura);
	    List<Costalero> findByTipoAltura(Altura.TipoAltura tipoAltura);

}