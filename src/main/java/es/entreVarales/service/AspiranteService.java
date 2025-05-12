package es.entreVarales.service;


import es.entreVarales.model.Aspirantes;
import es.entreVarales.model.Costalero;
import es.entreVarales.repository.AspiranteRepository;
import es.entreVarales.repository.CostaleroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AspiranteService {

    @Autowired
    private AspiranteRepository aspiranteRepository;

    public List<Aspirantes> findAll() {
        return aspiranteRepository.findAll();
    }

    public Optional<Aspirantes> findById(String dni) {
        return aspiranteRepository.findById(dni);
    }

    public Aspirantes save(Aspirantes costalero) {
        return aspiranteRepository.save(costalero);
    }

    public void deleteById(String dni) {
    	aspiranteRepository.deleteById(dni);
    }

    public Aspirantes update(String dni, Aspirantes updated) {
        if (aspiranteRepository.existsById(dni)) {
            updated.setDniAspirante(dni);
            return aspiranteRepository.save(updated);
        }
        throw new RuntimeException("Aspirante no encontrado con DNI: " + dni);
    }
}
