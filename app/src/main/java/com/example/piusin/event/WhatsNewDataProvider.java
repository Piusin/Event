package com.example.piusin.event;

/**
 * Created by Piusin on 2/17/2018.
 */

public class WhatsNewDataProvider {
    private int whatsNewImage;
    private String prodName;

    public WhatsNewDataProvider(int whatsNewImage, String prodName) {
        this.whatsNewImage = whatsNewImage;
        this.prodName = prodName;
    }

    public int getWhatsNewImage() {
        return whatsNewImage;
    }

    public String getProdName() {
        return prodName;
    }
}
