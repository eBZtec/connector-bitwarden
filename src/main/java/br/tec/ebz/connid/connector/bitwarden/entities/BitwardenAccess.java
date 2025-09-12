package br.tec.ebz.connid.connector.bitwarden.entities;

import java.util.Objects;

public class BitwardenAccess {
    private String id;
    private Boolean readOnly;
    private Boolean hidePasswords;
    private Boolean manage;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Boolean getManage() {
        return manage;
    }

    public void setManage(Boolean manage) {
        this.manage = manage;
    }

    public Boolean getHidePasswords() {
        return hidePasswords;
    }

    public void setHidePassword(Boolean hidePasswords) {
        this.hidePasswords = hidePasswords;
    }

    public Boolean getReadOnly() {
        return readOnly;
    }

    public void setReadOnly(Boolean readOnly) {
        this.readOnly = readOnly;
    }

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof BitwardenAccess that)) return false;

        return getId().equals(that.getId()) && Objects.equals(getReadOnly(), that.getReadOnly()) && Objects.equals(getHidePasswords(), that.getHidePasswords()) && Objects.equals(getManage(), that.getManage());
    }

    @Override
    public int hashCode() {
        int result = getId().hashCode();
        result = 31 * result + Objects.hashCode(getReadOnly());
        result = 31 * result + Objects.hashCode(getHidePasswords());
        result = 31 * result + Objects.hashCode(getManage());
        return result;
    }

    @Override
    public String toString() {
        return "BitwardenCollections{" +
                "id='" + id + '\'' +
                ", readOnly=" + readOnly +
                ", hidePasswords=" + hidePasswords +
                ", manage=" + manage +
                '}';
    }
}
