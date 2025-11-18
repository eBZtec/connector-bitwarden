package br.tec.ebz.connid.connector.bitwarden;

import br.tec.ebz.connid.connector.bitwarden.processing.CollectionsProcessing;
import br.tec.ebz.connid.connector.bitwarden.schema.CollectionSchemaAttributes;
import org.identityconnectors.framework.api.ConnectorFacade;
import org.identityconnectors.framework.common.objects.AttributeDelta;
import org.identityconnectors.framework.common.objects.AttributeDeltaBuilder;
import org.identityconnectors.framework.common.objects.Uid;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import static org.junit.jupiter.api.Assumptions.assumeTrue;

public class UpdateCollectionTest extends BitwardenConfigurationHandler{

    private ConnectorFacade facade;
    private ListResultHandler handler;
    private String collectionID;
    private String externalId;

    @BeforeEach
    public void init() {
        int randomCode = new Random().nextInt(1000);
        externalId = "update" + randomCode;

        facade = getTestConnection();
        handler = new ListResultHandler();
        collectionID = env("BW_COLLECTION_ID");
        assumeTrue(collectionID != null && !collectionID.isBlank(), "BW_COLLECTION_ID not set");
    }

    @Test
    public void should_update_a_collection() {
        Uid uid = new Uid(collectionID);
        Set<AttributeDelta> deltaAttributes = new HashSet<AttributeDelta>();
        AttributeDeltaBuilder builder = new AttributeDeltaBuilder();
        builder.setName(CollectionSchemaAttributes.EXTERNAL_ID);
        builder.addValueToReplace(externalId);

        deltaAttributes.add(builder.build());

        facade.updateDelta(CollectionsProcessing.OBJECT_CLASS, uid, deltaAttributes, null);
    }
}
