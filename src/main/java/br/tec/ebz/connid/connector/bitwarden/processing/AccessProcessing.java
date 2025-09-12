package br.tec.ebz.connid.connector.bitwarden.processing;

import br.tec.ebz.connid.connector.bitwarden.schema.AccessSchemaAttributes;
import org.identityconnectors.framework.common.objects.*;

public class AccessProcessing {

    public static final String OBJECT_CLASS_NAME = "access";
    public static final ObjectClass OBJECT_CLASS = new ObjectClass(OBJECT_CLASS_NAME);

    public static ObjectClassInfo schema() {
        ObjectClassInfoBuilder accessBld = new ObjectClassInfoBuilder()
                .setType(AccessProcessing.OBJECT_CLASS_NAME);
        accessBld.setEmbedded(true);
        accessBld.addAttributeInfo(AttributeInfoBuilder.build(Uid.NAME, String.class));
        accessBld.addAttributeInfo(AttributeInfoBuilder.build(Name.NAME, String.class));
        accessBld.addAttributeInfo(AttributeInfoBuilder.build(AccessSchemaAttributes.ID, String.class));
        accessBld.addAttributeInfo(AttributeInfoBuilder.build(AccessSchemaAttributes.READ_ONLY, Boolean.class));
        accessBld.addAttributeInfo(AttributeInfoBuilder.build(AccessSchemaAttributes.HIDE_PASSWORDS, Boolean.class));
        accessBld.addAttributeInfo(AttributeInfoBuilder.build(AccessSchemaAttributes.MANAGE, Boolean.class));
        return accessBld.build();
    }
}
