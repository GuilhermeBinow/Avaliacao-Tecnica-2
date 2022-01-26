package pauta.object;

import javax.persistence.*;

@Entity
@Table(name = "Pauta_info")
public class Pauta {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Integer numeroDeAssociados;

    public Pauta(Integer numeroDeAssociados) {
        this.numeroDeAssociados = numeroDeAssociados;
    }

    public Pauta() {
    }

    public Long getId() {
        return id;
    }


    public Integer getNumeroDeAssociados() {
        return numeroDeAssociados;
    }

    public void setNumeroDeAssociados(Integer numeroDeAssociados) {
        this.numeroDeAssociados = numeroDeAssociados;
    }
}
