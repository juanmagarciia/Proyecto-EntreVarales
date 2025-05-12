package es.entreVarales.service;


import es.entreVarales.model.Aspirantes;
import es.entreVarales.model.Costalero;
import es.entreVarales.model.CuerpoCapataces;
import es.entreVarales.repository.AspiranteRepository;
import es.entreVarales.repository.CostaleroRepository;
import es.entreVarales.repository.CuerpoCapatacesRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CuerpoCapatacesService {

    @Autowired
    private CuerpoCapatacesRepository cuerpoCapatacesRepository;

    public List<CuerpoCapataces> findAll() {
        return cuerpoCapatacesRepository.findAll();
    }

    public Optional<CuerpoCapataces> findById(String dni) {
        return cuerpoCapatacesRepository.findById(dni);
    }

    public CuerpoCapataces save(CuerpoCapataces cuerpoCapataces) {
        return cuerpoCapatacesRepository.save(cuerpoCapataces);
    }

    public void deleteById(String dni) {
    	cuerpoCapatacesRepository.deleteById(dni);
    }

    public CuerpoCapataces update(String dni, CuerpoCapataces updated) {
        if (cuerpoCapatacesRepository.existsById(dni)) {
            updated.setDniCapataz(dni);
            return cuerpoCapatacesRepository.save(updated);
        }
        throw new RuntimeException("Capataz no encontrado con DNI: " + dni);
    }
}
