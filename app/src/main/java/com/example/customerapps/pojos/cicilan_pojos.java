package com.example.customerapps.pojos;

public class cicilan_pojos {

    private String tgl_payment;
    private String payment;
    private String no_payment;

    public cicilan_pojos(String tgl_payment, String payment, String no_payment) {
        this.tgl_payment = tgl_payment;
        this.payment = payment;
        this.no_payment = no_payment;

    }

    public String getTgl_payment() {
        return tgl_payment;
    }

    public String getPayment() {
        return payment;
    }

    public String getNo_payment() {
        return no_payment;
    }
}
