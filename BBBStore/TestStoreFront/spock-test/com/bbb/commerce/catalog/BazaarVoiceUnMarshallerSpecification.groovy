package com.bbb.commerce.catalog

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection
import java.sql.PreparedStatement;
import java.sql.SQLException
import java.sql.Statement
import java.util.Map
import javax.sql.DataSource
import javax.xml.bind.JAXBElement;
import atg.adapter.gsa.GSAItemDescriptor
import atg.adapter.gsa.GSARepository
import atg.repository.MutableRepository
import atg.repository.RepositoryException
import com.bbb.constants.BBBCmsConstants
import com.bbb.framework.jaxb.bazaarvoice.FeedType;
import com.bbb.utils.BBBConfigRepoUtils;
import com.bbb.framework.jaxb.bazaarvoice.ProductType
import com.bbb.framework.jaxb.bazaarvoice.ReviewStatisticsType;
import spock.lang.specification.BBBExtendedSpec;

class BazaarVoiceUnMarshallerSpecification extends BBBExtendedSpec {

	BazaarVoiceUnMarshaller unmarshal
	Map<String, String> siteFeedConfiguration = new HashMap()
	Map<String, String> feedFileArchivePath = new HashMap()
	Map<String, String> feedFilePath = new HashMap()
	BBBCatalogTools bbbCatalogTools =Mock()
	BazaarVoiceManager manager =Mock()
	GSARepository gRep =Mock()
	DataSource dS =Mock()
	Connection conn =Mock()

	def setup(){
		unmarshal = new BazaarVoiceUnMarshaller()
		unmarshal.setSiteFeedConfiguration(siteFeedConfiguration)
		unmarshal.setFeedFilePath(feedFilePath)
		BBBConfigRepoUtils.setBbbCatalogTools(bbbCatalogTools)
		unmarshal.setCatalogTools(bbbCatalogTools)
		unmarshal.setQuery("")
		unmarshal.setArchiveFilePathKey("")
	}

	private setParametersForSpy(){
		unmarshal = Spy()
		unmarshal.setSiteFeedConfiguration(siteFeedConfiguration)
		unmarshal.setFeedFilePath(feedFilePath)
		unmarshal.setFeedFileArchivePath(feedFileArchivePath)
		unmarshal.setBazaarVoiceManager(manager)
	}

