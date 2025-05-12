package es.entreVarales.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "Altura")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Altura {

    @Id
    @Enumerated(EnumType.STRING)
    private TipoAltura tipoAltura;

    public enum TipoAltura {
        ALTA,
        BAJA
    }
}

