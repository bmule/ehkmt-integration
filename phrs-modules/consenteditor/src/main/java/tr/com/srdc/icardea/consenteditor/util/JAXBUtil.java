package tr.com.srdc.icardea.consenteditor.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URL;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;

import tr.com.srdc.icardea.consenteditor.ConfigurationParser;
import tr.com.srdc.icardea.consenteditor.model.ObjectFactory;

public class JAXBUtil {

	public static ConfigurationParser confParser = new ConfigurationParser();

	public static String xml2String(String filepath) {
		File file;
		String result = "";
		try {
			URL fileUrl = JAXBUtil.class.getClassLoader().getResource(filepath);
			file = new File(fileUrl.toURI());
			StringBuffer fileData = new StringBuffer(1000);
			BufferedReader reader = new BufferedReader(new FileReader(file));
			char[] buf = new char[1024];
			int numRead = 0;
			while ((numRead = reader.read(buf)) != -1) {
				String readData = String.valueOf(buf, 0, numRead);
				fileData.append(readData);
				buf = new char[1024];
			}
			reader.close();
			result = fileData.toString();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	public static void string2File(String content, String directory,
			String filename) {
		File messageFile;
		System.out.println("CONTENT:");
		System.out.println(content);
		try {
			URL fileUrl = JAXBUtil.class.getClassLoader()
					.getResource(directory);
			fileUrl = new URL(fileUrl.toURI() + filename);
			messageFile = new File(fileUrl.toURI());

			FileOutputStream fileOutput = new FileOutputStream(messageFile);
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
					fileOutput, "UTF-8"));
			writer.write(content);
			writer.close();
			try {
				File backupFile = new File(confParser.getAbstractPath()
						+ directory + filename);
				fileOutput = new FileOutputStream(backupFile);
				writer = new BufferedWriter(new OutputStreamWriter(fileOutput,
						"UTF-8"));
				writer.write(content);
				writer.close();
			} catch (Exception e) {
				System.out.println("Could not write to external file.");
				//e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	public static Object unmarshall(String filePath) {
		Object result = null;
		JAXBContext jc;
		try {
			jc = JAXBContext.newInstance("tr.com.srdc.icardea.consenteditor.model",
					ObjectFactory.class.getClassLoader());

			Unmarshaller unmarshaller = jc.createUnmarshaller();

			SchemaFactory factory = SchemaFactory
					.newInstance("http://www.w3.org/2001/XMLSchema");

			URL url = JAXBUtil.class.getClassLoader().getResource(
					confParser.getSchemaLocation());
			File schemaLocation = new File(url.toURI());
			Schema schema = factory.newSchema(schemaLocation);
			unmarshaller.setSchema(schema);

			URL fileUrl = JAXBUtil.class.getClassLoader().getResource(filePath);
			File file = new File(fileUrl.toURI());

			result = unmarshaller.unmarshal(file);

		} catch (JAXBException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return result;
	}

	public static Object unmarshallContent(String content) {
		Object result = null;
		JAXBContext jc;
		try {
			jc = JAXBContext.newInstance("tr.com.srdc.icardea.consenteditor.model",
					ObjectFactory.class.getClassLoader());

			Unmarshaller unmarshaller = jc.createUnmarshaller();

			SchemaFactory factory = SchemaFactory
					.newInstance("http://www.w3.org/2001/XMLSchema");

			URL url = JAXBUtil.class.getClassLoader().getResource(
					confParser.getSchemaLocation());
			File schemaLocation = new File(url.toURI());
			Schema schema = factory.newSchema(schemaLocation);
			unmarshaller.setSchema(schema);

			InputStream is = new ByteArrayInputStream(content.getBytes("UTF-8"));

			result = unmarshaller.unmarshal(is);

		} catch (JAXBException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return result;
	}

	public static void marshall(Object object, String fileDirectory,
			String filePath) {
		System.out.println("marshall0");
		System.out.println(filePath);
		System.out.println(fileDirectory);
		try {
			JAXBContext jc = JAXBContext.newInstance(
					"tr.com.srdc.icardea.consenteditor.model", ObjectFactory.class
							.getClassLoader());
			Marshaller marshaller = jc.createMarshaller();

			SchemaFactory factory = SchemaFactory
					.newInstance("http://www.w3.org/2001/XMLSchema");
			URL url = JAXBUtil.class.getClassLoader().getResource(
					confParser.getSchemaLocation());
			File schemaLocation;
			schemaLocation = new File(url.toURI());

			Schema schema = factory.newSchema(schemaLocation);
			marshaller.setSchema(schema);
			marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			URL fileUrl = JAXBUtil.class.getClassLoader().getResource(
					fileDirectory);
			fileUrl = new URL(fileUrl.toURI() + filePath);
			File file = new File(fileUrl.toURI());

			marshaller.marshal(object, file);
			
			try{
			File backupFile = new File(confParser.getAbstractPath()
					+ fileDirectory + filePath);
			marshaller.marshal(object, backupFile);
			}
			catch(Exception e){
				System.out.println("Could not write to external file.");
				//e.printStackTrace();
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (JAXBException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String marshall2String(Object object) {
		String result = "";
		try {
			JAXBContext jc = JAXBContext.newInstance(
					"tr.com.srdc.icardea.consenteditor.model", ObjectFactory.class
							.getClassLoader());
			Marshaller marshaller = jc.createMarshaller();

			SchemaFactory factory = SchemaFactory
					.newInstance("http://www.w3.org/2001/XMLSchema");
			URL url = JAXBUtil.class.getClassLoader().getResource(
					confParser.getSchemaLocation());
			File schemaLocation;
			schemaLocation = new File(url.toURI());
			Schema schema = factory.newSchema(schemaLocation);
			marshaller.setSchema(schema);
			marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			marshaller.marshal(object, baos);
			result = baos.toString();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (JAXBException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
		return result;
	}
}
