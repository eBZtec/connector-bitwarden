package br.tec.ebz.connid.connector.bitwarden.processing;

import br.tec.ebz.connid.connector.bitwarden.utils.AttributeValueNormalizer;
import org.identityconnectors.common.logging.Log;
import org.identityconnectors.common.security.GuardedString;
import org.identityconnectors.framework.common.exceptions.InvalidAttributeValueException;
import org.identityconnectors.framework.common.exceptions.UnknownUidException;
import org.identityconnectors.framework.common.objects.*;

import java.util.*;
import java.util.stream.Collectors;

public abstract class ObjectProcessing {
    private static final Log LOG = Log.getLog(ObjectProcessing.class);

    protected static <T> T getAttributeValue(String name, Class<T> type, Set<Attribute> attributes) {
        LOG.ok("Processing attribute {0} of the type {1}", name, type.toString());

        Attribute attr = AttributeUtil.find(name, attributes);

        if (attr == null) {
            return null;
        }

        if (String.class.equals(type)) {
            return (T) AttributeUtil.getStringValue(attr);
        } else if (Long.class.equals(type)) {
            return (T) AttributeUtil.getLongValue(attr);
        } else if (Integer.class.equals(type)) {
            return (T) AttributeUtil.getIntegerValue(attr);
        } else if (GuardedString.class.equals(type)) {
            return (T) AttributeUtil.getGuardedStringValue(attr);
        } else if (Boolean.class.equals(type)) {
            return (T) AttributeUtil.getBooleanValue(attr);
        } else if (List.class.equals(type)) {
            return (T) attr.getValue();
        } else if(Date.class.equals(type)) {
            return (T) AttributeUtil.getDateValue(attr);
        } else {
            throw new InvalidAttributeValueException("Unknown value type " + type);
        }
    }

    protected void addAttribute(ConnectorObjectBuilder builder, String attrName, Object value) {
        LOG.info("Processing attribute {0} with value(s) {1}", attrName, value);

        if (value == null) {
            return;
        }

        AttributeBuilder attributeBuilder = new AttributeBuilder();
        attributeBuilder.setName(attrName);

        if (value instanceof Collection) {
            attributeBuilder.addValue((Collection<?>) value);
        } else {
            attributeBuilder.addValue(value);
        }

        builder.addAttribute(attributeBuilder.build());
    }

    protected Set<Attribute> updateObjectAttributes(Uid uid, Set<AttributeDelta> attributes, ConnectorObject oldObject) {

        if (oldObject == null) throw new UnknownUidException("Uid \"" + uid.getUidValue() + "\" não encontrado");

        Set<Attribute> processedAttrs = new HashSet<>();
        Set<Attribute> attrsToReplace = new HashSet<>();
        Set<Attribute> attrsToRemove = new HashSet<>();
        Set<Attribute> attrsToAdd = new HashSet<>();

        attributes.forEach(delta -> {

            List<Object> valuesToReplace = delta.getValuesToReplace();
            List<Object> valuesToRemove = delta.getValuesToRemove();
            List<Object> valuesToAdd = delta.getValuesToAdd();

            if (valuesToReplace != null) {
                attrsToReplace.add(AttributeBuilder.build(delta.getName(), valuesToReplace));
            } else if (valuesToRemove != null) {
                attrsToRemove.add(AttributeBuilder.build(delta.getName(), valuesToRemove));
            } else if (valuesToAdd != null) {
                attrsToAdd.add(AttributeBuilder.build(delta.getName(), valuesToAdd));
            }
        });

        processedAttrs.addAll(oldObject.getAttributes());

        if (!attrsToReplace.isEmpty()) {
            attrsToReplace.forEach(newAttr -> {

                Attribute oldAttr = AttributeUtil.find(newAttr.getName(), processedAttrs);
                if (oldAttr != null) {
                    processedAttrs.remove(oldAttr);
                }
                processedAttrs.add(newAttr);
            });
        }

        if (!attrsToAdd.isEmpty()) {
            attrsToAdd.forEach(newAttr -> {

                Attribute oldAttr = AttributeUtil.find(newAttr.getName(), processedAttrs);

                if (oldAttr != null) {
                    List values = new ArrayList();

                    if (oldAttr.getValue() != null) {

                        values.addAll(oldAttr.getValue());
                    }

                    values.addAll(newAttr.getValue());
                    processedAttrs.remove(oldAttr);
                    processedAttrs.add(AttributeBuilder.build(oldAttr.getName(), values));
                } else {

                    processedAttrs.add(newAttr);
                }
            });
        }

        if (!attrsToRemove.isEmpty()) {
            attrsToRemove.forEach(newAttr -> {

                Attribute oldAttr = AttributeUtil.find(newAttr.getName(), processedAttrs);
                if (oldAttr == null) return;

                List<Object> current = new ArrayList<>(Optional.ofNullable(oldAttr.getValue())
                        .orElseGet(List::of));

                List<Object> toRemove = new ArrayList<>(Optional.ofNullable(newAttr.getValue())
                        .orElseGet(List::of));

                Set<Object> removeKeys = toRemove.stream()
                        .map(AttributeValueNormalizer::keyOf)
                        .collect(Collectors.toSet());

                List<Object> filtered = current.stream()
                        .filter(v -> !removeKeys.contains(AttributeValueNormalizer.keyOf(v)))
                        .collect(Collectors.toList());

                processedAttrs.remove(oldAttr);
                processedAttrs.add(AttributeBuilder.build(oldAttr.getName(), filtered));
            });
        }

        return processedAttrs;
    }

    protected AttributeInfo buildAttributeInfo(String name, Class type, String nativeName, AttributeInfo.Flags... flags) {

        AttributeInfoBuilder aib = new AttributeInfoBuilder(name);
        aib.setType(type);

        if (nativeName == null) {
            nativeName = name;
        }

        aib.setNativeName(nativeName);

        if (flags.length != 0) {
            Set<AttributeInfo.Flags> set = new HashSet<>();
            set.addAll(Arrays.asList(flags));
            aib.setFlags(set);
        }

        return aib.build();
    }
}