	def"unmarshal, parse bazaar voice file and populates its value to VO"(){

		given:
		setParametersForSpy()
		FileInputStream fstream = Mock()
		JAXBElement<FeedType> itemJaxb =Mock()
		ReviewStatisticsType reviewStatistics =Mock()
		ReviewStatisticsType reviewStatistics1 =Mock()
		Statement statement =Mock()
		PreparedStatement preparedStatement =Mock()
		FeedType item =	Mock()
		ProductType p1 =Mock()
		ProductType p2 =Mock()
		ProductType p3 =Mock()
		GSAItemDescriptor desc =Mock()
		GSAItemDescriptor desc1 =Mock()

		siteFeedConfiguration.put("1", "true")
		siteFeedConfiguration.put("2", "true")
		siteFeedConfiguration.put("3", "false")
		feedFilePath.put("1", "US")
		feedFilePath.put("2", "true")
		feedFileArchivePath.put("1", "US")

		1*bbbCatalogTools.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS ,"true" ) >> ["filePath"]
		2*bbbCatalogTools.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS ,"US" ) >>  ["filePath1"]

		1*unmarshal.extractMethodForObtainingFilePath("filePath") >> {throw new FileNotFoundException("")}
		1*unmarshal.extractMethodForObtainingFilePath("filePath1") >> fstream
		1*unmarshal.extractMethodForUnmarshalling(_, fstream) >> itemJaxb
		1*itemJaxb.getValue() >> item
		item.getProduct() >> [p1,p2,null]

		1*p1.getExternalId()  >> "extId"
		2*p1.getId() >> "id"
		2*p1.getName() >>"name"
		2*p1.getSource() >> "source"
		1*p1.getReviewStatistics() >> reviewStatistics
		2*reviewStatistics.getTotalReviewCount() >> 10
		2*reviewStatistics.getOverallRatingRange() >> 20
		2*reviewStatistics.getRatingsOnlyReviewCount() >> 30
		2*reviewStatistics.getAverageOverallRating() >> 40

		1*p2.getExternalId()  >> "extId"
		2*p2.getId() >> "id"
		2*p2.getName() >>"name"
		2*p2.getSource() >> "source"
		1*p2.getReviewStatistics() >> reviewStatistics1
		2*reviewStatistics1.getTotalReviewCount() >> 10
		1*reviewStatistics1.getOverallRatingRange() >> null
		1*reviewStatistics1.getRatingsOnlyReviewCount() >> null
		1*reviewStatistics1.getAverageOverallRating() >> null


		manager.getBazaarVoiceRepository() >> gRep
		gRep.getDataSource() >> dS
		unmarshal.setBatchNumber(0)
		dS.getConnection() >> conn
		conn.prepareStatement(_) >>preparedStatement
		conn.createStatement() >> statement
		unmarshal.extractDBCall(preparedStatement) >> 80
		1*statement.close() >> {throw new SQLException()}
		1*preparedStatement.close() >> {throw new SQLException("")}

		1*gRep.getItemDescriptorNames() >> ["1","2"]
		1*gRep.getItemDescriptor("1") >> desc
		1*gRep.getItemDescriptor("2") >> {throw new RepositoryException("")}
		1*fstream.close() >> {throw new IOException("IOException")}

		when:
		boolean flag  = unmarshal.unmarshal()

		then:
		flag == true
		1*desc.invalidateCaches()
		1*unmarshal.logError("Bazaar Voice feed file not found on this path " + "filePath", _)
		1*unmarshal.logError("RepositoryException from "+ BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,_)
		1*unmarshal.logError("SQL exception while closing the statementjava.sql.SQLException: ")
	}

	def"unmarshal, when SQL exception is thrown while creating statement and rollbacking the connection"(){

		given:
		setParametersForSpy()
		FileInputStream fstream = Mock()
		JAXBElement<FeedType> itemJaxb =Mock()
		FeedType item =	Mock()
		ProductType p1 =Mock()
		GSAItemDescriptor desc =Mock()

		siteFeedConfiguration.put("1", "true")
		feedFilePath.put("1", "US")
		feedFileArchivePath.put("1", "US")
		2*bbbCatalogTools.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS ,"US" ) >>  ["filePath1"]

		1*unmarshal.extractMethodForObtainingFilePath("filePath1") >> fstream
		1*unmarshal.extractMethodForUnmarshalling(_, fstream) >> itemJaxb
		1*itemJaxb.getValue() >> item
		item.getProduct() >> [p1]

		1*p1.getExternalId()  >> "extId"
		3*p1.getId() >> "id"
		1*p1.getName() >>"name"
		1*p1.getSource() >> "source"
		1*p1.getReviewStatistics() >> {throw new Exception("")}

		manager.getBazaarVoiceRepository() >> gRep
		gRep.getDataSource() >> dS
		unmarshal.setBatchNumber(90)
		dS.getConnection() >> conn
		0*conn.prepareStatement(_)
		1*conn.createStatement() >> {throw new SQLException("")}
		1*conn.rollback() >> {throw new SQLException("")}
		0*unmarshal.extractDBCall(_)
		1*gRep.getItemDescriptorNames() >> []

		when:
		boolean flag  = unmarshal.unmarshal()

		then:
		flag == true
		1*unmarshal.logError("Error in BazaarVoiceUnMarsahlling for product :" + p1.getId());
		1*unmarshal.logError("SQL exception while clearing the BazaarVoice repository",_)
		1*unmarshal.logError("SQL exception in rollbacking connection", _)
		0*desc.invalidateCaches()
	}

	def"unmarshal, when SQL exception is thrown while creating PreparedStatement"(){

		given:
		setParametersForSpy()
		FileInputStream fstream = Mock()
		Statement statement =Mock()
		JAXBElement<FeedType> itemJaxb =Mock()
		FeedType item =	Mock()
		ProductType p1 =Mock()
		GSAItemDescriptor desc =Mock()

		siteFeedConfiguration.put("1", "true")
		feedFilePath.put("1", "US")
		feedFileArchivePath.put("1", "US")
		2*bbbCatalogTools.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS ,"US" ) >>  ["filePath1"]

		1*unmarshal.extractMethodForObtainingFilePath("filePath1") >> fstream
		1*unmarshal.extractMethodForUnmarshalling(_, fstream) >> itemJaxb
		1*itemJaxb.getValue() >> item
		item.getProduct() >> [p1]

		1*p1.getExternalId()  >> "extId"
		3*p1.getId() >> "id"
		1*p1.getName() >>"name"
		1*p1.getSource() >> "source"
		1*p1.getReviewStatistics() >> {throw new Exception("")}

		manager.getBazaarVoiceRepository() >> gRep
		gRep.getDataSource() >> dS
		unmarshal.setBatchNumber(90)
		dS.getConnection() >> conn
		1*conn.prepareStatement(_) >>{throw new SQLException("")}
		1*conn.createStatement() >> statement
		1*conn.rollback() >> {}
		0*unmarshal.extractDBCall(_)
		1*gRep.getItemDescriptorNames() >> []

		when:
		boolean flag  = unmarshal.unmarshal()

		then:
		flag == true
		1*unmarshal.logError("Error in BazaarVoiceUnMarsahlling for product :" + p1.getId());
		1*unmarshal.logError("SQL exception while clearing the BazaarVoice repository",_)
		0*unmarshal.logError("SQL exception in rollbacking connection", _)
		0*desc.invalidateCaches()
	}

	def"unmarshal, when SQL exception is thrown while creating connection"(){

		given:
		setParametersForSpy()
		FileInputStream fstream = Mock()
		JAXBElement<FeedType> itemJaxb =Mock()
		FeedType item =	Mock()
		ProductType p1 =Mock()
		GSAItemDescriptor desc =Mock()

		siteFeedConfiguration.put("1", "true")
		feedFilePath.put("1", "US")
		feedFileArchivePath.put("1", "US")
		2*bbbCatalogTools.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS ,"US" ) >>  ["filePath1"]

		1*unmarshal.extractMethodForObtainingFilePath("filePath1") >> fstream
		1*unmarshal.extractMethodForUnmarshalling(_, fstream) >> itemJaxb
		1*itemJaxb.getValue() >> item
		item.getProduct() >> [p1]

		1*p1.getExternalId()  >> "extId"
		3*p1.getId() >> "id"
		1*p1.getName() >>"name"
		1*p1.getSource() >> "source"
		1*p1.getReviewStatistics() >> {throw new Exception("")}

		manager.getBazaarVoiceRepository() >> gRep
		gRep.getDataSource() >> dS
		unmarshal.setBatchNumber(90)
		dS.getConnection() >> {throw new SQLException("")}
		0*conn.prepareStatement(_)
		0*conn.createStatement()
		0*conn.rollback() >> {}
		0*unmarshal.extractDBCall(_)
		1*gRep.getItemDescriptorNames() >> []

		when:
		boolean flag  = unmarshal.unmarshal()

		then:
		flag == true
		1*unmarshal.logError("Error in BazaarVoiceUnMarsahlling for product :" + p1.getId());
		1*unmarshal.logError("SQL exception while clearing the BazaarVoice repository",_)
		0*unmarshal.logError("SQL exception in rollbacking connection", _)
		0*desc.invalidateCaches()
	}

	def"unmarshal, when FeedItem is null"(){

		given:
		setParametersForSpy()
		FileInputStream fstream = Mock()
		JAXBElement<FeedType> itemJaxb =Mock()
		GSAItemDescriptor desc =Mock()

		siteFeedConfiguration.put("1", "true")
		feedFilePath.put("1", "US")
		feedFileArchivePath.put("1", "US")
		bbbCatalogTools.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS ,"US" ) >>  ["filePath1"] >> [null]
		1*unmarshal.extractMethodForObtainingFilePath("filePath1") >> fstream
		1*unmarshal.extractMethodForUnmarshalling(_, fstream) >> itemJaxb
		1*itemJaxb.getValue() >> null
		0*unmarshal.archiveFile(_, "filePath1") >> {}

		when:
		boolean flag  = unmarshal.unmarshal()

		then:
		flag == false
		1*unmarshal.logDebug("Bazaar Voice feed file is empty " + "filePath1")
		0*desc.invalidateCaches()
		0*unmarshal.logError("Bazaar Voice feed file not found on this path " + "filePath", _)
		0*unmarshal.logError("RepositoryException from "+ BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,_)
	}

	def"moveFileToUploadLoc, when filename ends with bazaarvoice.xml"(){

		given:
		unmarshal.setFilePathKey("pathKey")
		File dir= Mock()
		File file1 = Mock()
		File file2  = Mock()
		1*dir.listFiles() >> [file1,file2]
		3*file1.getName() >>"cde"
		3*file2.getName() >>"bazaarvoice.xml"
		//for getFilePathFrmConfigRepo
		1*bbbCatalogTools.getContentCatalogConfigration("pathKey") >> ["str1"]

		when:
		File file =unmarshal.moveFileToUploadLoc(dir)

		then:
		file == file2
	}

	def"moveFileToUploadLoc, when filename ends with bazaarvoice.xml and getContentCatalogConfigration list is empty"(){

		given:
		unmarshal.setFilePathKey("pathKey")
		File dir= Mock()
		File file2  = Mock()
		1*dir.listFiles() >> [file2]
		3*file2.getName() >>"bazaarvoice.xml"
		//for getFilePathFrmConfigRepo
		1*bbbCatalogTools.getContentCatalogConfigration("pathKey") >> []

		when:
		File file =unmarshal.moveFileToUploadLoc(dir)

		then:
		file == file2
	}

	def"moveFileToUploadLoc, when fileList is empty"(){

		given:
		unmarshal.setFilePathKey("pathKey")
		File dir= Mock()
		1*dir.listFiles() >> []

		when:
		File file =unmarshal.moveFileToUploadLoc(dir)

		then:
		file == null
	}

	def"moveFileToUploadLoc, when fileList is null"(){

		given:
		unmarshal.setFilePathKey("pathKey")
		File dir= Mock()
		1*dir.listFiles() >> null

		when:
		File file =unmarshal.moveFileToUploadLoc(dir)

		then:
		file == null
	}

	def"moveFileToUploadLoc, when input dir is null"(){

		given:
		unmarshal.setFilePathKey("pathKey")

		when:
		File file =unmarshal.moveFileToUploadLoc(null)

		then:
		file == null
	}

	def"getFilePathForFeed, when fileToStream is not null"(){

		given:
		unmarshal =Spy()
		unmarshal.setFilePathKey("pathKey")
		File fileToStream =Mock()
		FileInputStream fstream =Mock()
		1*unmarshal.getFilePathFrmConfigRepo("pathKey") >> fileToStream
		1*unmarshal.extractFileRetreivalForGetFileForFeed(fileToStream) >> fstream

		when:
		FileInputStream fs =unmarshal.getFilePathForFeed()

		then:
		fs == fstream
	}

	def"getFilePathForFeed,when fileToStream is null"(){

		given:
		unmarshal =Spy()
		unmarshal.setFilePathKey("pathKey")
		1*unmarshal.getFilePathFrmConfigRepo("pathKey") >> null
		0*unmarshal.extractFileRetreivalForGetFileForFeed(null)

		when:
		FileInputStream fs =unmarshal.getFilePathForFeed()

		then:
		fs == null
	}

	def"getFilePathForFeed,when FileNotFoundException is thrown"(){

		given:
		unmarshal =Spy()
		unmarshal.setFilePathKey("pathKey")
		1*unmarshal.getFilePathFrmConfigRepo("pathKey") >> {throw new FileNotFoundException("error")}
		0*unmarshal.extractFileRetreivalForGetFileForFeed(_)

		when:
		FileInputStream fs =unmarshal.getFilePathForFeed()

		then:
		fs == null
		1*unmarshal.logError("File can't be read at BazaarVoiceUnMarshaller.unmarshal()" + "error",_)
	}

	def"getFeedFilePaths, "(){

		given:
		String key ="key"
		1*bbbCatalogTools.getContentCatalogConfigration(key) >> ["str1"]

		when:
		String value = unmarshal.getFeedFilePaths(key)

		then:
		value  == "str1"
	}

	def"getFeedFilePaths, 2"(){

		given:
		String key ="key"
		1*bbbCatalogTools.getContentCatalogConfigration(key) >> []

		when:
		String value = unmarshal.getFeedFilePaths(key)

		then:
		value  == null
	}
}
