package com.example.diamon.Controleur;

import org.json.JSONArray;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User implements Serializable {
    public String nom;
    public String pseudo;
    public String account_id;
    public String pwd;


    public User(String nom, String pseudo, String account_id, String pwd) {
        this.nom = nom;
        this.pseudo = pseudo;
        this.account_id = account_id;
        this.pwd = pwd;
    }

    public String getNom() {
        return nom;
    }

    public String getPseudo() {
        return pseudo;
    }

    public String getAccount_id() {
        return account_id;
    }

    public String getPwd() {
        return pwd;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public void setAccount_id(String account_id) {
        this.account_id = account_id;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }
}
