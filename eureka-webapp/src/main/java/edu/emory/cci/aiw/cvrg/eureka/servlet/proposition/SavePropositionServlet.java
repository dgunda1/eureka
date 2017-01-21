/*
 * #%L
 * Eureka WebApp
 * %%
 * Copyright (C) 2012 - 2013 Emory University
 * %%
 * This program is dual licensed under the Apache 2 and GPLv3 licenses.
 * 
 * Apache License, Version 2.0:
 * 
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
 * 
 * GNU General Public License version 3:
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */
package edu.emory.cci.aiw.cvrg.eureka.servlet.proposition;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.sun.jersey.api.client.ClientResponse;

import org.eurekaclinical.eureka.client.comm.Phenotype;
import org.eurekaclinical.eureka.client.comm.User;
import org.eurekaclinical.common.comm.clients.ClientException;
import edu.emory.cci.aiw.cvrg.eureka.common.comm.clients.ServicesClient;
import edu.emory.cci.aiw.cvrg.eureka.webapp.authentication.WebappAuthenticationSupport;
import java.net.URI;

public class SavePropositionServlet extends HttpServlet {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(SavePropositionServlet.class);
	private static final ObjectMapper MAPPER = new ObjectMapper();
	private final ServicesClient servicesClient;
	private final WebappAuthenticationSupport authenticationSupport;

	@Inject
	public SavePropositionServlet(ServicesClient inClient) {
		this.servicesClient = inClient;
		this.authenticationSupport = new WebappAuthenticationSupport(this.servicesClient);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.debug("SavePropositionServlet");

		try {
			Phenotype phenotype = MAPPER.readValue(req.getReader(),
					Phenotype.class);
			User user = this.authenticationSupport.getMe(req);
			phenotype.setUserId(user.getId());
			if (phenotype.getId() == null) {
				try {
					URI phenotypeURI = this.servicesClient.saveUserPhenotype(phenotype);
					if (phenotypeURI != null) {
						resp.setStatus(HttpServletResponse.SC_CREATED);
						resp.setHeader("Location", phenotypeURI.toString());
					}
				} catch (ClientException e) {
					resp.setStatus(e.getResponseStatus().getStatusCode());
					resp.getOutputStream().print(e.getMessage());
				}
			} else {
				this.servicesClient.updateUserPhenotype(phenotype.getId(), phenotype);
			}
		} catch (ClientException e) {
			try {
				switch (e.getResponseStatus()) {
					case UNAUTHORIZED:
						this.authenticationSupport.needsToLogin(req, resp);
						break;
					default:
						resp.setStatus(e.getResponseStatus().getStatusCode());
						resp.getWriter().write(e.getMessage());
				}
			} catch (IOException ex) {
				resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				LOGGER.error("IO Error writing response status", ex);
				try {
					resp.getWriter().write("Internal server error");
				} catch (IOException ignore) {
					LOGGER.error("Error writing the internal server error message: {}", ignore);
				}
			}
		} catch (IOException t) {
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			LOGGER.error("IO Error saving phenotype", t);
			try {
				resp.getWriter().write("Internal server error");
			} catch (IOException ignore) {
				LOGGER.error("Error writing the internal server error message: {}", ignore);
			}
		}
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		doPost(req, resp);
	}
}
