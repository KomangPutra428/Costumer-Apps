package com.example.customerapps.pojos;

public class co_pojos {
    private String no_co;
    private String no_invoice;
    private String szName;

    private String decQty;
    private String dtmDue;
    private String nom_inv;
    private String tanggal_co;

    private String no_so;
    private String no_do;

    private String dtmClosed;


    public co_pojos(String no_co, String no_invoice, String szName,
                    String decQty, String dtmDue, String nom_inv,
                    String tanggal_co, String no_so, String no_do, String dtmClosed) {
        this.no_co = no_co;
        this.no_invoice = no_invoice;
        this.szName = szName;

        this.decQty = decQty;
        this.dtmDue = dtmDue;
        this.nom_inv = nom_inv;
        this.tanggal_co = tanggal_co;

        this.no_so = no_so;
        this.no_do = no_do;
        this.dtmClosed = dtmClosed;
    }

    public String getNo_co() {
        return no_co;
    }

    public String getNo_invoice() {
        return no_invoice;
    }

    public String getSzName() {
        return szName;
    }

    public String getDecQty() {
        return decQty;
    }

    public String getDtmDue() {
        return dtmDue;
    }

    public String getNom_inv() {
        return nom_inv;
    }

    public String getTanggal_co() {
        return tanggal_co;
    }

    public String getNo_so() {
        return no_so;
    }
    public String getNo_do() {
        return no_do;
    }

    public String getDtmClosed() {
        return dtmClosed;
    }

    @Override
    public String toString() {
        return no_co;
    }
}
