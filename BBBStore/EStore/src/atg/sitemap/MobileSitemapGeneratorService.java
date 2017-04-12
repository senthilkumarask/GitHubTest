package atg.sitemap;

import java.util.ArrayList;
import java.util.List;

import atg.multisite.SiteManager;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;


/**
 * Sitemap generator service for mobile sites.
 * 
 * @author ssha53
 * 
 */
public class MobileSitemapGeneratorService extends SitemapGeneratorService {
	
	/** The Constant for sites start with GS */
	public static final String SITE_ID_START_WITH_GS = "GS_";
	public static final String SITE_ID_START_WITH_TBS = "TBS_";
	
	/**
	 * This method is used to filter siteIds which are not required during
	 * mobile site sitemap generation.
	 * 
	 * @return List
	 */
	@Override
    public List<String> getSites()
    {
		if(isLoggingDebug()){
			this.logDebug("MobileSitemapGeneratorService.getSites() - start");
		}
        final SiteManager siteManager = getSiteURLManager().getSiteManager();
        RepositoryItem sites[] = null;
        List<String> siteIds = null;
        try
        {
        	if(isActiveSitesOnly()){
                sites = siteManager.getActiveSites();
            } else
            if(isEnabledSitesOnly()){
                sites = siteManager.getEnabledSites();
            }else {
                sites = siteManager.getAllSites();
            }
        }
        catch(RepositoryException ex)
        {
        	logError( "MobileSitemapGeneratorService.getSites() :: " ,ex);
        }
        if(sites != null)
        {
            siteIds = new ArrayList<String>();
            final RepositoryItem repositoryItemArray[] = sites;
            final int length = repositoryItemArray.length;
            for(int itemCount = 0; itemCount < length; itemCount++)
            {
                final RepositoryItem site = repositoryItemArray[itemCount];
                if(!(site.getRepositoryId().startsWith(SITE_ID_START_WITH_GS) || site.getRepositoryId().startsWith(SITE_ID_START_WITH_TBS))){
                	siteIds.add(site.getRepositoryId());
                }
            }

        }
        if(isLoggingDebug()){
        	this.logDebug("MobileSitemapGeneratorService.getSites() - end");
        }
        
        return siteIds;
    }
	
}
