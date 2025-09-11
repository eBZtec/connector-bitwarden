package br.tec.ebz.connid.connector.bitwarden.processing;

import br.tec.ebz.connid.connector.bitwarden.schema.CollectionAccessSchemaAttributes;
import org.identityconnectors.framework.common.objects.*;

public class CollectionAccessProcessing {

    public static final String OBJECT_CLASS_NAME = "collection_access";
    public static final ObjectClass OBJECT_CLASS = new ObjectClass(OBJECT_CLASS_NAME);

    public static ObjectClassInfo schema() {
        ObjectClassInfoBuilder accessBld = new ObjectClassInfoBuilder()
                .setType(CollectionAccessProcessing.OBJECT_CLASS_NAME);
        accessBld.setEmbedded(true);
        accessBld.addAttributeInfo(AttributeInfoBuilder.build(Uid.NAME, String.class));
        accessBld.addAttributeInfo(AttributeInfoBuilder.build(Name.NAME, String.class));
        accessBld.addAttributeInfo(AttributeInfoBuilder.build(CollectionAccessSchemaAttributes.ID, String.class));
        accessBld.addAttributeInfo(AttributeInfoBuilder.build(CollectionAccessSchemaAttributes.READ_ONLY, Boolean.class));
        accessBld.addAttributeInfo(AttributeInfoBuilder.build(CollectionAccessSchemaAttributes.HIDE_PASSWORDS, Boolean.class));
        accessBld.addAttributeInfo(AttributeInfoBuilder.build(CollectionAccessSchemaAttributes.MANAGE, Boolean.class));
        return accessBld.build();
    }
}
