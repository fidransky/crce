package cz.zcu.kiv.crce.metadata.osgi.internal;

import static org.junit.Assert.*;

import java.lang.reflect.Field;
import java.util.List;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.Version;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import cz.zcu.kiv.crce.metadata.Attribute;
import cz.zcu.kiv.crce.metadata.Operator;
import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.ResourceFactory;
import cz.zcu.kiv.crce.metadata.impl.SimpleAttributeType;
import cz.zcu.kiv.crce.metadata.internal.ResourceFactoryImpl;
import cz.zcu.kiv.crce.metadata.osgi.namespace.NsOsgiPackage;
import cz.zcu.kiv.crce.metadata.osgi.util.FilterParser;

/**
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public class FilterParserTest {

    private static ResourceFactory resourceFactory;
    private static FilterParser filterParser;

    @BeforeClass
    public static void beforeClass() throws NoSuchFieldException, IllegalAccessException {
        resourceFactory = new ResourceFactoryImpl();
        filterParser = new FilterParserImpl();
        Field field = FilterParserImpl.class.getDeclaredField("resourceFactory");
        field.setAccessible(true);
        field.set(filterParser, resourceFactory);
    }

    @Test
    public void testEmptyFilter() throws InvalidSyntaxException {
        String filter = "";
        String namespace = "empty";

        Requirement requirement = filterParser.parse(filter, namespace);

        assertEquals(namespace, requirement.getNamespace());
        assertTrue(requirement.getChildren().isEmpty());
        assertTrue(requirement.getAttributes().isEmpty());
    }

    @Test
    public void testSingleGenericExpression() throws InvalidSyntaxException {
        String filter = "(a=b)";
        String namespace = NsOsgiPackage.NAMESPACE__OSGI_PACKAGE;

        Requirement requirement = filterParser.parse(filter, namespace);

        assertEquals(namespace, requirement.getNamespace());
        assertTrue(requirement.getChildren().isEmpty());

        assertFalse(requirement.getAttributes().isEmpty());
        assertEquals(1, requirement.getAttributes().size());

        SimpleAttributeType<String> type = new SimpleAttributeType<>("a", String.class);

        List<Attribute<String>> attributes = requirement.getAttributes(type);
        assertEquals(1, attributes.size());

        Attribute<String> attribute = attributes.get(0);

        assertEquals(type, attribute.getAttributeType());
        assertEquals(Operator.EQUAL, attribute.getOperator());
        assertEquals("b", attribute.getValue());
        assertEquals("b", attribute.getStringValue());
    }

    @Test
    public void testSinglePackageExpression() throws InvalidSyntaxException {
        String filter = "(package=cz.zcu.kiv.crce)";
        String namespace = NsOsgiPackage.NAMESPACE__OSGI_PACKAGE;

        Requirement requirement = filterParser.parse(filter, namespace);

        assertEquals(namespace, requirement.getNamespace());
        assertTrue(requirement.getChildren().isEmpty());

        assertFalse(requirement.getAttributes().isEmpty());
        assertEquals(1, requirement.getAttributes().size());

        List<Attribute<String>> attributes = requirement.getAttributes(NsOsgiPackage.ATTRIBUTE__NAME);
        assertEquals(1, attributes.size());

        Attribute<String> attribute = attributes.get(0);

        assertEquals(NsOsgiPackage.ATTRIBUTE__NAME, attribute.getAttributeType());
        assertEquals(Operator.EQUAL, attribute.getOperator());
        assertEquals("cz.zcu.kiv.crce", attribute.getValue());
        assertEquals("cz.zcu.kiv.crce", attribute.getStringValue());
    }

    @Test
    public void testPackageAndVersionConjunction() throws InvalidSyntaxException {
        String filter = "(&(package=cz.zcu.kiv.crce)(version=1.0.0))";
        String namespace = NsOsgiPackage.NAMESPACE__OSGI_PACKAGE;

        Requirement requirement = filterParser.parse(filter, namespace);

        assertEquals(namespace, requirement.getNamespace());
        assertTrue(requirement.getChildren().isEmpty());

        assertFalse(requirement.getAttributes().isEmpty());
        assertEquals(2, requirement.getAttributes().size());

        // package (name)

        List<Attribute<String>> names = requirement.getAttributes(NsOsgiPackage.ATTRIBUTE__NAME);
        assertEquals(1, names.size());

        Attribute<String> name = names.get(0);

        assertEquals(NsOsgiPackage.ATTRIBUTE__NAME, name.getAttributeType());
        assertEquals(Operator.EQUAL, name.getOperator());
        assertEquals("cz.zcu.kiv.crce", name.getValue());
        assertEquals("cz.zcu.kiv.crce", name.getStringValue());

        // version

        List<Attribute<Version>> versions = requirement.getAttributes(NsOsgiPackage.ATTRIBUTE__VERSION);
        assertEquals(1, versions.size());

        Attribute<Version> version = versions.get(0);

        assertEquals(NsOsgiPackage.ATTRIBUTE__VERSION, version.getAttributeType());
        assertEquals(Operator.EQUAL, version.getOperator());
        assertEquals(new Version("1.0.0"), version.getValue());
        assertEquals("1.0.0", version.getStringValue());
    }

    @Test
    public void testPackageAndTwoVersionsConjunction() throws InvalidSyntaxException {
        String filter = "(&(package=cz.zcu.kiv.crce)(version>=1.0.0)(version<2.0.0))";
        String namespace = NsOsgiPackage.NAMESPACE__OSGI_PACKAGE;

        Requirement requirement = filterParser.parse(filter, namespace);

        assertEquals(namespace, requirement.getNamespace());
        assertTrue(requirement.getChildren().isEmpty());

        assertFalse(requirement.getAttributes().isEmpty());
        assertEquals(3, requirement.getAttributes().size());

        // package (name)

        List<Attribute<String>> names = requirement.getAttributes(NsOsgiPackage.ATTRIBUTE__NAME);
        assertEquals(1, names.size());

        Attribute<String> name = names.get(0);

        assertEquals(NsOsgiPackage.ATTRIBUTE__NAME, name.getAttributeType());
        assertEquals(Operator.EQUAL, name.getOperator());
        assertEquals("cz.zcu.kiv.crce", name.getValue());
        assertEquals("cz.zcu.kiv.crce", name.getStringValue());

        // versions

        List<Attribute<Version>> versions = requirement.getAttributes(NsOsgiPackage.ATTRIBUTE__VERSION);
        assertEquals(2, versions.size());

        Attribute<Version> version = versions.get(0);

        assertEquals(NsOsgiPackage.ATTRIBUTE__VERSION, version.getAttributeType());
        assertEquals(Operator.GREATER_EQUAL, version.getOperator());
        assertEquals(new Version("1.0.0"), version.getValue());
        assertEquals("1.0.0", version.getStringValue());

        version = versions.get(1);

        assertEquals(NsOsgiPackage.ATTRIBUTE__VERSION, version.getAttributeType());
        assertEquals(Operator.LESS, version.getOperator());
        assertEquals(new Version("2.0.0"), version.getValue());
        assertEquals("2.0.0", version.getStringValue());
    }

    @Ignore // TODO support of nested requirements from filter
    @Test
    public void testNestedRequirement() throws InvalidSyntaxException {
        String filter = "(&(package=cz.zcu.kiv.crce)(|(&(version>=1.0.0)(version<1.5.0))(&(version>=1.6.0)(version<2.0.0))))";
        String namespace = NsOsgiPackage.NAMESPACE__OSGI_PACKAGE;

        Requirement requirement = filterParser.parse(filter, namespace);

        assertEquals(namespace, requirement.getNamespace());
        assertFalse(requirement.getChildren().isEmpty());

        assertFalse(requirement.getAttributes().isEmpty());
        assertEquals(1, requirement.getAttributes().size());

        assertEquals(1, requirement.getChildren().size());

        // package (name)

        List<Attribute<String>> names = requirement.getAttributes(NsOsgiPackage.ATTRIBUTE__NAME);
        assertEquals(1, names.size());

        Attribute<String> name = names.get(0);

        assertEquals(NsOsgiPackage.ATTRIBUTE__NAME, name.getAttributeType());
        assertEquals(Operator.EQUAL, name.getOperator());
        assertEquals("cz.zcu.kiv.crce", name.getValue());
        assertEquals("cz.zcu.kiv.crce", name.getStringValue());

        // versions

        List<Attribute<Version>> versions = requirement.getAttributes(NsOsgiPackage.ATTRIBUTE__VERSION);
        assertTrue(versions.isEmpty());


        // --- 1. level nested requirement ---

        Requirement parent = requirement;
        requirement = requirement.getChildren().get(0);

        assertEquals(namespace, requirement.getNamespace());
        assertEquals(requirement.getParent(), parent);

        assertTrue(requirement.getAttributes().isEmpty());
        assertEquals("or", requirement.getDirectives().get("operator"));

        assertEquals(2, requirement.getChildren());

        // package (name)

        names = requirement.getAttributes(NsOsgiPackage.ATTRIBUTE__NAME);
        assertTrue(names.isEmpty());

        // versions

        versions = requirement.getAttributes(NsOsgiPackage.ATTRIBUTE__VERSION);
        assertTrue(versions.isEmpty());


        // --- 2. level 1. nested requirement ---

        parent = requirement;
        requirement = parent.getChildren().get(0);

        assertEquals(namespace, requirement.getNamespace());
        assertEquals(requirement.getParent(), parent);
        assertTrue(requirement.getChildren().isEmpty());

        assertFalse(requirement.getAttributes().isEmpty());
        assertEquals(2, requirement.getAttributes().size());

        // package (name)

        names = requirement.getAttributes(NsOsgiPackage.ATTRIBUTE__NAME);
        assertTrue(names.isEmpty());

        // versions

        versions = requirement.getAttributes(NsOsgiPackage.ATTRIBUTE__VERSION);
        assertEquals(2, versions.size());

        Attribute<Version> version = versions.get(0);

        assertEquals(NsOsgiPackage.ATTRIBUTE__VERSION, version.getAttributeType());
        assertEquals(Operator.GREATER_EQUAL, version.getOperator());
        assertEquals(new Version("1.0.0"), version.getValue());
        assertEquals("1.0.0", version.getStringValue());

        version = versions.get(1);

        assertEquals(NsOsgiPackage.ATTRIBUTE__VERSION, version.getAttributeType());
        assertEquals(Operator.LESS, version.getOperator());
        assertEquals(new Version("1.5.0"), version.getValue());
        assertEquals("1.5.0", version.getStringValue());


        // --- 2. level 2. nested requirement ---

        requirement = parent.getChildren().get(1);

        assertEquals(namespace, requirement.getNamespace());
        assertEquals(requirement.getParent(), parent);
        assertTrue(requirement.getChildren().isEmpty());

        assertFalse(requirement.getAttributes().isEmpty());
        assertEquals(2, requirement.getAttributes().size());

        // package (name)

        names = requirement.getAttributes(NsOsgiPackage.ATTRIBUTE__NAME);
        assertTrue(names.isEmpty());

        // versions

        versions = requirement.getAttributes(NsOsgiPackage.ATTRIBUTE__VERSION);
        assertEquals(2, versions.size());

        version = versions.get(0);

        assertEquals(NsOsgiPackage.ATTRIBUTE__VERSION, version.getAttributeType());
        assertEquals(Operator.GREATER_EQUAL, version.getOperator());
        assertEquals(new Version("1.6.0"), version.getValue());
        assertEquals("1.6.0", version.getStringValue());

        version = versions.get(1);

        assertEquals(NsOsgiPackage.ATTRIBUTE__VERSION, version.getAttributeType());
        assertEquals(Operator.LESS, version.getOperator());
        assertEquals(new Version("2.0.0"), version.getValue());
        assertEquals("2.0.0", version.getStringValue());
    }

}
