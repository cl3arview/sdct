### Documentation du Projet de gestion Syndicat.

---

## Table des Matières

1. [Aperçu du Projet](#aperçu-du-projet)
2. [Prérequis](#prérequis)
3. [Instructions d'Installation](#instructions-dinstallation)
   - [Cloner le Dépôt](#cloner-le-dépôt)
   - [Configurer Firebase](#configurer-firebase)
   - [Configurer les Clés API](#configurer-les-clés-api)
   - [Construire et Exécuter le Projet](#construire-et-exécuter-le-projet)
4. [Fonctionnalités Principales](#fonctionnalités-principales)
5. [Utilisation](#utilisation)

---

## Aperçu du Projet

Le SDCT (Syndic de Copropriété Tracker) est une application Android pour la gestion des syndicats de copropriété, intégrant Firebase pour l'authentification, le stockage des données et des documents, ainsi que Google Maps et OpenWeatherMap pour les services basés sur la localisation et les informations météorologiques.

## Prérequis

- Android Studio
- Gradle 8.4 and up
- Target SDK 34.
- JDK 8 ou supérieur
- Projet Firebase avec Firestore, Authentication, et Storage
- Clés API Google Maps et OpenWeatherMap

## Instructions d'Installation

### Cloner le Dépôt

```bash
git clone https://github.com/votre-repo/sdct.git
cd sdct
```

### Configurer Firebase

1. Créez un projet Firebase et activez Authentication, Firestore et Storage.
2. Téléchargez `google-services.json` et placez-le dans le répertoire `app`.

### Configurer les Clés API

Ajoutez les lignes suivantes à `gradle.properties` :

```properties
manifestPlaceholders = [MAPS_API_KEY: "${project.properties['MAPS_API_KEY']}"]
WEATHER_API_KEY=VOTRE_CLE_OPENWEATHERMAP
```

Modifiez `build.gradle` (module) :

```gradle
android {
    ...
    buildFeatures {
        buildConfig true
    }
    defaultConfig {
        ...
         manifestPlaceholders = [MAPS_API_KEY: "${project.properties['MAPS_API_KEY']}"]
         buildConfigField "String", "WEATHER_API_KEY", "\"${WEATHER_API_KEY}\""
    }
}
```

### Construire et Exécuter le Projet

1. Ouvrez Android Studio et importez le projet.
2. Synchronisez les fichiers Gradle.
3. Exécutez le projet sur un émulateur ou un appareil physique.

## Fonctionnalités Principales

- **Authentification des Utilisateurs**
  - Inscription, connexion, déconnexion.
- **Gestion des Tâches**
  - Ajouter, compléter, annuler, supprimer des tâches.
  - Voir les tâches actives et complètes.
- **Messagerie**
  - Liste des utilisateurs, envoi et réception de messages.
- **Gestion des Documents**
  - Télécharger, voir, et supprimer des documents.
- **Google Maps**
  - Voir les points d'intérêt à proximité, calcul d'itinéraire.
- **Informations Météorologiques**
  - Afficher les conditions météorologiques actuelles.

## Utilisation

### Authentification des Utilisateurs

1. Inscrivez-vous avec votre email, nom d'utilisateur, et mot de passe.
2. Connectez-vous avec vos identifiants.

### Gestion des Tâches

1. Ajouter une tâche, marquer comme complète, annuler, ou supprimer.
2. Basculez entre tâches actives et complètes.

### Messagerie

1. Sélectionnez un utilisateur pour démarrer une conversation.
2. Envoyez et recevez des messages.

### Gestion des Documents

1. Téléchargez des documents.
2. Voir et supprimer des documents.

### Google Maps

1. Voir les points d'intérêt à proximité.
2. Calculez l'itinéraire vers les points d'intérêt.

### Informations Météorologiques

1. Voir les conditions météorologiques actuelles pour planifier les activités.

