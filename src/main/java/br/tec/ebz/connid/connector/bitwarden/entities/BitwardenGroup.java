package br.tec.ebz.connid.connector.bitwarden.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BitwardenGroup {
    private String id;
    private String object;
    private String name;
    private String externalId;
    private List<BitwardenCollections> collections;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public List<BitwardenCollections> getCollections() {
        if (collections == null) return new ArrayList<>();
        return collections;
    }

    public void setCollections(List<BitwardenCollections> collections) {
        this.collections = collections;
    }

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof BitwardenGroup group)) return false;

        return getId().equals(group.getId()) && getObject().equals(group.getObject()) && Objects.equals(getName(), group.getName()) && Objects.equals(getExternalId(), group.getExternalId()) && Objects.equals(getCollections(), group.getCollections());
    }

    @Override
    public int hashCode() {
        int result = getId().hashCode();
        result = 31 * result + getObject().hashCode();
        result = 31 * result + Objects.hashCode(getName());
        result = 31 * result + Objects.hashCode(getExternalId());
        result = 31 * result + Objects.hashCode(getCollections());
        return result;
    }

    @Override
    public String toString() {
        return "BitwardenGroup{" +
                "id='" + id + '\'' +
                ", object='" + object + '\'' +
                ", name='" + name + '\'' +
                ", externalId='" + externalId + '\'' +
                ", collections=" + collections +
                '}';
    }
}
