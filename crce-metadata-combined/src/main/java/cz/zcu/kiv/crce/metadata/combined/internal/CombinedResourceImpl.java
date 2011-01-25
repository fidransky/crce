package cz.zcu.kiv.crce.metadata.combined.internal;

import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.combined.CombinedResource;
import cz.zcu.kiv.crce.metadata.Property;
import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.Type;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.osgi.framework.Version;

// TODO specifi exact write behavior
/**
 *
 * @author kalwi
 */
public class CombinedResourceImpl implements CombinedResource {

    private Resource m_staticResource;
    private Resource m_writableResource;

    public CombinedResourceImpl(Resource staticResource, Resource writableResource) {
        m_staticResource = staticResource;
        m_writableResource = writableResource;
    }

    @Override
    public String getId() {
        return m_staticResource.getId() != null ? m_staticResource.getId() : m_writableResource.getId();
    }

    @Override
    public String getSymbolicName() {
        return m_staticResource.getSymbolicName() != null ? m_staticResource.getSymbolicName() : m_writableResource.getSymbolicName();
    }

    @Override
    public Version getVersion() {
        return !"0.0.0".equals(m_staticResource.getVersion().toString()) ? m_staticResource.getVersion() : m_writableResource.getVersion();
    }

    @Override
    public String getPresentationName() {
        return m_staticResource.getPresentationName() != null ? m_staticResource.getPresentationName() : m_writableResource.getPresentationName();
    }

    @Override
    public URI getUri() {
        return m_staticResource.getUri() != null ? m_staticResource.getUri() : m_writableResource.getUri();
    }

    @Override
    public long getSize() {
        return m_staticResource.getSize();
    }

    @Override
    public String[] getCategories() {
        return concat(m_staticResource.getCategories(), m_writableResource.getCategories());
    }

    @Override
    public Capability[] getCapabilities() {
        return concat(m_staticResource.getCapabilities(), m_writableResource.getCapabilities());
    }

    @Override
    public Requirement[] getRequirements() {
        return concat(m_staticResource.getRequirements(), m_writableResource.getRequirements());
    }

    @Override
    public Map<String, String> getPropertiesMap() {
        Map<String, String> out = new HashMap<String, String>();
        Map<String, String> tmp = m_writableResource.getPropertiesMap();
        for (String key : tmp.keySet()) {
            out.put(key, tmp.get(key));
        }
        tmp = m_staticResource.getPropertiesMap();
        for (String key : tmp.keySet()) {
            out.put(key, tmp.get(key));
        }
        return out;
    }

    @Override
    public boolean hasCategory(String category) {
        return m_staticResource.hasCategory(category) || m_writableResource.hasCategory(category);
    }

    @Override
    public void setSymbolicName(String name) {
        if (m_staticResource.getSymbolicName() == null) {
            m_writableResource.setSymbolicName(name);
        }
    }

    @Override
    public void setVersion(Version version) {
        if ("0.0.0".equals(m_staticResource.getVersion().toString()) && m_staticResource.isWritable()) {
            m_writableResource.setVersion(version);
        }
    }

    @Override
    public void setVersion(String version) {
        if ("0.0.0".equals(m_staticResource.getVersion().toString()) && m_staticResource.isWritable()) {
            m_writableResource.setVersion(version);
        }
    }

    @Override
    public void addCategory(String category) {
        if (!m_staticResource.hasCategory(category)) {
            m_writableResource.addCategory(category);
        }
    }

    @Override
    public void addCapability(Capability capability) {
        if (!m_staticResource.hasCapability(capability)) {
            m_writableResource.addCapability(capability);
        }
    }

    @Override
    public void addRequirement(Requirement requirement) {
        if (!m_staticResource.hasRequirement(requirement)) {
            m_writableResource.addRequirement(requirement);
        }
    }

    @Override
    public Capability createCapability(String name) {
        return m_writableResource.createCapability(name);
    }

    @Override
    public Requirement createRequirement(String name) {
        return m_writableResource.createRequirement(name);
    }

