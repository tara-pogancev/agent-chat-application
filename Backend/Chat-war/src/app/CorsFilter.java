package app;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

@Provider
public class CorsFilter implements ContainerResponseFilter{

	@Override
	public void filter(ContainerRequestContext reqContext, ContainerResponseContext resContext) throws IOException {
		resContext.getHeaders().add("Access-Control-Allow-Origin", "*");
		resContext.getHeaders().add("Access-Control-Allow-Headers", "*");
	    resContext.getHeaders().add("Access-Control-Allow-Credentials", "true");
	    resContext.getHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");
	}

}
