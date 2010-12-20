package tr.com.srdc.icardea.consenteditor;

import java.io.InputStream;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;


@Path("/xacmlDocuments") 
public class DownloadHelper {
	@GET
	@Path("/{filename}")
	@Produces(MediaType.APPLICATION_XML)
	public Response getFile(@PathParam("filename") String filename){
		System.out.println("File:" + filename);
		ConfigurationParser confParser = new ConfigurationParser();
		InputStream is = DownloadHelper.class.getResourceAsStream("/" + confParser.getXacmlDocuments() + filename);
		System.out.println(is.toString());
		if(is == null){
			return Response.status(Status.NOT_FOUND).build();
		}else{
			return Response.ok(is).build();
		}
	}
	
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public Response getString(){
		System.out.println("File");
		return Response.ok("Test Successful!!").build();
	}
}
