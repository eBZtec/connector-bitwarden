package br.tec.ebz.connid.connector.bitwarden.processing;

import br.tec.ebz.connid.connector.bitwarden.entities.BitwardenGroup;
import br.tec.ebz.connid.connector.bitwarden.schema.GroupSchemaAttributes;
import br.tec.ebz.connid.connector.bitwarden.services.GroupsService;
import org.identityconnectors.common.logging.Log;
import org.identityconnectors.framework.common.objects.*;

import java.util.Set;

public class GroupsProcessing extends ObjectProcessing{

    private static final Log LOG = Log.getLog(MemberProcessing.class);

    public static final String OBJECT_CLASS_NAME = "group";
    public static final ObjectClass OBJECT_CLASS = new ObjectClass(OBJECT_CLASS_NAME);

    private final GroupsService groupsService;

    public GroupsProcessing(GroupsService groupssService) {
        this.groupsService = groupssService;
    }

    public Uid create(Set<Attribute> attributes, OperationOptions options) {
        BitwardenGroup newGroup = translate(attributes);

        BitwardenGroup group = groupsService.create(newGroup);

        LOG.ok("Group \"{0}\" created successfully.", group.getId());
        return new Uid(group.getId());
    }

    private BitwardenGroup translate(Set<Attribute> attributes) {
        String id = getAttributeValue(Uid.NAME, String.class, attributes);
        String name = getAttributeValue(Name.NAME, String.class, attributes);
        String externalId = getAttributeValue(GroupSchemaAttributes.EXTERNAL_ID, String.class, attributes);

        BitwardenGroup group = new BitwardenGroup();

        if (id != null) group.setId(id);

        group.setName(name);
        group.setExternalId(externalId);
        group.setObject("group");

        return group;
    }
}
