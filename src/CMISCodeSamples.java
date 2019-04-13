package com.ibm.cmis.samples;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.enums.BindingType;

public class CMISCodeSamples {

	public static void main(String[] args) {
		try
		{
			SessionFactoryImpl factory = SessionFactoryImpl.newInstance();
			Map<String, String> parameters = new HashMap<String, String>();
			parameters.put(SessionParameter.USER, "XXX");
			parameters.put(SessionParameter.PASSWORD, "XXX");
			parameters.put(SessionParameter.ATOMPUB_URL, "http://<host>:<port>/<context_root>/atom11");
			parameters.put(SessionParameter.REPOSITORY_ID, "XXX");
			parameters.put(SessionParameter.BINDING_TYPE, BindingType.ATOMPUB.value());
			Session sess = factory.createSession(parameters);
			CmisObject cmisObject = sess.getObject("XXX");
			if (cmisObject instanceof Document) {
			    Document document = (Document) cmisObject;
			    System.out.println(document.getContentStreamFileName());
			    FileOutputStream outputStream = new FileOutputStream(new File("c:\\new_tiff.tif"));
			    int read = 0;
			    byte[] bytes = new byte[1024];
			    ContentStream newCS = document.getContentStream();
			    InputStream newIS = newCS.getStream();
			    while ((read = newIS.read(bytes)) != -1) {
			    	outputStream.write(bytes, 0, read);
			    }
			} else 
				System.out.println("It's not a CMIS Document");
		}
		catch (Exception e)
		{
			System.out.println(e);
		}

	}

}

