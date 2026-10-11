package ma.enset.ikram.tp7.model;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "espaces_laboratoires_l2")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class EspaceLaboratoire implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_espace")
    private Long idEspace;

    @Column(name = "code_espace", nullable = false, unique = true, length = 30)
    private String codeEspace;

    @Column(name = "intitule_labo", nullable = false)
    private String intituleLabo;

    @Column(name = "capacite_postes")
    private int capacitePostes;

    @Column(name = "specialite_materielle")
    private String specialiteMaterielle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_pole_academique")
    private PoleAcademique poleRattache;

    public EspaceLaboratoire() {}

    public EspaceLaboratoire(String codeEspace, String intituleLabo, int capacitePostes, String specialiteMaterielle) {
        this.codeEspace = codeEspace;
        this.intituleLabo = intituleLabo;
        this.capacitePostes = capacitePostes;
        this.specialiteMaterielle = specialiteMaterielle;
    }

    public Long getIdEspace() { return idEspace; }
    public void setIdEspace(Long idEspace) { this.idEspace = idEspace; }
    public String getCodeEspace() { return codeEspace; }
    public void setCodeEspace(String codeEspace) { this.codeEspace = codeEspace; }
    public String getIntituleLabo() { return intituleLabo; }
    public void setIntituleLabo(String intituleLabo) { this.intituleLabo = intituleLabo; }
    public int getCapacitePostes() { return capacitePostes; }
    public void setCapacitePostes(int capacitePostes) { this.capacitePostes = capacitePostes; }
    public String getSpecialiteMaterielle() { return specialiteMaterielle; }
    public void setSpecialiteMaterielle(String specialiteMaterielle) { this.specialiteMaterielle = specialiteMaterielle; }
    public PoleAcademique getPoleRattache() { return poleRattache; }
    public void setPoleRattache(PoleAcademique poleRattache) { this.poleRattache = poleRattache; }

    @Override
    public String toString() {
        return "EspaceLaboratoire{" + codeEspace + ", " + intituleLabo + ", " + capacitePostes + " postes}";
    }
}