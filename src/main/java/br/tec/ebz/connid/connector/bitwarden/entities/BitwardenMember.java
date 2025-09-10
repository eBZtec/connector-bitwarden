package br.tec.ebz.connid.connector.bitwarden.entities;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BitwardenMember {

    private String object;
    private String id;
    private String name;
    private String email;
    private Boolean twoFactorEnabled;
    private Integer status;
    private List<BitwardenCollection> collections;
    private Boolean resetPasswordEnrolled;
    private String ssoExternalId;
    private Integer type;
    private String externalId;
    private List<String> groups;
    private BitwardenPermissions permissions;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getTwoFactorEnabled() {
        return twoFactorEnabled;
    }

    public void setTwoFactorEnabled(Boolean twoFactorEnabled) {
        this.twoFactorEnabled = twoFactorEnabled;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public List<BitwardenCollection> getCollections() {
        if (collections == null) return new ArrayList<>();

        return collections;
    }

    public void setCollections(List<BitwardenCollection> collections) {
        this.collections = collections;
    }

    public Boolean getResetPasswordEnrolled() {
        return resetPasswordEnrolled;
    }

    public void setResetPasswordEnrolled(Boolean resetPasswordEnrolled) {
        this.resetPasswordEnrolled = resetPasswordEnrolled;
    }

    public String getSsoExternalId() {
        return ssoExternalId;
    }

    public void setSsoExternalId(String ssoExternalId) {
        this.ssoExternalId = ssoExternalId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public List<String> getGroups() {

        if (groups == null) return new ArrayList<>();

        return groups;
    }

    public void setGroups(List<String> groups) {
        this.groups = groups;
    }

    public BitwardenPermissions getPermissions() {
        return permissions;
    }

    public void setPermissions(BitwardenPermissions permissions) {
        this.permissions = permissions;
    }

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof BitwardenMember member)) return false;

        return getObject().equals(member.getObject()) && Objects.equals(getId(), member.getId()) && Objects.equals(getName(), member.getName()) && getEmail().equals(member.getEmail()) && Objects.equals(getTwoFactorEnabled(), member.getTwoFactorEnabled()) && getStatus().equals(member.getStatus()) && Objects.equals(getCollections(), member.getCollections()) && Objects.equals(getResetPasswordEnrolled(), member.getResetPasswordEnrolled()) && Objects.equals(getSsoExternalId(), member.getSsoExternalId()) && getType().equals(member.getType()) && Objects.equals(getExternalId(), member.getExternalId()) && Objects.equals(getGroups(), member.getGroups()) && Objects.equals(getPermissions(), member.getPermissions());
    }

    @Override
    public int hashCode() {
        int result = getObject().hashCode();
        result = 31 * result + Objects.hashCode(getId());
        result = 31 * result + Objects.hashCode(getName());
        result = 31 * result + getEmail().hashCode();
        result = 31 * result + Objects.hashCode(getTwoFactorEnabled());
        result = 31 * result + getStatus().hashCode();
        result = 31 * result + Objects.hashCode(getCollections());
        result = 31 * result + Objects.hashCode(getResetPasswordEnrolled());
        result = 31 * result + Objects.hashCode(getSsoExternalId());
        result = 31 * result + getType().hashCode();
        result = 31 * result + Objects.hashCode(getExternalId());
        result = 31 * result + Objects.hashCode(getGroups());
        result = 31 * result + Objects.hashCode(getPermissions());
        return result;
    }

    @Override
    public String toString() {
        return "BitwardenMember{" +
                "object='" + object + '\'' +
                ", id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", twoFactorEnabled=" + twoFactorEnabled +
                ", status=" + status +
                ", collections=" + collections +
                ", resetPasswordEnrolled=" + resetPasswordEnrolled +
                ", ssoExternalId='" + ssoExternalId + '\'' +
                ", type=" + type +
                ", externalId='" + externalId + '\'' +
                ", groups=" + groups +
                ", permissions=" + permissions +
                '}';
    }
}
