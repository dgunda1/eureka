package edu.emory.cci.aiw.cvrg.eureka.services.finder;

import java.util.ArrayList;
import java.util.List;

import com.google.inject.Inject;

import edu.emory.cci.aiw.cvrg.eureka.common.comm.PropositionWrapper;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;

public class SystemPropositionFinder extends AbstractPropositionFinder<Long,
	String> {

	private static final String CACHE_NAME = "systemPropositions";
	private static final CacheManager cacheManager = CacheManager.create();
	private static final Cache cache = cacheManager.getCache(CACHE_NAME);

	@Inject
	public SystemPropositionFinder(SystemPropositionRetriever inRetriever) {
		super(inRetriever);
	}

	@Override
	protected List<String> getPrefetchKeys(PropositionWrapper inWrapper) {
		List<String> keys = new ArrayList<String>();
		for (PropositionWrapper child : inWrapper.getChildren()) {
			if (child.isInSystem()) {
				keys.add(child.getKey());
			}
		}
		return keys;
	}

	@Override
	protected Cache getCache() {
		return cache;
	}
}