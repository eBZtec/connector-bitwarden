package br.tec.ebz.connid.connector.bitwarden.entities;

import java.util.List;

public class BitwardenListResponse<T> {

    private String object;
    private List<T> data;

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }


}
