package com.akiniyalocts.files.model;

/**
 * Created by anthonykiniyalocts on 1/28/17.
 *
 * {"success":true,"key":"rwfPwY","link":"https://file.io/rwfPwY","expiry":"14 days"}
 */

public class Link {

    private boolean success;

    private String key;

    private String link;

    private String expiry;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getExpiry() {
        return expiry;
    }

    public void setExpiry(String expiry) {
        this.expiry = expiry;
    }
}
