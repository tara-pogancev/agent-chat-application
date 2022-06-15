package rest;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.jaunt.*;
import com.jaunt.component.*;
import java.io.*;

@Stateless
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@LocalBean
@Path("/web")
public class WebScrapingRestRemoteBean implements WebScrapingRestRemote {

	@Override
	public String searchWeb(String text) {
		String retVal = "SEARCH RESULT";
		  try{
			  UserAgent userAgent = new UserAgent();         //create new userAgent (headless browser)
			  userAgent.visit("http://google.com");          //visit google
			  userAgent.doc.apply("butterflies").submit();   //apply form input and submit
			       
		//	  for (int i = 0; i < 15; i++) {

				  Elements linkDiv = userAgent.doc.findEvery("<a>").findEvery("<h3>");  //find search result links
				  System.out.println(linkDiv.size());
				  for(Element link : linkDiv) {
					  String linkText = link.getTextContent();
					  System.out.println(linkText);
					  
					  Element linkHref = link.getParent();					  
					  System.out.println(linkHref);
					  
		//		  }
				  //Hyperlink nextPageLink = userAgent.doc.nextPageLink(); //extract url to next page of results
			      //nextPageLink.follow();                                 //visit next page (p 2).

			  }
		      
		    } 
		    catch(JauntException e){
		      System.err.println(e);
		    } 		  

			return retVal;
		  }
	
	      	

}
