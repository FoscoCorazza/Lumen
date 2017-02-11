package com.corazza.fosco.lumenGame.savemanager;

/**
 * Created by Simone on 01/06/2016.
 */
public class SchemeResult {
    private String code;
    private int stars;
    private boolean unwasted;
    private boolean perfect;
    private boolean sbloccato;

    public SchemeResult(String code, int stars, boolean unwasted, boolean perfect, boolean sbloccato) {
        this.code = code;
        this.stars = stars;
        this.unwasted = unwasted;
        this.perfect = perfect;
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


    static SchemeResult fromString(String string) {
        String[] strings = string.split("\\.");
        if(strings.length != 5)  return null;

        return new SchemeResult(strings[0],
                Integer.parseInt(strings[1]),
                "true".equals(strings[2]),
                "true".equals(strings[3]),
                "true".equals(strings[4]));
    }

    @Override
    public String toString() {
        return code + "." + stars + "." + unwasted + "." + perfect + "." + sbloccato;
    }


    public boolean isSbloccato() {
        return sbloccato;
    }

    public void setSbloccato(boolean sbloccato) {
        this.sbloccato = sbloccato;
    }

    public int getTotal() {
        return stars + (unwasted ? 1:0) + (perfect?1:0);
    }
}
