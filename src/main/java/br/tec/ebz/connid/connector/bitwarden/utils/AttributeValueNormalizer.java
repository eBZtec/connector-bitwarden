package br.tec.ebz.connid.connector.bitwarden.utils;

import org.identityconnectors.framework.common.objects.*;

import java.util.*;
import java.util.stream.Collectors;

public final class AttributeValueNormalizer {
    public static Object keyOf(Object v) {
        if (v == null) return null;

        if (v instanceof ConnectorObjectReference) {
            BaseConnectorObject obj = ((ConnectorObjectReference) v).getValue();
            String oc = obj.getObjectClass().getObjectClassValue();
            Object id = extractId(obj.getAttributes());
            return List.of("REF", oc, id);
        }

        if (v instanceof byte[]) return Arrays.hashCode((byte[]) v);

        return v;
    }

    private static Object extractId(Set<Attribute> attrs) {
        Attribute uidAttr = AttributeUtil.getUidAttribute(attrs);
        if (uidAttr != null && uidAttr.getValue() != null && !uidAttr.getValue().isEmpty()) {
            return uidAttr.getValue().get(0);
        }
        Attribute n = AttributeUtil.find(Name.NAME, attrs);
        if (n != null && n.getValue() != null && !n.getValue().isEmpty()) {
            return n.getValue().get(0);
        }
        return attrs.stream()
                .sorted(Comparator.comparing(Attribute::getName))
                .map(att -> Map.entry(att.getName(), att.getValue()))
                .collect(Collectors.toList());
    }
}
