/**
 * 
 */
package atg.sitemap;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import atg.multisite.SiteManager;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;

/**
 * @author nkansal
 * 
 */
public class BBBSitemapGeneratorService extends SitemapGeneratorService {
	public static final String SITE_ID_START_WITH_GS = "GS_";
	public static final String SITE_ID_START_WITH_TBS = "TBS_";

	/**
	 * This method is used to filter siteIds which are not required during
	 * site sitemap generation.
	 * 
	 * @return List
	 */
	@Override
	public List<String> getSites()
	  {
		if(isLoggingDebug()){
			this.logDebug("SitemapGeneratorService.getSites() - starts");
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
        	logError( "SitemapGeneratorService.getSites() :: " ,ex);
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
                	this.logDebug("SitemapGeneratorService.getSites() - sites are:"+site.getRepositoryId() );
                	siteIds.add(site.getRepositoryId());
                }
            }

        }
        if(isLoggingDebug()){
        	
        	this.logDebug("SitemapGeneratorService.getSites() - ends");
        }
        
        return siteIds;
	   }
	 }

