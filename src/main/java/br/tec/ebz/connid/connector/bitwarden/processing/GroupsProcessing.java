package br.tec.ebz.connid.connector.bitwarden.processing;

import br.tec.ebz.connid.connector.bitwarden.entities.BitwardenAccess;
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

import java.util.*;
import java.util.stream.Collectors;

public class GroupsProcessing extends ObjectProcessing{

    private static final Log LOG = Log.getLog(MemberProcessing.class);

    public static final String OBJECT_CLASS_NAME = "group";
    public static final ObjectClass OBJECT_CLASS = new ObjectClass(OBJECT_CLASS_NAME);

    public static final String ACCESS_CLASS_NAME = "COLLECTION_ACCESS";
    public static final ObjectClass ACCESS_OBJECT_CLASS = new ObjectClass(ACCESS_CLASS_NAME);

    public static final String COLLECTION_ACCESS_CLASS_NAME = "collection_access";

    private final GroupsService groupsService;
    private final MembersService membersService;

    public GroupsProcessing(GroupsService groupsService, MembersService membersService) {
        this.groupsService = groupsService;
        this.membersService = membersService;
    }

    public Uid create(Set<Attribute> attributes, OperationOptions options) {
        BitwardenGroup newGroup = translate(attributes);

        LOG.ok("New Group request {0}", newGroup);

        BitwardenGroup group = groupsService.create(newGroup);

        LOG.ok("Group \"{0}\" created successfully.", group.getId());
        return new Uid(group.getId());
    }

    public void update(Uid uid, Set<AttributeDelta> modifications, OperationOptions options) {
        ConnectorObject currentObject = getObject(uid.getUidValue());
        Set<Attribute> updatedAttributes = updateObjectAttributes(uid, modifications, currentObject);
        BitwardenGroup group = translate(updatedAttributes);

        LOG.info("Updated Group {0}.", group);

        groupsService.update(uid.getUidValue(), group);
        LOG.ok("Group \"{0}\" updated successfully.", uid.getUidValue());
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

        List<String> collections = new ArrayList<>();
        for (BitwardenAccess collection: group.getCollections()) {
            collections.add(collection.toString());
        }

        addAttribute(connectorObject, GroupSchemaAttributes.COLLECTIONS, collections);

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
        group.setCollections(getCollectionsAccesses(attributes));

        return group;
    }

    private static List<BitwardenAccess> getCollectionsAccesses(Set<Attribute> attributes) {
        AttributesAccessor accessor = new AttributesAccessor(attributes);

        List<BitwardenAccess> collectionsAccesses = new ArrayList<>();
        Attribute collections = accessor.find(GroupSchemaAttributes.COLLECTIONS);

        if (collections != null && collections.getValue() != null) {
            for (Object v : collections.getValue()) {
                String collection = (String) v;
                Map<String,String> m = Arrays.stream(collection.split(";"))
                        .map(p -> p.split("=",2))
                        .collect(Collectors.toMap(a->a[0], a->a[1]));
                BitwardenAccess access = new BitwardenAccess();
                access.setId(m.get("id"));
                access.setReadOnly("1".equals(m.getOrDefault("ro","0")));
                access.setHidePassword("1".equals(m.getOrDefault("hp","0")));
                access.setManage("1".equals(m.getOrDefault("mg","0")));

                collectionsAccesses.add(access);

            }
        }

        return collectionsAccesses;
    }

    public ObjectClassInfo schema() {
        ObjectClassInfoBuilder objectClassInfoBuilder = new ObjectClassInfoBuilder();
        objectClassInfoBuilder.setType(GroupsProcessing.OBJECT_CLASS_NAME);

        objectClassInfoBuilder.addAttributeInfo(
                buildAttributeInfo(
                        Uid.NAME,
                        String.class,
                        GroupSchemaAttributes.ID,
                        AttributeInfo.Flags.REQUIRED,
                        AttributeInfo.Flags.NOT_UPDATEABLE
                )
        );

        objectClassInfoBuilder.addAttributeInfo(
                buildAttributeInfo(
                        Name.NAME,
                        String.class,
                        GroupSchemaAttributes.NAME
                )
        );

        objectClassInfoBuilder.addAttributeInfo(
                buildAttributeInfo(
                        GroupSchemaAttributes.EXTERNAL_ID,
                        String.class,
                        null
                )
        );

        objectClassInfoBuilder.addAttributeInfo(
                buildAttributeInfo(
                        GroupSchemaAttributes.COLLECTIONS,
                        String.class,
                        null,
                        AttributeInfo.Flags.MULTIVALUED
                )
        );

        objectClassInfoBuilder.addAttributeInfo(
                buildAttributeInfo(
                        GroupSchemaAttributes.MEMBERS,
                        String.class,
                        null,
                        AttributeInfo.Flags.MULTIVALUED
                )
        );


        return objectClassInfoBuilder.build();
    }
}
