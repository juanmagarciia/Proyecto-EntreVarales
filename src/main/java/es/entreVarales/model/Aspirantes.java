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
@Table(name = "Aspirantes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Aspirantes {

    @Id
    @Column(nullable = false, length = 9)
    private String dniAspirante;

    @Column(nullable = false)
    private String nombreAspirante;

    @Column(nullable = false)
    private String apellidoAspirante;

    @Column(nullable = false)
    private String telefonoAspirante;

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

	public String getDniAspirante() {
		return dniAspirante;
	}

	public void setDniAspirante(String dniAspirante) {
		this.dniAspirante = dniAspirante;
	}

	public String getNombreAspirante() {
		return nombreAspirante;
	}

	public void setNombreAspirante(String nombreAspirante) {
		this.nombreAspirante = nombreAspirante;
	}

	public String getApellidoAspirante() {
		return apellidoAspirante;
	}

	public void setApellidoAspirante(String apellidoAspirante) {
		this.apellidoAspirante = apellidoAspirante;
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

	public String getTelefonoAspirante() {
		return telefonoAspirante;
	}

	public void setTelefonoAspirante(String telefonoAspirante) {
		this.telefonoAspirante = telefonoAspirante;
	}

    
    
}