    @Override
    public boolean hasCapability(Capability capability) {
        return m_staticResource.hasCapability(capability) || m_writableResource.hasCapability(capability);
    }

    @Override
    public boolean hasRequirement(Requirement requirement) {
        return m_staticResource.hasRequirement(requirement) || m_writableResource.hasRequirement(requirement);
    }

    public static <T> T[] concat(T[] first, T[] second) {
        T[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

    public Resource getWritableResource() {
        return m_writableResource;
    }

    @Override
    public Resource getStaticResource() {
        return m_staticResource;
    }

    @Override
    public boolean isWritable() {
        return m_staticResource.isWritable() || m_writableResource.isWritable();
    }

    @Override
    public void setPresentationName(String name) {
        m_writableResource.setPresentationName(name);   // TODO check static p.n.?
    }

    @Override
    public void setSize(long size) {
        if (m_staticResource.getSize() < 0) {
            m_writableResource.setSize(size);
        }
    }

    @Override
    public void setUri(URI uri) {
        // TODO - je dobre, aby tohle bylo takto implementovano?
        if (m_staticResource.getUri() == null) {
            m_writableResource.setUri(uri);
        }
    }

    @Override
    public Property[] getProperties() {
        Set<Property> set = new HashSet<Property>();
        set.addAll(Arrays.asList(m_writableResource.getProperties()));
        set.addAll(Arrays.asList(m_staticResource.getProperties()));
        return set.toArray(new Property[set.size()]);
//        return concat(m_staticResource.getProperties(), m_writableResource.getProperties());
    }

    @Override
    public Property getProperty(String name) {
        Property property = m_staticResource.getProperty(name);
        if (property == null) {
            property = m_writableResource.getProperty(name);
        }
        return property;
    }

    @Override
    public void setProperty(Property property) {
        if (m_staticResource.getProperty(property.getName()) == null) {
            m_writableResource.setProperty(property);
        }
    }

    @Override
    public void setProperty(String name, String value, Type type) {
        if (m_staticResource.getProperty(name) == null) {
            m_writableResource.setProperty(name, value, type);
        }
    }

    @Override
    public void setProperty(String name, String string) {
        if (m_staticResource.getProperty(name) == null) {
            m_writableResource.setProperty(name, string);
        }
    }

    @Override
    public void setProperty(String name, Version version) {
        if (m_staticResource.getProperty(name) == null) {
            m_writableResource.setProperty(name, version);
        }
    }

    @Override
    public void setProperty(String name, URL url) {
        if (m_staticResource.getProperty(name) == null) {
            m_writableResource.setProperty(name, url);
        }
    }

    @Override
    public void setProperty(String name, URI uri) {
        if (m_staticResource.getProperty(name) == null) {
            m_writableResource.setProperty(name, uri);
        }
    }

    @Override
    public void setProperty(String name, long llong) {
        if (m_staticResource.getProperty(name) == null) {
            m_writableResource.setProperty(name, llong);
        }
    }

    @Override
    public void setProperty(String name, double ddouble) {
        if (m_staticResource.getProperty(name) == null) {
            m_writableResource.setProperty(name, ddouble);
        }
    }

    @Override
    public void setProperty(String name, Set values) {
        if (m_staticResource.getProperty(name) == null) {
            m_writableResource.setProperty(name, values);
        }
    }

    @Override
    public void unsetProperty(String name) {
        m_writableResource.unsetProperty(name);
    }

    @Override
    public String getPropertyString(String name) {
        String str = m_staticResource.getPropertyString(name);
        return str != null ? str : m_writableResource.getPropertyString(name);
    }

    @Override
    public String toString() {
        return "[" + m_staticResource.toString() + ", " + m_writableResource.toString() + "]";
    }

    @Override
    public String asString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Static resource:\n");
        sb.append(m_staticResource.asString());
        sb.append("\nWritable resource:\n");
        sb.append(m_writableResource.asString());
        return sb.toString();
    }

    @Override
    public void unsetWritable() {
        // TODO - check whether no action is correct
    }
}