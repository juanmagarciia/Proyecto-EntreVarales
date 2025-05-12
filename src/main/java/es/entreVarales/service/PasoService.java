package es.entreVarales.service;
import es.entreVarales.model.Paso;
import es.entreVarales.repository.PasoRepository; // Suponiendo que tienes un repositorio para Paso
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PasoService {

    @Autowired
    private PasoRepository pasoRepository;

    public List<Paso> findAll() {
        return pasoRepository.findAll(); // Devuelve la lista completa de pasos
    }
}
