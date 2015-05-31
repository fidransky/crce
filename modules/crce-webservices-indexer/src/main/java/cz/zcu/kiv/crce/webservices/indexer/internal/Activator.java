package cz.zcu.kiv.crce.webservices.indexer.internal;

import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;

import org.osgi.framework.BundleContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.zcu.kiv.crce.metadata.MetadataFactory;

/**
 * Activator of this bundle.
 *
 * @author David Pejrimovsky (maxidejf@gmail.com)
 */
public class Activator extends DependencyActivatorBase {

    private static final Logger logger = LoggerFactory.getLogger(Activator.class);

    @Override
    public void init(BundleContext context, final DependencyManager manager) throws Exception {
        logger.debug("Initializing Webservices Indexer module.");
        manager.add(createComponent()
                .setInterface(WebserviceDescription.class.getName(), null)
                .setImplementation(WebservicesDescriptionImpl.class)
                .add(createServiceDependency().setRequired(true).setService(MetadataFactory.class)));
    }

    @Override
    public void destroy(BundleContext context, DependencyManager manager) throws Exception {
        logger.debug("Destroying Webservices Indexer module.");
    }
}