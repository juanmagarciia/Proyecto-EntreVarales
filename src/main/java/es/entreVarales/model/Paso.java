package es.entreVarales.model;



import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "Paso")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Paso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idPaso;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoPaso tipoPaso;

    @Column(nullable = false)
    private String nombreTitular;

    @Column(nullable = false)
    private Integer numTrabajaderaTotal;

    public enum TipoPaso {
        CRISTO,
        VIRGEN
    }

	public Integer getIdPaso() {
		return idPaso;
	}

	public void setIdPaso(Integer idPaso) {
		this.idPaso = idPaso;
	}

	public TipoPaso getTipoPaso() {
		return tipoPaso;
	}

	public void setTipoPaso(TipoPaso tipoPaso) {
		this.tipoPaso = tipoPaso;
	}

	public String getNombreTitular() {
		return nombreTitular;
	}

	public void setNombreTitular(String nombreTitular) {
		this.nombreTitular = nombreTitular;
	}

	public Integer getNumTrabajaderaTotal() {
		return numTrabajaderaTotal;
	}

	public void setNumTrabajaderaTotal(Integer numTrabajaderaTotal) {
		this.numTrabajaderaTotal = numTrabajaderaTotal;
	}
    
    
}
