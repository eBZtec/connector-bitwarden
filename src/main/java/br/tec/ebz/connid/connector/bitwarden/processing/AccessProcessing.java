package br.tec.ebz.connid.connector.bitwarden.processing;

import br.tec.ebz.connid.connector.bitwarden.schema.AccessSchemaAttributes;
import org.identityconnectors.framework.common.objects.*;

public class AccessProcessing {

    public static ObjectClassInfo schema(String type) {
        ObjectClassInfoBuilder accessBld = new ObjectClassInfoBuilder()
                .setType(type);
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
