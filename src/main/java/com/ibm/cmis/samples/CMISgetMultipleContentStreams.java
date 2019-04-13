/* Author Satya

Code to extract multiple content steams
from IBM CMIS object

*/


package com.ibm.cmis.samples;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Property;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.data.RepositoryInfo;
import org.apache.chemistry.opencmis.commons.enums.VersioningState;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ibm.ecm.cmis.test.opencmis.util.AbstractTest;
import com.ibm.ecm.cmis.test.opencmis.util.TestUtils;

public class CMISgetMultipleContentStreams extends AbstractTest {
	static protected RepositoryInfo _repoInfo;


    @SuppressWarnings("unchecked")
	@Test
	public void getMultiContentElements_WithContentElementsTest() {
		System.out.println("***************************** getMultiContentElements_WithContentElementsTest *********************");

		String documentName = "Rel_30_Multiple_Content_Document";
		try {
			Document document = (Document) session.getObjectByPath(sandboxFolder.getPath() + "/" + documentName);

			List<Property<?>> pList = document.getProperties();
			List<String> contentStreamIdsList = null;
			for(Property<?> p: pList){
				if(p.getDisplayName().equals("cmis:contentStreamId")){
					contentStreamIdsList = (List<String>) p.getValues();
					break;
				}
			}

			assertEquals(3, contentStreamIdsList.size());
			//Example code to fetch the multiple content streams
//			for(String i: contentStreamIdsList){
//				ContentStream s = document.getContentStream(i);
//				System.out.println(s.getMimeType());
//			}
		}
		catch(Exception e){

			fail(documentName + " is not found in the repository");

		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getMultiContentElements_WithoutContentElementsTest() {
		System.out.println("***************************** getMultiContentElements_WithoutContentElementsTest *********************");
		String documentName = "Rel_30_Without_Content_Document";

		//Creating document without content elements
		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:document");
		properties.put(PropertyIds.NAME, documentName);
		Document document = TestUtils.createDoc(sandboxFolder, documentName, VersioningState.MAJOR);

		List<Property<?>> pList = document.getProperties();
		List<String> contentStreamIdsList = null;
		for(Property<?> p: pList){
			if(p.getDisplayName().equals("cmis:contentStreamId")){
				contentStreamIdsList = (List<String>) p.getValues();
				break;
			}
		}

		assertEquals(1, contentStreamIdsList.size());
	}


	 @AfterClass
	 public static void tearDown() {

		String documentName = "Rel_30_Without_Content_Document";

		TestUtils.delete(sandboxFolder.getPath() + "/" +documentName, session);

	    AbstractTest.tearDown();
	 }

	@BeforeClass
	public static void setUp() throws Exception {
		AbstractTest.setUp();
		 _repoInfo = session.getRepositoryInfo();

	}

}
