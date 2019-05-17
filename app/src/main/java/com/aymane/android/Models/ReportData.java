package com.aymane.android.Models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ReportData {

    List<Report> reports = new ArrayList<>();

    public static List<String> getBesoinsValues() {
        List<String> besoins = new ArrayList<>();
        besoins.add("Nourriture");
        besoins.add("Couverture");
        besoins.add("Chaussures");
        besoins.add("Abris");
        besoins.add("Logement");
        besoins.add("Eau");
        besoins.add("Soutien Psychologique");
        besoins.add("Boissons");
        Collections.sort(besoins);
        return besoins;
    }

    public static List<String> getSexeValues() {
        List<String> sexe = new ArrayList<>();
        sexe.add("Homme");
        sexe.add("Femme");
        Collections.sort(sexe);
        return sexe;
    }

    public static List<String> getAgeValues() {
        List<String> age = new ArrayList<>();
        age.add("Adolescent");
        age.add("Personne Jeune");
        age.add("Personne Agé");
        age.add("Enfant");
        Collections.sort(age);
        return age;
    }

    public static List<String> getEtatValues() {
        List<String> etat = new ArrayList<>();
        etat.add("Anxieu");
        etat.add("Agressif");
        etat.add("Violent");
        etat.add("Soul");
        etat.add("Drogué");
        etat.add("Fiévreux");
        etat.add("Blessé");
        etat.add("Malade");
        Collections.sort(etat);
        return etat;
    }

}
