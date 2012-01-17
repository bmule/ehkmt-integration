package at.srfg.kmt.ehealth.phrs.presentation.utils.json
 
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.util.Iterator

import org.codehaus.jackson.JsonGenerationException
import org.codehaus.jackson.JsonNode
import org.codehaus.jackson.map.JsonMappingException
import org.codehaus.jackson.map.ObjectMapper
 
public class JacksonTreeModel {
	/* http://wiki.fasterxml.com/JacksonTreeModel
ObjectMapper mapper = new ObjectMapper();
      // (note: can also use more specific type, like ArrayNode or ObjectNode!)
      JsonNode rootNode = mapper.readValue(src, JsonNode.class); // src can be a File, URL, InputStream etc
    
      // or, with dedicated method; requires a JsonParser instance
      JsonParser jp = ...; // construct using JsonFactory (can get one using ObjectMapper.getJsonFactory)
      rootNode = mapper.readTree(jp);
      // (most useful when binding sub-trees)
   
   
   
     JsonNode array = root.get("array"); // would return null if none found
  // let's use "path()" -- if no such element, will return MissingNode which can be dereferenced!
  String name = array.path(1).getValueAsText(); // or, getTextValue if it's certain it is JSON String
  String id = root.path("object").path("id").getTextValue(); // so it'll be null if not found
  // or
  JsonNode idNode = root.path("object").path("id"); // never returns null
  if (idNode.isMissingNode()) {
    throw new IllegalArgumentException("Could not find id!");
  }

  // or maybe we just want to traverse all values of properties of the array node?
  for (JsonNode node : root.path("array")) {
    System.out.println("Entry: "+node.toString());
  }
*/

	public def formatJsonArticle(def src, String resourceType) {
 
		ObjectMapper mapper = new ObjectMapper();
 
		try {
			//BufferedReader fileReader = new BufferedReader(
			//	new StringReader(src));
			JsonNode rootNode = mapper.readValue(src, JsonNode.class);
			//JsonNode rootNode = mapper.readTree(fileReader);
 
			/*** read ***/
			JsonNode nameNode = rootNode.path("name");
			System.out.println(nameNode.getTextValue());
 
			JsonNode ageNode = rootNode.path("age");
			System.out.println(ageNode.getIntValue());
 
			JsonNode msgNode = rootNode.path("messages");
			Iterator<JsonNode> ite = msgNode.getElements();
 
			while (ite.hasNext()) {
				JsonNode temp = ite.next();
				System.out.println(temp.getTextValue());
 
			}
 
			/* update could add stuff for View... 
			((ObjectNode)rootNode).put("nickname", "new nickname");
			((ObjectNode)rootNode).put("name", "updated name");
			((ObjectNode)rootNode).remove("age");
				*/
			mapper.writeValue(new File("c:\\user.json"), rootNode);
 
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
 
	}
 
}
