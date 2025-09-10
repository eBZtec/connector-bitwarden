package br.tec.ebz.connid.connector.bitwarden.entities;

import java.util.Objects;

public class BitwardenGroup {
    private String id;
    private String object;
    private String name;
    private String externalId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof BitwardenGroup that)) return false;

        return getId().equals(that.getId()) && getObject().equals(that.getObject()) && Objects.equals(getName(), that.getName()) && Objects.equals(getExternalId(), that.getExternalId());
    }

    @Override
    public int hashCode() {
        int result = getId().hashCode();
        result = 31 * result + getObject().hashCode();
        result = 31 * result + Objects.hashCode(getName());
        result = 31 * result + Objects.hashCode(getExternalId());
        return result;
    }

    @Override
    public String toString() {
        return "BitwardenGroup{" +
                "id='" + id + '\'' +
                ", object='" + object + '\'' +
                ", name='" + name + '\'' +
                ", externalId='" + externalId + '\'' +
                '}';
    }
}
