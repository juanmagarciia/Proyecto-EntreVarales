package es.entreVarales.service;


import es.entreVarales.model.Altura;
import es.entreVarales.model.Costalero;
import es.entreVarales.repository.CostaleroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CostaleroService {

    @Autowired
    private CostaleroRepository costaleroRepository;

    public List<Costalero> findAll() {
        return costaleroRepository.findAll();
    }

    public Optional<Costalero> findById(String dni) {
        return costaleroRepository.findById(dni);
    }

    public Costalero save(Costalero costalero) {
        return costaleroRepository.save(costalero);
    }

    public void deleteById(String dni) {
        costaleroRepository.deleteById(dni);
    }

    public Costalero update(String dni, Costalero updated) {
        if (costaleroRepository.existsById(dni)) {
            updated.setDniCostalero(dni);
            return costaleroRepository.save(updated);
        }
        throw new RuntimeException("Costalero no encontrado con DNI: " + dni);
    }
    
    public List<Costalero> findByPaso(Integer idPaso) {
        return costaleroRepository.findByPaso_IdPaso(idPaso);
    }

	public List<Costalero> findByTipoAltura(Altura.TipoAltura tipoAltura) {  
	    return costaleroRepository.findByTipoAltura(tipoAltura);
	}
	
	public List<Costalero> findByPasoAndTipoAltura(Integer idPaso, Altura.TipoAltura tipoAltura) {
	    return costaleroRepository.findByPaso_IdPasoAndTipoAltura(idPaso, tipoAltura);
	}
	
}
