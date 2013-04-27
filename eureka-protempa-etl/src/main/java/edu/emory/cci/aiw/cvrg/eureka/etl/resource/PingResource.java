/*
 * #%L
 * Eureka Services
 * %%
 * Copyright (C) 2012 - 2013 Emory University
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package edu.emory.cci.aiw.cvrg.eureka.etl.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.google.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;


/**
 * A REST resource to allow an administrator to test an account.
 *
 * @author hrathod
 */
@Path("/ping")
@Produces(MediaType.APPLICATION_JSON)
public class PingResource {

	@Inject
	public PingResource() {
	}
	
	@GET
	@Path("")
	public Response doPing(@Context HttpServletRequest request) {
		HttpSession session = request.getSession();
		NewCookie cookie = new NewCookie("JSESSIONID", session.getId());
		return Response.ok().cookie(cookie).build();
	}
}
