package com.corazza.fosco.lumenGame.savemanager;

/**
 * Created by Simone on 01/06/2016.
 */
public class SchemeResult {
    private String code;
    private int stars;
    private int punteggio;
    private boolean sbloccato;

    public SchemeResult(String code, int stars, int punteggio, boolean sbloccato) {
        this.code = code;
        this.stars = stars;
        this.punteggio = punteggio;
        this.sbloccato = sbloccato;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getStars() {
        return stars;
    }

    public void setStars(int stars) {
        this.stars = stars;
    }

    public int getPunteggio() {
        return punteggio;
    }

    public void setPunteggio(int punteggio) {
        this.punteggio = punteggio;
    }

    static SchemeResult fromString(String string) {
        String[] strings = string.split("\\.");
        if(strings.length != 4)  return null;
        return new SchemeResult(strings[0], Integer.parseInt(strings[1]), Integer.parseInt(strings[2]), "true".equals(strings[3]));
    }

    @Override
    public String toString() {
        return code + "." + stars + "." + punteggio + "." + sbloccato;
    }

    public boolean isUnlocked() {
        return sbloccato;
    }

    public boolean isSbloccato() {
        return sbloccato;
    }

    public void setSbloccato(boolean sbloccato) {
        this.sbloccato = sbloccato;
    }
}
