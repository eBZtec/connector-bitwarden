package br.tec.ebz.connid.connector.bitwarden.entities;

import java.util.ArrayList;
import java.util.List;

public class BiwardenUpdateMemberGroups {
    private List<String> groupIds;

    public List<String> getGroupIds() {
        if (groupIds == null) return new ArrayList<>();
        return groupIds;
    }

    public void setGroupIds(List<String> groupIds) {
        this.groupIds = groupIds;
    }
}
