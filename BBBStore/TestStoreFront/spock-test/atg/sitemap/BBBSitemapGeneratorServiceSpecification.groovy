/**
 * 
 */
package atg.sitemap;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.bbb.repository.RepositoryItemMock

import atg.multisite.SiteManager;
import atg.multisite.SiteURLManager
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import spock.lang.specification.BBBExtendedSpec

/**
 * @author nkansal
 * 
 */
public class BBBSitemapGeneratorServiceSpecification extends BBBExtendedSpec {
	
	BBBSitemapGeneratorService bsg ;
	def SiteURLManager siteURLManagerMock = Mock()
	def SiteManager siteManagerMock = Mock()
	def setup()
	{
		bsg= new BBBSitemapGeneratorService();
		bsg.setSiteURLManager(siteURLManagerMock)
	}
	def "Fetching the active sites"(){
		given:
		List list = new ArrayList<RepositoryItemMock>()
		RepositoryItemMock iteMMock = new RepositoryItemMock()
		RepositoryItemMock iteMMock1 = new RepositoryItemMock()
		RepositoryItemMock iteMMock2 = new RepositoryItemMock()
		iteMMock.setRepositoryId("1234")
		iteMMock1.setRepositoryId("GS_1234")
		iteMMock2.setRepositoryId("TBS_1234")
		list.add(iteMMock)
		list.add(iteMMock1)
		list.add(iteMMock2)
		siteURLManagerMock.getSiteManager() >> siteManagerMock
		bsg.setActiveSitesOnly(true)
		siteManagerMock.getActiveSites() >> list.toArray()
		bsg.setLoggingDebug(true)
		when:
		List exList = bsg.getSites()
		then:
		exList.size() == 1
		exList.get(0).equals("1234")
		}

	def "Fetching the eanble sites"(){
		given:
		List list = new ArrayList<RepositoryItemMock>()
		RepositoryItemMock iteMMock = new RepositoryItemMock()
		RepositoryItemMock iteMMock1 = new RepositoryItemMock()
		RepositoryItemMock iteMMock2 = new RepositoryItemMock()
		iteMMock.setRepositoryId("1234")
		iteMMock1.setRepositoryId("GS_1234")
		iteMMock2.setRepositoryId("TBS_1234")
		list.add(iteMMock)
		list.add(iteMMock1)
		list.add(iteMMock2)
		siteURLManagerMock.getSiteManager() >> siteManagerMock
		bsg.setActiveSitesOnly(false)
		bsg.setEnabledSitesOnly(true)
		siteManagerMock.getEnabledSites() >> list.toArray()
		when:
		List exList = bsg.getSites()
		then:
		exList.size() == 1
		exList.get(0).equals("1234")
		}
	def "Fetching the ALl sites"(){
		given:
		List list = new ArrayList<RepositoryItemMock>()
		RepositoryItemMock iteMMock = new RepositoryItemMock()
		RepositoryItemMock iteMMock1 = new RepositoryItemMock()
		RepositoryItemMock iteMMock2 = new RepositoryItemMock()
		iteMMock.setRepositoryId("1234")
		iteMMock1.setRepositoryId("GS_1234")
		iteMMock2.setRepositoryId("TBS_1234")
		list.add(iteMMock)
		list.add(iteMMock1)
		list.add(iteMMock2)
		1*siteURLManagerMock.getSiteManager() >> siteManagerMock
		bsg.setActiveSitesOnly(false)
		bsg.setEnabledSitesOnly(false)
		1*siteManagerMock.getAllSites() >> list.toArray()
		when:
		List exList = bsg.getSites()
		then:
		exList.size() == 1
		exList.get(0).equals("1234")
		}
	def "Fetching the ALl sites is null"(){
		given:
		1*siteURLManagerMock.getSiteManager() >> siteManagerMock
		bsg.setActiveSitesOnly(false)
		bsg.setEnabledSitesOnly(false)
		1*siteManagerMock.getAllSites() >> null
		when:
		List exList = bsg.getSites()
		then:
		exList == null
		}
	def "Fetching the ALl sites is throws exception"(){
		given:
		1*siteURLManagerMock.getSiteManager() >> siteManagerMock
		bsg.setActiveSitesOnly(false)
		bsg.setEnabledSitesOnly(false)
		1*siteManagerMock.getAllSites() >> {throw new RepositoryException("Mock of Respository exception")}
		when:
		List exList = bsg.getSites()
		then:
		exList == null
	}

 }

