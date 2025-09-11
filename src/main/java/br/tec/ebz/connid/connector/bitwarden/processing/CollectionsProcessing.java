package br.tec.ebz.connid.connector.bitwarden.processing;

import br.tec.ebz.connid.connector.bitwarden.schema.MemberSchemaAttributes;
import org.identityconnectors.common.logging.Log;
import org.identityconnectors.framework.common.objects.*;

public class CollectionsProcessing extends ObjectProcessing {
    private static final Log LOG = Log.getLog(CollectionsProcessing.class);

    public static final String OBJECT_CLASS_NAME = "collection";
    public static final ObjectClass OBJECT_CLASS = new ObjectClass(OBJECT_CLASS_NAME);

    public ObjectClassInfo schema() {
        ObjectClassInfoBuilder objectClassInfoBuilder = new ObjectClassInfoBuilder();
        objectClassInfoBuilder.setType(CollectionsProcessing.OBJECT_CLASS_NAME);

        objectClassInfoBuilder.addAttributeInfo(
                buildAttributeInfo(
                        Uid.NAME,
                        String.class,
                        MemberSchemaAttributes.ID,
                        AttributeInfo.Flags.REQUIRED,
                        AttributeInfo.Flags.NOT_UPDATEABLE
                )
        );

        objectClassInfoBuilder.addAttributeInfo(
                buildAttributeInfo(
                        Name.NAME,
                        String.class,
                        MemberSchemaAttributes.EMAIL,
                        AttributeInfo.Flags.REQUIRED
                )
        );

        return objectClassInfoBuilder.build();
    }
}
