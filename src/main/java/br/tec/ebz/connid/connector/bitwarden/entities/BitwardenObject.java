package br.tec.ebz.connid.connector.bitwarden.entities;

public class BitwardenObject {
    private String object;
    private String id;

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        int result = getObject().hashCode();
        result = 31 * result + getId().hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Object{" +
                "object='" + object + '\'' +
                '}';
    }
}
