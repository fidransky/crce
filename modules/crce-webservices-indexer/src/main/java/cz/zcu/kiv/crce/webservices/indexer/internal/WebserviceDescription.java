package cz.zcu.kiv.crce.webservices.indexer.internal;

import cz.zcu.kiv.crce.metadata.Resource;

/**
 *
 * @author David Pejrimovsky (maxidejf@gmail.com)
 */
public interface WebserviceDescription {

    Resource parseWebserviceDescription(String url);

    boolean saveResourceIntoRepository();
	
}