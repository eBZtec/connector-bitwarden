package br.tec.ebz.connid.connector.bitwarden.entities;

import java.util.ArrayList;
import java.util.List;

public class BitwardenGroup extends BitwardenObject {

    private String name;
    private String externalId;
    private List<BitwardenCollection> collections;

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

    public List<BitwardenCollection> getCollections() {
        if (collections == null) {
            return new ArrayList<>();
        }

        return collections;
    }

    public void setCollections(List<BitwardenCollection> collections) {

        this.collections = collections;
    }

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof BitwardenGroup that)) return false;

        return getId().equals(that.getId()) && getName().equals(that.getName()) && getExternalId().equals(that.getExternalId()) && getCollections().equals(that.getCollections());
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + getId().hashCode();
        result = 31 * result + getName().hashCode();
        result = 31 * result + getExternalId().hashCode();
        result = 31 * result + getCollections().hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "BitwardenGroup{" +
                ", name='" + name + '\'' +
                ", externalId='" + externalId + '\'' +
                ", collections=" + collections +
                "} " + super.toString();
    }
}
