# TP 7 : Cache L2 (Ehcache) & Ã‰limination du ProblÃ¨me N+1 (Ikram Mellakh)

Optimisation des performances sous JPA 2.2 et Hibernate 5.6 :
- DÃ©monstration de l'explosion des requÃªtes SQL engendrÃ©e par le lazy loading (ProblÃ¨me N+1)
- Ã‰radication via requÃªte `JOIN FETCH`
- Ã‰radication via `@NamedEntityGraph` et hint dynamique `javax.persistence.fetchgraph`
- Cache de Second Niveau (L2 Cache Ehcache) avec statistiques de hits / misses

---

## Captures d'Ã‰cran IntelliJ IDEA & Terminal CMD

### 1. EntitÃ© PoleAcademique @Cacheable & NamedEntityGraph (IntelliJ IDEA)
![EntitÃ© Cache L2](docs/images/01-intellij-cache-entitygraph.png)

### 2. EntitÃ© EspaceLaboratoire AssociÃ©e (IntelliJ IDEA)
![EntitÃ© Laboratoire](docs/images/02-intellij-espace-labo.png)

### 3. RÃ©sultat des Tests et ExÃ©cution (CMD / PowerShell)
![RÃ©sultat ExÃ©cution CMD](docs/images/03-cmd-test-result.png)

---

## Lancement
```bash
mvn clean compile exec:java -Dexec.mainClass=ma.enset.ikram.tp7.App
```