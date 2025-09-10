package br.tec.ebz.connid.connector.bitwarden.processing;

import br.tec.ebz.connid.connector.bitwarden.entities.BitwardenGroup;
import br.tec.ebz.connid.connector.bitwarden.entities.BitwardenListResponse;
import br.tec.ebz.connid.connector.bitwarden.schema.GroupSchemaAttributes;
import br.tec.ebz.connid.connector.bitwarden.services.GroupsService;
import br.tec.ebz.connid.connector.bitwarden.services.MembersService;
import org.identityconnectors.common.logging.Log;
import org.identityconnectors.framework.common.exceptions.UnknownUidException;
import org.identityconnectors.framework.common.objects.*;
import org.identityconnectors.framework.common.objects.filter.ContainsAllValuesFilter;
import org.identityconnectors.framework.common.objects.filter.EqualsFilter;
import org.identityconnectors.framework.common.objects.filter.Filter;

import java.util.List;
import java.util.Set;

public class GroupsProcessing extends ObjectProcessing{

    private static final Log LOG = Log.getLog(MemberProcessing.class);

    public static final String OBJECT_CLASS_NAME = "group";
    public static final ObjectClass OBJECT_CLASS = new ObjectClass(OBJECT_CLASS_NAME);

    private final GroupsService groupsService;
    private final MembersService membersService;

    public GroupsProcessing(GroupsService groupsService, MembersService membersService) {
        this.groupsService = groupsService;
        this.membersService = membersService;
    }

    public Uid create(Set<Attribute> attributes, OperationOptions options) {
        BitwardenGroup newGroup = translate(attributes);

        BitwardenGroup group = groupsService.create(newGroup);

        LOG.ok("Group \"{0}\" created successfully.", group.getId());
        return new Uid(group.getId());
    }

    public void delete(Uid uid, OperationOptions options) {
        groupsService.delete(uid.getUidValue());

        LOG.ok("Group \"{0}\" deleted successfully", uid.getUidValue());
    }

    public void search(Filter query, ResultsHandler handler, OperationOptions options) {
        if (query == null) {
            searchAll(handler, options);
        } else {
            searchByFilter(query, handler, options);
        }
    }

    private void searchAll(ResultsHandler handler, OperationOptions options) {
        BitwardenListResponse<BitwardenGroup> groups = groupsService.list();

        LOG.info("Found {0} groups", groups.getData().size());

        for (BitwardenGroup group: groups.getData()) {
            handler.handle(translate(group));
        }
    }

    private void searchByFilter(Filter query, ResultsHandler handler, OperationOptions options) {
        if (query instanceof EqualsFilter equalsFilter) {
            Attribute attribute = equalsFilter.getAttribute();

            if (attribute != null) {
                String attributeName = attribute.getName();
                List<Object> attributeValues = attribute.getValue();

                if (!attributeName.equals(Uid.NAME)) throw new UnsupportedOperationException("Could not search, reason: attribute " + attributeName + " is not supported by search operation");

                if (attributeValues.size() != 1) throw new UnsupportedOperationException("Could not search, reason: search attribute must have only one value. Found " + attributeValues.size());
                String id = String.valueOf(attributeValues.get(0));

                handler.handle(getObject(id));
                LOG.ok("Member \"{0}\" was found.", id);
            }
        } else if (query instanceof ContainsAllValuesFilter containsAllValuesFilter) {
            Attribute attribute = containsAllValuesFilter.getAttribute();

            if (attribute != null) {
                String attributeName = attribute.getName();
                List<Object> attributeValues = attribute.getValue();

                if (!attributeName.equals(GroupSchemaAttributes.MEMBERS))
                    throw new UnsupportedOperationException("Could not search member, reason: attribute " + attributeName + " is not supported by contains all values filter");

                if (attributeValues.size() != 1)
                    throw new UnsupportedOperationException("Could not search member, reason: search attribute must have only one value. Found " + attributeValues.size());

                String id = String.valueOf(attributeValues.get(0));
                List<String> memberGroupsIds = membersService.getMemberGroups(id);

                for (String groupId: memberGroupsIds) {
                    handler.handle(getObject(groupId));
                }

                LOG.ok("Found {0} groups for user {1}", memberGroupsIds.size(), id);

            }
        } else {
            throw new UnsupportedOperationException("Filter " + query + " is not supported.");
        }
    }

    private ConnectorObject getObject(String id) {
        BitwardenGroup group = groupsService.get(id);

        if (group == null) throw new UnknownUidException("Group \"" + id + "\" not found.");

        List<String> members = groupsService.getGroupMembers(id);
        group.setMembers(members);

        LOG.ok("Found \"{0}\" group", group);
        return translate(group);
    }

    private ConnectorObject translate(BitwardenGroup group) {
        ConnectorObjectBuilder connectorObject = new ConnectorObjectBuilder();
        connectorObject.setObjectClass(GroupsProcessing.OBJECT_CLASS);

        addAttribute(connectorObject, Uid.NAME, group.getId());
        addAttribute(connectorObject, Name.NAME, group.getName());
        addAttribute(connectorObject, GroupSchemaAttributes.EXTERNAL_ID, group.getExternalId());
        addAttribute(connectorObject, GroupSchemaAttributes.MEMBERS, group.getMembers());

        return connectorObject.build();
    }

    private BitwardenGroup translate(Set<Attribute> attributes) {
        String id = getAttributeValue(Uid.NAME, String.class, attributes);
        String name = getAttributeValue(Name.NAME, String.class, attributes);
        String externalId = getAttributeValue(GroupSchemaAttributes.EXTERNAL_ID, String.class, attributes);
        List<String> members = getAttributeValue(GroupSchemaAttributes.MEMBERS, List.class, attributes);

        BitwardenGroup group = new BitwardenGroup();

        if (id != null) group.setId(id);

        group.setName(name);
        group.setExternalId(externalId);
        group.setMembers(members);
        group.setObject("group");

        return group;
    }
}
