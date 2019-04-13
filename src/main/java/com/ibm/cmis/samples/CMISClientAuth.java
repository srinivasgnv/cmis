/* Author - Satya 
CMIS passing Basic Auth token in 
HTTP Headers 
and perform some basic operations */

package com.ibm.cmis.samples;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.ObjectId;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.enums.BaseTypeId;
import org.apache.chemistry.opencmis.commons.enums.BindingType;
import org.apache.chemistry.opencmis.commons.enums.VersioningState;
import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;

public class CMISClientAuth
{

	//CMIS Connection details
	private static String ATOMPUB_BINDING_URL = "https://host/openfncmis_wlp/atom";
	private static String USERNAME = "username";
	private static String PASSWORD = "passwrod";
	private static String REPOSITORY_ID = "REPO";
	//Base location for this Demo to where the uploaded files gets stored
	private static String ROOT_FOLDER = "Demo";
	private static ObjectId objectId = null;


	public static void main(String[] args) {
		BacsocCmisClient demo = new BacsocCmisClient();
		//Creating the connection and getting session object,  A session is a connection to a CMIS repository with a specific user.
		//Refer link more about Session https://chemistry.apache.org/java/javadoc/org/apache/chemistry/opencmis/client/api/Session.html
		Session sess = demo.setUpConnection();
		demo.createDocument(sess);

	}

	/**
	 * Establishing connection to CMIS
	 */
	public  Session setUpConnection()
	{
		try
		{
			readFromPropertiesFile();
			SessionFactoryImpl factory = SessionFactoryImpl.newInstance();
			Map<String, String> parameters = new HashMap<String, String>();
			parameters.put(SessionParameter.USER, USERNAME);
			parameters.put(SessionParameter.PASSWORD, PASSWORD);
			parameters.put(SessionParameter.REPOSITORY_ID, REPOSITORY_ID);
			parameters.put(SessionParameter.HEADER+".0", "Authorization: Basic Y21pcy5maWRAdDIwMDY6c3I3Kzd2cWE3NmVJaGo4ZU1kN21iVVNOSEhKd29mSllHSnpYWjlqZg==");

			parameters.put(SessionParameter.ATOMPUB_URL, ATOMPUB_BINDING_URL);
			parameters.put(SessionParameter.BINDING_TYPE, BindingType.ATOMPUB.value());
			System.out.println("Executing Atompub binding");

			return factory.createSession(parameters);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;

	}

	/**
	 * Creating document
	 */
	public void createDocument(Session session)
	{
		try{
			String docName = "Document_ATOMPUB";
			System.out.println("inside createDocument()");

			//STEP 1: Getting the folder object to place the document inside that folder
			Folder folder = getFolder(session);

			Map<String, Object> properties = new HashMap<String, Object>();
			properties.put(PropertyIds.OBJECT_TYPE_ID, BaseTypeId.CMIS_DOCUMENT.value());
			properties.put(PropertyIds.NAME, docName);

			//STEP 2: Creating the document
			ObjectId docId = session.createDocument(properties, folder, createContentStream(docName, session), VersioningState.MAJOR);
			System.out.println("------ Document created and "+ docId + " under folder: "+folder.getName());

			//STEP 3: Retrieve the created document from repository
			getDocument(session, docId);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * Get the folder if exist in repository else create and return
	 * @param session
	 * @return
	 */
	public Folder getFolder(Session session){
		Folder folder = null;
		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put(PropertyIds.OBJECT_TYPE_ID, BaseTypeId.CMIS_FOLDER.value());
		properties.put(PropertyIds.NAME, ROOT_FOLDER);
		try
		{
			folder = (Folder) session.getObjectByPath("/" + ROOT_FOLDER);
			return folder;
		}
		catch(CmisObjectNotFoundException e){
			System.out.println("----- "+ROOT_FOLDER + " not exist, creating....");
			Folder rootFolder = (Folder) session.getRootFolder();
			folder = rootFolder.createFolder(properties);
		}
		return folder;

	}

	/**
	 * Get document details
	 */

	public void getDocument(Session session, ObjectId objectId)
	{
		System.out.println("inside getDocument");
		Document document = (Document)session.getObject(objectId);
		System.out.println("------ Document name: "+ document.getName() + " , content size: "+ document.getContentStreamLength() + " , content file name: "+ document.getContentStreamFileName());
	}

	/**
	 * Creating Content Stream
	 * @param documentName
	 * @return
	 */
	private ContentStream createContentStream(String documentName, Session session) {

		try {
			String fileNameStr = documentName;
			// Make sure the temporary file exist in the provided location with some content
			File file = new File("test.txt");
			byte[] content =   readContentIntoByteArray(file);
			InputStream stream = new ByteArrayInputStream(content);
			//Creating the content stream in the repository
			ContentStream contentStream = session.getObjectFactory().createContentStream(fileNameStr + ".txt", content.length, "text/plain", stream);
			return contentStream;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Creating content byte array
	 * @param file
	 * @return
	 */
	static byte[] readContentIntoByteArray(File file)
	   {
	      FileInputStream fileInputStream = null;
	      byte[] bFile = new byte[(int) file.length()];
	      try
	      {
	         //convert file into array of bytes
	         fileInputStream = new FileInputStream(file);
	         fileInputStream.read(bFile);
	         fileInputStream.close();
	      }
	      catch (Exception e)
	      {
	         e.printStackTrace();
	      }
	      return bFile;
	   }

	private static void readFromPropertiesFile(){
		Properties prop = new Properties();
		InputStream input = null;

		try {

			input = new FileInputStream("cmis_sample.properties");

			// load a properties file
			prop.load(input);
			
			ATOMPUB_BINDING_URL=prop.getProperty("ATOMPUB_BINDING_URL");
			
			REPOSITORY_ID=prop.getProperty("REPOSITORY_ID");
			
			USERNAME=prop.getProperty("USERNAME");
			
			PASSWORD=prop.getProperty("PASSWORD");
			
			ROOT_FOLDER=prop.getProperty("ROOT_FOLDER");
			
			
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}

