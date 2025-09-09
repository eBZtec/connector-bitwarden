package br.tec.ebz.connid.connector.bitwarden.entities;


import java.util.List;

public class BitwardenMember extends BitwardenObject {

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
        return groups;
    }

    public void setGroups(List<String> groups) {
        this.groups = groups;
    }

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof BitwardenMember that)) return false;

        return getName().equals(that.getName()) && getEmail().equals(that.getEmail()) && getTwoFactorEnabled().equals(that.getTwoFactorEnabled()) && getStatus().equals(that.getStatus()) && getCollections().equals(that.getCollections()) && getResetPasswordEnrolled().equals(that.getResetPasswordEnrolled()) && getSsoExternalId().equals(that.getSsoExternalId()) && getType().equals(that.getType()) && getExternalId().equals(that.getExternalId()) && getGroups().equals(that.getGroups());
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + getName().hashCode();
        result = 31 * result + getEmail().hashCode();
        result = 31 * result + getTwoFactorEnabled().hashCode();
        result = 31 * result + getStatus().hashCode();
        result = 31 * result + getCollections().hashCode();
        result = 31 * result + getResetPasswordEnrolled().hashCode();
        result = 31 * result + getSsoExternalId().hashCode();
        result = 31 * result + getType().hashCode();
        result = 31 * result + getExternalId().hashCode();
        result = 31 * result + getGroups().hashCode();
        return result;
    }
}
