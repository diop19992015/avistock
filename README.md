# Avistock

Avistock est une application web de gestion destinée aux petites et moyennes
exploitations avicoles, en particulier aux élevages de poulets de chair. Elle
centralise le suivi des lots, des clients, des ventes et des pertes afin de
fiabiliser le stock vivant, d'évaluer la rentabilité et de soutenir les
décisions d'approvisionnement.

Le projet répond aux limites d'une gestion sur cahier ou tableur : informations
dispersées, mises à jour manuelles, difficulté à expliquer les pertes et manque
de visibilité sur les résultats économiques.

## Fonctionnalités

- Gestion des lots : quantité initiale, quantité restante, charges et date de
  maturité prévue.
- Gestion des clients et des utilisateurs.
- Enregistrement des commandes reliées à un lot et décrémentation automatique
  du stock disponible.
- Enregistrement des pertes et mise à jour automatique du stock restant.
- Suivi des événements commerciaux ou saisonniers.
- Calcul de la rentabilité par lot et au niveau global.
- Génération de rapports par lot.
- Suggestion d'approvisionnement tenant compte de l'historique des ventes, du
  stock projeté, des pertes historiques et des événements saisis.

> Le module de prévision est un moteur de règles et de calculs métier. Il ne
> s'agit pas encore d'un modèle de machine learning entraîné sur des données
> externes.

## Architecture

```text
React + Vite (interface web)  -->  API REST Spring Boot  -->  H2 (base de données)
          port 5173                     port 8080
```

| Composant | Technologies |
| --- | --- |
| Frontend | React, Vite, Material UI, Axios |
| Backend | Java 17, Spring Boot, Spring MVC, Spring Data JPA, Hibernate |
| Base de données | H2, persistante sur disque en développement |
| Conteneurisation | Docker et Docker Compose |

## Démarrage rapide avec Docker

### Prérequis

- Docker Desktop avec Docker Compose

Depuis la racine du projet :

```bash
docker compose up --build
```

Une fois les conteneurs démarrés :

- Interface : <http://localhost:5173>
- API : <http://localhost:8080/api>
- Console H2 : <http://localhost:8080/h2-console>

Pour arrêter les services :

```bash
docker compose down
```

Les données H2 utilisées par Docker sont conservées dans le volume
`avistock-h2-data`.

## Démarrage en développement

### Prérequis

- JDK 17 ou version compatible
- Node.js et npm

### 1. Démarrer l'API Spring Boot

Sous Windows :

```powershell
.\mvnw.cmd spring-boot:run
```

Sous macOS ou Linux :

```bash
./mvnw spring-boot:run
```

L'API est alors disponible sur `http://localhost:8080`.

### 2. Démarrer l'interface React

Dans un second terminal :

```bash
cd avicole-stock-ia-front
npm ci
npm run dev
```

Vite affiche l'adresse locale de l'interface, généralement
`http://localhost:5173`. Par défaut, le frontend appelle l'API sur le port
`8080` de la même machine.

Pour cibler une autre adresse d'API :

```bash
VITE_API_URL=http://localhost:8080/api npm run dev
```

Sous PowerShell :

```powershell
$env:VITE_API_URL = "http://localhost:8080/api"
npm run dev
```

## API REST

Les principales ressources sont exposées sous `/api` :

| Ressource | Point d'entrée |
| --- | --- |
| Lots | `/api/lots` |
| Clients | `/api/clients` |
| Commandes | `/api/commandes` |
| Pertes | `/api/pertes` |
| Événements | `/api/evenements` |
| Rentabilité | `/api/rentabilite` |
| Rapports | `/api/rapports` |
| Prévisions | `/api/previsions/approvisionnement` |

Exemples de calculs disponibles :

- `GET /api/rentabilite/global`
- `GET /api/rentabilite/lots/{lotId}`
- `GET /api/rapports/lots/{lotId}`
- `POST /api/previsions/approvisionnement`

## Règles métier importantes

- Une commande ou une perte ne peut pas dépasser la quantité restante du lot.
- La suppression d'une commande ou d'une perte restitue la quantité concernée
  au lot.
- Un lot lié à des commandes ou à des pertes ne peut pas être supprimé avant
  suppression de ces données associées.
- La suggestion d'approvisionnement prend notamment en compte la demande sur
  les 120 derniers jours, la mortalité observée, les lots arrivant à maturité
  et les effets avant ou après événement.

## Structure du projet

```text
.
├── src/                         # API Spring Boot
│   ├── main/java/               # contrôleurs, services, entités et DTO
│   └── main/resources/          # configuration de l'application
├── avicole-stock-ia-front/      # interface React + Vite
├── documents/                   # documentation fonctionnelle du projet
├── docker-compose.yml           # démarrage conjoint frontend et backend
└── Dockerfile                   # image du backend
```

## Tests et qualité

Lancer les tests backend :

```bash
./mvnw test
```

Sous Windows :

```powershell
.\mvnw.cmd test
```

Vérifier le frontend :

```bash
cd avicole-stock-ia-front
npm run lint
npm run build
```

## Limites et évolutions envisagées

Avistock est un projet en évolution, conçu dans un cadre académique et
démonstratif. Avant un déploiement en production, il est recommandé de :

- mettre en place une authentification et une autorisation par rôles ;
- remplacer H2 par une base de données adaptée à la production ;
- sécuriser la configuration et les échanges réseau ;
- ajouter des sauvegardes, une journalisation métier et une stratégie de
  déploiement ;
- faire évoluer le moteur de prévision avec davantage de données historiques
  et une validation des résultats.

## Documentation

La documentation de conception et les documents associés se trouvent dans le
dossier [`documents`](documents/).

---

Avistock vise à fournir une gestion avicole plus fiable, plus lisible et mieux
adaptée aux décisions quotidiennes qu'un suivi manuel ou un tableur générique.
