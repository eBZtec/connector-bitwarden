package br.tec.ebz.connid.connector.bitwarden.processing;

import br.tec.ebz.connid.connector.bitwarden.schema.GroupSchemaAttributes;
import org.identityconnectors.common.logging.Log;
import org.identityconnectors.common.security.GuardedString;
import org.identityconnectors.framework.common.exceptions.InvalidAttributeValueException;
import org.identityconnectors.framework.common.exceptions.UnknownUidException;
import org.identityconnectors.framework.common.objects.*;

import java.util.*;

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

        if (oldObject == null) throw new UnknownUidException("Uid \"" + uid.getUidValue() + "\" not found");

        final String COLLECTIONS = GroupSchemaAttributes.COLLECTIONS; // "collections"

        Set<Attribute> processedAttrs = new HashSet<>();
        Map<String, Flags> coll = new LinkedHashMap<>();

        for (Attribute a : oldObject.getAttributes()) {
            if (a != null && COLLECTIONS.equals(a.getName())) {
                coll.putAll(parseCollections(a.getValue()));
            } else if (a != null) {
                processedAttrs.add(a);
            }
        }

        Set<Attribute> replaces = new HashSet<>();
        Set<Attribute> removes  = new HashSet<>();
        Set<Attribute> adds     = new HashSet<>();

        attributes.forEach(delta -> {

            List<Object> valuesToReplace = delta.getValuesToReplace();
            List<Object> valuesToRemove = delta.getValuesToRemove();
            List<Object> valuesToAdd = delta.getValuesToAdd();

            if (valuesToReplace != null) {
                replaces.add(AttributeBuilder.build(delta.getName(), valuesToReplace));
            }
            if (valuesToRemove != null) {
                removes.add(AttributeBuilder.build(delta.getName(), valuesToRemove));
            }

            if (valuesToAdd != null) {
                adds.add(AttributeBuilder.build(delta.getName(), valuesToAdd));
            }
        });


        LOG.info("ADD Entries {0}", adds);
        LOG.info("REPLACE Entries {0}", replaces);
        LOG.info("REMOVE Entries {0}", removes);


        for (Attribute remAttr : removes) {
            if (COLLECTIONS.equals(remAttr.getName())) {
                Set<String> idsToRemove = parseIds(remAttr.getValue());
                idsToRemove.forEach(coll::remove);
            } else {
                Attribute oldAttr = AttributeUtil.find(remAttr.getName(), processedAttrs);
                if (oldAttr != null) {
                    List<Object> values = new ArrayList<>();
                    if (oldAttr.getValue() != null) values.addAll(oldAttr.getValue());
                    if (remAttr.getValue() != null) values.removeAll(remAttr.getValue());
                    processedAttrs.remove(oldAttr);
                    processedAttrs.add(AttributeBuilder.build(oldAttr.getName(), values));
                }
            }
        }

        for (Attribute newAttr : replaces) {
            if (COLLECTIONS.equals(newAttr.getName())) {
                coll.clear();
                coll.putAll(parseCollections(newAttr.getValue()));
            } else {
                Attribute oldAttr = AttributeUtil.find(newAttr.getName(), processedAttrs);
                if (oldAttr != null) processedAttrs.remove(oldAttr);
                processedAttrs.add(newAttr);
            }
        }

        for (Attribute addAttr : adds) {
            if (COLLECTIONS.equals(addAttr.getName())) {
                LOG.info("Processing entry {0}", addAttr.getValue());
                Map<String, Flags> toAdd = parseCollections(addAttr.getValue());
                for (Map.Entry<String, Flags> e : toAdd.entrySet()) {
                    coll.put(e.getKey(), e.getValue());
                }
            } else {
                Attribute oldAttr = AttributeUtil.find(addAttr.getName(), processedAttrs);
                if (oldAttr != null) {
                    List<Object> values = new ArrayList<>();
                    if (oldAttr.getValue() != null) values.addAll(oldAttr.getValue());
                    if (addAttr.getValue() != null)  values.addAll(addAttr.getValue());
                    processedAttrs.remove(oldAttr);
                    processedAttrs.add(AttributeBuilder.build(oldAttr.getName(), values));
                } else {
                    processedAttrs.add(addAttr);
                }
            }
        }

        processedAttrs.add(AttributeBuilder.build(COLLECTIONS, serializeCollections(coll)));

        return processedAttrs;
    }

    private static List<String> serializeCollections(Map<String, Flags> map) {
        List<String> out = new ArrayList<>(map.size());
        for (Map.Entry<String, Flags> e : map.entrySet()) {
            out.add(formatOne(e.getKey(), e.getValue()));
        }
        return out;
    }

    private static String formatOne(String id, Flags f) {
        return "id=" + id
                + ";ro=" + (f.ro ? "1" : "0")
                + ";hp=" + (f.hp ? "1" : "0")
                + ";mg=" + (f.mg ? "1" : "0");
    }

    private static final class Flags {
        boolean ro;
        boolean hp;
        boolean mg;
        Flags() {}
        Flags(boolean ro, boolean hp, boolean mg) { this.ro = ro; this.hp = hp; this.mg = mg; }
    }

    private static Map<String, Flags> parseCollections(List<Object> rawValues) {
        Map<String, Flags> map = new LinkedHashMap<>();
        if (rawValues == null) return map;
        for (Object o : rawValues) {
            if (o == null) continue;
            String s = String.valueOf(o).trim();
            if (s.isEmpty()) continue;
            Parsed p = parseOne(s);
            if (p != null && p.id != null && !p.id.isEmpty()) {
                map.put(p.id, new Flags(p.ro, p.hp, p.mg));
            }
        }
        return map;
    }

    private static final class Parsed {
        String id; boolean ro; boolean hp; boolean mg;
    }

    private static Set<String> parseIds(List<Object> rawValues) {
        Set<String> ids = new java.util.LinkedHashSet<>();
        if (rawValues == null) return ids;
        for (Object o : rawValues) {
            if (o == null) continue;
            String s = String.valueOf(o).trim();
            if (s.isEmpty()) continue;
            Parsed p = parseOne(s);
            if (p != null && p.id != null && !p.id.isEmpty()) {
                ids.add(p.id);
            } else {
                String id = extractIdLoose(s);
                if (id != null && !id.isEmpty()) ids.add(id);
            }
        }
        return ids;
    }

    private static Parsed parseOne(String s) {
        Parsed p = new Parsed();
        String[] parts = s.split(";");
        for (String part : parts) {
            String kv = part.trim();
            if (kv.isEmpty()) continue;
            int i = kv.indexOf('=');
            if (i < 0) continue;
            String k = kv.substring(0, i).trim();
            String v = kv.substring(i + 1).trim();
            switch (k) {
                case "id" -> p.id = v;
                case "ro" -> p.ro = "1".equals(v) || "true".equalsIgnoreCase(v);
                case "hp" -> p.hp = "1".equals(v) || "true".equalsIgnoreCase(v);
                case "mg" -> p.mg = "1".equals(v) || "true".equalsIgnoreCase(v);
                default -> { }
            }
        }
        return p.id != null ? p : null;
    }

    private static String extractIdLoose(String s) {
        int i = s.indexOf("id=");
        if (i >= 0) {
            String rest = s.substring(i + 3).trim();
            int sc = rest.indexOf(';');
            return sc >= 0 ? rest.substring(0, sc).trim() : rest;
        }
        if (s.matches("(?i)^[0-9a-f\\-]{8,}$")) return s;
        return null;
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
