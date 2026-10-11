package ma.enset.ikram.tp7.model;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "poles_academiques_l2")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@NamedEntityGraph(
        name = "PoleAcademique.avecLaboratoires",
        attributeNodes = @NamedAttributeNode("sallesLaboratoires")
)
public class PoleAcademique implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pole")
    private Long idPole;

    @Column(name = "code_pole", nullable = false, unique = true, length = 30)
    private String codePole;

    @Column(name = "designation_pole", nullable = false, length = 120)
    private String designationPole;

    @Column(name = "budget_recherche")
    private Double budgetRecherche;

    @OneToMany(mappedBy = "poleRattache", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private List<EspaceLaboratoire> sallesLaboratoires = new ArrayList<>();

    public PoleAcademique() {}

    public PoleAcademique(String codePole, String designationPole, Double budgetRecherche) {
        this.codePole = codePole;
        this.designationPole = designationPole;
        this.budgetRecherche = budgetRecherche;
    }

    public void ajouterLaboratoire(EspaceLaboratoire labo) {
        this.sallesLaboratoires.add(labo);
        labo.setPoleRattache(this);
    }

    public Long getIdPole() { return idPole; }
    public void setIdPole(Long idPole) { this.idPole = idPole; }
    public String getCodePole() { return codePole; }
    public void setCodePole(String codePole) { this.codePole = codePole; }
    public String getDesignationPole() { return designationPole; }
    public void setDesignationPole(String designationPole) { this.designationPole = designationPole; }
    public Double getBudgetRecherche() { return budgetRecherche; }
    public void setBudgetRecherche(Double budgetRecherche) { this.budgetRecherche = budgetRecherche; }
    public List<EspaceLaboratoire> getSallesLaboratoires() { return sallesLaboratoires; }
    public void setSallesLaboratoires(List<EspaceLaboratoire> sallesLaboratoires) { this.sallesLaboratoires = sallesLaboratoires; }

    @Override
    public String toString() {
        return "PoleAcademique{" + codePole + " : " + designationPole + "}";
    }
}