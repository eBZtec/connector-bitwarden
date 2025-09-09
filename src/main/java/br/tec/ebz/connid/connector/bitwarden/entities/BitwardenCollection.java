package br.tec.ebz.connid.connector.bitwarden.entities;

import java.util.List;

public class BitwardenCollection extends BitwardenObject {
    private String externalId;
    private List<BitwardenGroup> groups;

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public List<BitwardenGroup> getGroups() {
        return groups;
    }

    public void setGroups(List<BitwardenGroup> groups) {
        this.groups = groups;
    }

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof BitwardenCollection that)) return false;

        return getExternalId().equals(that.getExternalId()) && getGroups().equals(that.getGroups());
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + getExternalId().hashCode();
        result = 31 * result + getGroups().hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "BitwardenCollection{" +
                "externalId='" + externalId + '\'' +
                ", groups=" + groups +
                "} " + super.toString();
    }
}
