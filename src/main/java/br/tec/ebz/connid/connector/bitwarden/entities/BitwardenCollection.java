package br.tec.ebz.connid.connector.bitwarden.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BitwardenCollection {
    private String id;
    private String externalId;
    private List<BitwardenAccess> groups;
    private String object;

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

    public List<BitwardenAccess> getGroups() {
        if (groups == null) return new ArrayList<>();
        return groups;
    }

    public void setGroups(List<BitwardenAccess> groups) {
        this.groups = groups;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof BitwardenCollection that)) return false;

        return getId().equals(that.getId()) && Objects.equals(getExternalId(), that.getExternalId()) && Objects.equals(getGroups(), that.getGroups()) && getObject().equals(that.getObject());
    }

    @Override
    public int hashCode() {
        int result = getId().hashCode();
        result = 31 * result + Objects.hashCode(getExternalId());
        result = 31 * result + Objects.hashCode(getGroups());
        result = 31 * result + getObject().hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "BitwardenCollection{" +
                "id='" + id + '\'' +
                ", externalId='" + externalId + '\'' +
                ", groups=" + getGroups() +
                ", object='" + object + '\'' +
                '}';
    }
}
