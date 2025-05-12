package es.entreVarales.model;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "Costalero")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Costalero {

    @Id
    @Column(nullable = false, length = 9)
    private String dniCostalero;

    @Column(nullable = false)
    private String nombreCostalero;

    @Column(nullable = false)
    private String apellidoCostalero;

    @ManyToOne
    @JoinColumn(name = "id_user", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "id_paso", nullable = false)
    private Paso paso;

    @Column(nullable = false)
    private Integer numTrabajadera;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Altura.TipoAltura tipoAltura;
    
    public String getDniCostalero() {
        return dniCostalero;
    }

    public void setDniCostalero(String dniCostalero) {
        this.dniCostalero = dniCostalero;
    }

	public String getNombreCostalero() {
		return nombreCostalero;
	}

	public void setNombreCostalero(String nombreCostalero) {
		this.nombreCostalero = nombreCostalero;
	}

	public String getApellidoCostalero() {
		return apellidoCostalero;
	}

	public void setApellidoCostalero(String apellidoCostalero) {
		this.apellidoCostalero = apellidoCostalero;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Paso getPaso() {
		return paso;
	}

	public void setPaso(Paso paso) {
		this.paso = paso;
	}

	public Integer getNumTrabajadera() {
		return numTrabajadera;
	}

	public void setNumTrabajadera(Integer numTrabajadera) {
		this.numTrabajadera = numTrabajadera;
	}

	public Altura.TipoAltura getTipoAltura() {
		return tipoAltura;
	}

	public void setTipoAltura(Altura.TipoAltura tipoAltura) {
		this.tipoAltura = tipoAltura;
	}
	
	public Integer getIdPaso() {
	    return paso != null ? paso.getIdPaso() : null;
	}


    

}

