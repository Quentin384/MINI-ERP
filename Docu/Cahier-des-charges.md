# TP : Création d’un mini ERP

## 🎯 Objectif pédagogique

Développer une interface graphique de type **mini ERP** permettant de :

- Gérer les clients
- Visualiser les produits
- Passer des commandes simples
- Afficher l’historique des commandes

## 🧠 Compétences mobilisées

- Connexion à une base de données PostgreSQL via **JDBC**
- Création d’interfaces graphiques avec **AWT/Swing**
- Gestion des événements
- Requêtes SQL complexes (jointures, insertion via fonction)

## 🗂 Structure du TP

Le TP s’appuie sur la base de données **`store`**.

---

### Étape 1 : Connexion à la base PostgreSQL

**Objectifs** :
- Établir une connexion à la base via JDBC
- Tester la connexion avec une requête simple (ex : `SELECT * FROM customers LIMIT 5`)

**Instructions** :
- Créer une classe `DatabaseManager` pour gérer la connexion à la BD

---

### Étape 2 : Affichage des clients

**Objectifs** :
- Récupérer la liste des clients
- Afficher les résultats dans une `JTable`

**Instructions** :
- Créer une classe `CustomerDAO` pour l'accès aux données
- Créer une classe `Customer` (POJO)
- Interface graphique avec un bouton **"Charger clients"** pour remplir une `JTable`

---

### Étape 3 : Ajouter un client (via la fonction SQL `new_customer`)

**Objectifs** :
- Saisir les données client via un formulaire Swing
- Appeler la fonction `new_customer(...)` via `CallableStatement`
- Afficher un message de succès ou d’erreur

**Instructions** :
- Créer un formulaire `AddCustomerForm`
- Vérifier les champs obligatoires

---

### Étape 4 : Visualisation des produits

**Objectifs** :
- Lister les produits avec prix et catégorie
- Ajouter un filtre par catégorie

**Instructions** :
- Créer `ProductDAO` et `Product` (POJO)
- Afficher les résultats dans un tableau
- Ajouter un `JComboBox` pour filtrer par catégorie

---

### Étape 5 : Création d’une commande

**Objectifs** :
- Sélectionner un client et des produits
- Saisir une commande (une ou plusieurs lignes)
- Insérer les données dans `orders` et `orderlines`

**Instructions** :
- Utiliser les transactions JDBC (`begin`, `commit`, `rollback`)
- Calculer les champs : `netamount`, `tax`, `totalamount`

---

### Étape 6 : Historique des commandes client

**Objectifs** :
- À partir d’un client sélectionné, afficher ses commandes passées
- Détail des produits commandés (jointure avec `orderlines` et `products`)

---

### Étape 7 : Gestion du stock

**Objectifs** :
- Mettre à jour le stock dans `inventory` à chaque commande
- Afficher les produits à reconstituer (`inventory.quan_in_stock < seuil`)

---

## 📁 Dossier de rendu attendu

- Code source Java – **lien GitHub**
- Fichier `README` contenant :
  - Configuration JDBC
  - Captures d’écran de l’interface
  - Consignes éventuelles de lancement
