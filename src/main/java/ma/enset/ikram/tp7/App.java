package ma.enset.ikram.tp7;

import ma.enset.ikram.tp7.model.EspaceLaboratoire;
import ma.enset.ikram.tp7.model.PoleAcademique;
import org.hibernate.stat.Statistics;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;

public class App {
    public static void main(String[] args) {
        System.out.println("==========================================================================");
        System.out.println(">>> DEMARRAGE TP7 - CACHE L2 & RESOLUTION DU PROBLEME N+1 (JPA) <<<");
        System.out.println("                       (IKRAM MELLAKH - ENSET)                   ");
        System.out.println("==========================================================================");

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("ikramTP7_PU");

        // [1] Initialisation du jeu de test
        EntityManager emInit = emf.createEntityManager();
        emInit.getTransaction().begin();

        PoleAcademique poleIA = new PoleAcademique("POL-IA", "PÃ´le Intelligence Artificielle & Big Data", 350000.0);
        poleIA.ajouterLaboratoire(new EspaceLaboratoire("LAB-GPU-01", "Labo Deep Learning & Vision", 40, "Clusters GPU RTX 4090"));
        poleIA.ajouterLaboratoire(new EspaceLaboratoire("LAB-NLP-02", "Labo Traitement du Langage & LLM", 30, "Serveurs A100 TensorCore"));

        PoleAcademique poleCyber = new PoleAcademique("POL-CYBER", "PÃ´le CybersÃ©curitÃ© & RÃ©seaux DistribuÃ©s", 280000.0);
        poleCyber.ajouterLaboratoire(new EspaceLaboratoire("LAB-SOC-01", "Labo Red Team & SOC", 25, "Bancs RÃ©seaux DÃ©diÃ©s"));
        poleCyber.ajouterLaboratoire(new EspaceLaboratoire("LAB-IOT-02", "Labo SÃ©curitÃ© IoT & EmbarquÃ©", 20, "Cartes FPGA et Sondes"));

        PoleAcademique poleRobotic = new PoleAcademique("POL-ROBOT", "PÃ´le Robotique & SystÃ¨mes Autonomes", 420000.0);
        poleRobotic.ajouterLaboratoire(new EspaceLaboratoire("LAB-ROB-01", "Labo Bras Manipulateurs & Drones", 15, "Robots Mobiles ROS2"));

        emInit.persist(poleIA);
        emInit.persist(poleCyber);
        emInit.persist(poleRobotic);

        emInit.getTransaction().commit();
        Long idPoleTest = poleIA.getIdPole();
        emInit.close();
        System.out.println("âœ“ Jeu de donnees initialise : 3 poles academiques et 5 espaces laboratoires associes.\n");

        // [2] DÃ©monstration du ProblÃ¨me N+1 (Lazy Loading)
        System.out.println("--- [2] DEMONSTRATION DU PROBLEME N+1 (LAZY LOADING) ---");
        EntityManager emN1 = emf.createEntityManager();
        System.out.println(">> Requete initiale : SELECT p FROM PoleAcademique p");
        List<PoleAcademique> poles = emN1.createQuery("SELECT p FROM PoleAcademique p", PoleAcademique.class).getResultList();

        System.out.println(">> Acces aux collections paresseuses (declenchement des N requetes secondaires) :");
        for (PoleAcademique p : poles) {
            System.out.println("   Pole : " + p.getDesignationPole() + " -> Nombre de labos : " + p.getSallesLaboratoires().size());
        }
        emN1.close();

        // [3] Solution 1 : Ã‰limination du problÃ¨me N+1 via JOIN FETCH
        System.out.println("\n--- [3] SOLUTION 1 : ELIMINATION VIA JOIN FETCH ---");
        EntityManager emFetch = emf.createEntityManager();
        System.out.println(">> Requete unique optimisee avec JOIN FETCH :");
        List<PoleAcademique> polesFetch = emFetch.createQuery(
                "SELECT DISTINCT p FROM PoleAcademique p JOIN FETCH p.sallesLaboratoires",
                PoleAcademique.class
        ).getResultList();

        for (PoleAcademique p : polesFetch) {
            System.out.println("   [JOIN FETCH] " + p.getDesignationPole() + " -> " + p.getSallesLaboratoires().size() + " labos charges en une seule requete.");
        }
        emFetch.close();

        // [4] Solution 2 : Ã‰limination du problÃ¨me N+1 via EntityGraph JPA
        System.out.println("\n--- [4] SOLUTION 2 : ELIMINATION VIA ENTITY GRAPH ---");
        EntityManager emGraph = emf.createEntityManager();
        EntityGraph<?> graph = emGraph.getEntityGraph("PoleAcademique.avecLaboratoires");

        List<PoleAcademique> polesGraph = emGraph.createQuery("SELECT p FROM PoleAcademique p", PoleAcademique.class)
                .setHint("javax.persistence.fetchgraph", graph)
                .getResultList();

        for (PoleAcademique p : polesGraph) {
            System.out.println("   [ENTITY GRAPH] " + p.getDesignationPole() + " -> " + p.getSallesLaboratoires().size() + " labos charges.");
        }
        emGraph.close();

        // [5] DÃ©monstration du Cache de Second Niveau (L2 Cache Ehcache)
        System.out.println("\n--- [5] DEMONSTRATION DU CACHE DE SECOND NIVEAU (L2 CACHE) ---");
        Statistics stats = emf.unwrap(org.hibernate.SessionFactory.class).getStatistics();
        stats.setStatisticsEnabled(true);
        stats.clear();

        // Session 1 -> Miss dans le cache, lecture en base
        EntityManager emCache1 = emf.createEntityManager();
        System.out.println("Session 1 : Chargement de l'entite ID = " + idPoleTest);
        PoleAcademique p1 = emCache1.find(PoleAcademique.class, idPoleTest);
        System.out.println("Session 1 a lu : " + p1.getDesignationPole());
        emCache1.close(); // Session 1 fermee, cache L1 detruit

        // Session 2 -> Doit etre servi depuis le cache L2 sans requete SQL
        EntityManager emCache2 = emf.createEntityManager();
        System.out.println("\nSession 2 : Nouveau chargement ID = " + idPoleTest + " (apres fermeture Session 1)");
        PoleAcademique p2 = emCache2.find(PoleAcademique.class, idPoleTest);
        System.out.println("Session 2 a lu : " + p2.getDesignationPole());
        emCache2.close();

        System.out.println("\n--- STATISTIQUES OFFICIELLES DU CACHE L2 ---");
        System.out.println("L2 Cache Hits   : " + stats.getSecondLevelCacheHitCount() + " (Servi depuis le cache memoire sans SQL)");
        System.out.println("L2 Cache Misses : " + stats.getSecondLevelCacheMissCount() + " (Lecture SQL en base)");
        System.out.println("L2 Cache Puts   : " + stats.getSecondLevelCachePutCount());

        emf.close();
        System.out.println("\n==========================================================================");
        System.out.println(">>> DEMONSTRATION DU TP7 TERMINEE AVEC SUCCES (BUILD SUCCESS) <<<");
        System.out.println("==========================================================================");
    }
}