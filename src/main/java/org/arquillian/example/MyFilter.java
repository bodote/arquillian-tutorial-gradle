package org.arquillian.example;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import java.io.IOException;
import java.security.Principal;
import java.util.logging.Logger;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

import org.jboss.logging.MDC;

@Provider
@Priority(Priorities.USER) // default anyhow...
public class MyFilter implements ContainerRequestFilter, ContainerResponseFilter {
	Logger logger = Logger.getLogger(this.toString());

	@Override
	public void filter(ContainerRequestContext containerRequestContext) throws IOException {
		long time = System.currentTimeMillis();
		MDC.put("mdc.timestamp", new Long(time));
		MDC.put("mdc.http.url", containerRequestContext.getUriInfo().getAbsolutePath().toString());
		MDC.put("mdc.http.method", containerRequestContext.getMethod());

		MDC.put("mdc.user", "bodo");
		logger.fine("request filter");

	}

	@Override
	public void filter(ContainerRequestContext containerRequestContext,
			ContainerResponseContext containerResponseContext) throws IOException {

		logger.fine("responsefilter start ");

		long time = System.currentTimeMillis();
		long startTime = Long.parseLong(MDC.get("mdc.timestamp").toString());
		MDC.remove("mdc.timestamp");
		MDC.put("mdc.duration", Long.toString(time - startTime));

		logger.info("responsefilter end");

		MDC.remove("mdc.duration");
		MDC.remove("mdc.http.url");
		MDC.remove("mdc.http.method");
		MDC.remove("mdc.user");

	}

}
