package br.tec.ebz.connid.connector.bitwarden.entities;

import java.util.Objects;

public class BitwardenPermissions {
    private Boolean accessEventsLogs;
    private Boolean accessImportExport;
    private Boolean accessReports;
    private Boolean createNewCollection;
    private Boolean editAnyCollection;
    private Boolean deleteAnyCollection;
    private Boolean manageGroups;
    private Boolean managePolicies;
    private Boolean manageSso;
    private Boolean manageUsers;
    private Boolean manageResetPassword;
    private Boolean manageScim;

    public Boolean getAccessEventsLogs() {
        return accessEventsLogs;
    }

    public void setAccessEventsLogs(Boolean accessEventsLogs) {
        this.accessEventsLogs = accessEventsLogs;
    }

    public Boolean getAccessImportExport() {

        if (accessEventsLogs == null) return false;

        return accessImportExport;
    }

    public void setAccessImportExport(Boolean accessImportExport) {
        this.accessImportExport = accessImportExport;
    }

    public Boolean getAccessReports() {
        if (accessReports == null) return false;
        return accessReports;
    }

    public void setAccessReports(Boolean accessReports) {
        this.accessReports = accessReports;
    }

    public Boolean getCreateNewCollection() {
        if (createNewCollection == null) return false;

        return createNewCollection;
    }

    public void setCreateNewCollection(Boolean createNewCollection) {
        this.createNewCollection = createNewCollection;
    }

    public Boolean getEditAnyCollection() {
        return editAnyCollection;
    }

    public void setEditAnyCollection(Boolean editAnyCollection) {
        this.editAnyCollection = editAnyCollection;
    }

    public Boolean getDeleteAnyCollection() {
        if (deleteAnyCollection == null) return false;

        return deleteAnyCollection;
    }

    public void setDeleteAnyCollection(Boolean deleteAnyCollection) {
        this.deleteAnyCollection = deleteAnyCollection;
    }

    public Boolean getManageGroups() {
        if (manageGroups == null) return false;
        return manageGroups;
    }

    public void setManageGroups(Boolean manageGroups) {
        this.manageGroups = manageGroups;
    }

    public Boolean getManagePolicies() {
        if (managePolicies == null) return false;
        return managePolicies;
    }

    public void setManagePolicies(Boolean managePolicies) {
        this.managePolicies = managePolicies;
    }

    public Boolean getManageSso() {
        if (manageSso == null) return false;
        return manageSso;
    }

    public void setManageSso(Boolean manageSso) {
        this.manageSso = manageSso;
    }

    public Boolean getManageUsers() {
        if (manageUsers == null) return false;
        return manageUsers;
    }

    public void setManageUsers(Boolean manageUsers) {
        this.manageUsers = manageUsers;
    }

    public Boolean getManageResetPassword() {
        if (manageResetPassword == null) return false;

        return manageResetPassword;
    }

    public void setManageResetPassword(Boolean manageResetPassword) {
        this.manageResetPassword = manageResetPassword;
    }

    public Boolean getManageScim() {
        if (manageScim == null) return false;
        return manageScim;
    }

    public void setManageScim(Boolean manageScim) {
        this.manageScim = manageScim;
    }

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof BitwardenPermissions that)) return false;

        return Objects.equals(getAccessEventsLogs(), that.getAccessEventsLogs()) && Objects.equals(getAccessImportExport(), that.getAccessImportExport()) && Objects.equals(getAccessReports(), that.getAccessReports()) && Objects.equals(getCreateNewCollection(), that.getCreateNewCollection()) && Objects.equals(getEditAnyCollection(), that.getEditAnyCollection()) && Objects.equals(getDeleteAnyCollection(), that.getDeleteAnyCollection()) && Objects.equals(getManageGroups(), that.getManageGroups()) && Objects.equals(getManagePolicies(), that.getManagePolicies()) && Objects.equals(getManageSso(), that.getManageSso()) && Objects.equals(getManageUsers(), that.getManageUsers()) && Objects.equals(getManageResetPassword(), that.getManageResetPassword()) && Objects.equals(getManageScim(), that.getManageScim());
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(getAccessEventsLogs());
        result = 31 * result + Objects.hashCode(getAccessImportExport());
        result = 31 * result + Objects.hashCode(getAccessReports());
        result = 31 * result + Objects.hashCode(getCreateNewCollection());
        result = 31 * result + Objects.hashCode(getEditAnyCollection());
        result = 31 * result + Objects.hashCode(getDeleteAnyCollection());
        result = 31 * result + Objects.hashCode(getManageGroups());
        result = 31 * result + Objects.hashCode(getManagePolicies());
        result = 31 * result + Objects.hashCode(getManageSso());
        result = 31 * result + Objects.hashCode(getManageUsers());
        result = 31 * result + Objects.hashCode(getManageResetPassword());
        result = 31 * result + Objects.hashCode(getManageScim());
        return result;
    }

    @Override
    public String toString() {
        return "BitwardenPermissions{" +
                "accessEventsLogs=" + accessEventsLogs +
                ", accessImportExport=" + accessImportExport +
                ", accessReports=" + accessReports +
                ", createNewCollection=" + createNewCollection +
                ", editAnyCollection=" + editAnyCollection +
                ", deleteAnyCollection=" + deleteAnyCollection +
                ", manageGroups=" + manageGroups +
                ", managePolicies=" + managePolicies +
                ", manageSso=" + manageSso +
                ", manageUsers=" + manageUsers +
                ", manageResetPassword=" + manageResetPassword +
                ", manageScim=" + manageScim +
                '}';
    }
}
