package br.tec.ebz.connid.connector.bitwarden.processing;

import br.tec.ebz.connid.connector.bitwarden.entities.BitwardenAccess;
import br.tec.ebz.connid.connector.bitwarden.entities.BitwardenCollection;
import br.tec.ebz.connid.connector.bitwarden.entities.BitwardenListResponse;
import br.tec.ebz.connid.connector.bitwarden.schema.AccessSchemaAttributes;
import br.tec.ebz.connid.connector.bitwarden.schema.CollectionSchemaAttributes;
import br.tec.ebz.connid.connector.bitwarden.services.CollectionsService;
import org.identityconnectors.common.logging.Log;
import org.identityconnectors.framework.common.exceptions.UnknownUidException;
import org.identityconnectors.framework.common.objects.*;
import org.identityconnectors.framework.common.objects.filter.EqualsFilter;
import org.identityconnectors.framework.common.objects.filter.Filter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CollectionsProcessing extends ObjectProcessing {
    private static final Log LOG = Log.getLog(CollectionsProcessing.class);

    public static final String OBJECT_CLASS_NAME = "collection";
    public static final ObjectClass OBJECT_CLASS = new ObjectClass(OBJECT_CLASS_NAME);

    public static final String ACCESS_CLASS_NAME = "GROUP_ACCESS";
    public static final ObjectClass ACCESS_OBJECT_CLASS = new ObjectClass(ACCESS_CLASS_NAME);

    private final CollectionsService collectionsService;

    public  CollectionsProcessing(CollectionsService collectionsService) {
        this.collectionsService = collectionsService;
    }

    public void update(Uid uid, Set<AttributeDelta> modifications, OperationOptions options) {
        ConnectorObject currentObject = getObject(uid.getUidValue());
        Set<Attribute> updatedAttributes = updateObjectAttributes(uid, modifications, currentObject);
        BitwardenCollection collection = translate(updatedAttributes);

        LOG.info("Updated collection {0}", collection);
        collectionsService.update(uid.getUidValue(), collection);
        LOG.ok("Collection \"{0}\" updated successfully.", uid.getUidValue());
    }

    public void delete(Uid uid, OperationOptions options) {
        collectionsService.delete(uid.getUidValue());

        LOG.ok("Group \"{0}\" deleted successfully", uid.getUidValue());
    }

    public void search(Filter query, ResultsHandler handler, OperationOptions options) {
        if (query == null) {
            searchAll(handler, options);
        } else {
            searchByFilter(query, handler, options);
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
                LOG.ok("Collection with id \"{0}\" was found.", id);
            }
        } else {
            throw new UnsupportedOperationException("Filter " + query + " is not supported.");
        }
    }

    private void searchAll(ResultsHandler handler, OperationOptions options) {
        BitwardenListResponse<BitwardenCollection> collections = collectionsService.list();

        LOG.info("Found {0} collections", collections.getData().size());

        for (BitwardenCollection collection: collections.getData()) {
            handler.handle(translate(collection));
        }

    }

    private ConnectorObject getObject(String id) {
        BitwardenCollection collection = collectionsService.get(id);

        if (collection == null) throw new UnknownUidException("Collection \"" + id + "\" not found.");

        LOG.ok("Found collection \"{0}\"", collection);
        return translate(collection);
    }

    private ConnectorObject translate(BitwardenCollection collection) {
        ConnectorObjectBuilder connectorObject = new ConnectorObjectBuilder();
        connectorObject.setObjectClass(CollectionsProcessing.OBJECT_CLASS);

        addAttribute(connectorObject, Uid.NAME, collection.getId());
        addAttribute(connectorObject, Name.NAME, collection.getId());
        addAttribute(connectorObject, CollectionSchemaAttributes.EXTERNAL_ID, collection.getExternalId());

        return connectorObject.build();
    }

    private static BitwardenCollection translate(Set<Attribute> attributes) {
        String id = getAttributeValue(Uid.NAME, String.class, attributes);
        String externalId = getAttributeValue(CollectionSchemaAttributes.EXTERNAL_ID, String.class, attributes);

        BitwardenCollection collection = new BitwardenCollection();

        if (id != null) collection.setId(id);

        collection.setExternalId(externalId);
        collection.setObject("collection");

        return collection;
    }

    public ObjectClassInfo schema() {
        ObjectClassInfoBuilder objectClassInfoBuilder = new ObjectClassInfoBuilder();
        objectClassInfoBuilder.setType(CollectionsProcessing.OBJECT_CLASS_NAME);

        objectClassInfoBuilder.addAttributeInfo(
                buildAttributeInfo(
                        Uid.NAME,
                        String.class,
                        CollectionSchemaAttributes.ID,
                        AttributeInfo.Flags.REQUIRED,
                        AttributeInfo.Flags.NOT_UPDATEABLE
                )
        );

        objectClassInfoBuilder.addAttributeInfo(
                buildAttributeInfo(
                        Name.NAME,
                        String.class,
                        null,
                        AttributeInfo.Flags.REQUIRED
                )
        );

        objectClassInfoBuilder.addAttributeInfo(
                buildAttributeInfo(
                        CollectionSchemaAttributes.EXTERNAL_ID,
                        String.class,
                        null
                )
        );

        return objectClassInfoBuilder.build();
    }
}
