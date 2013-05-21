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
package edu.emory.cci.aiw.cvrg.eureka.services.config;

import java.util.List;

import org.junit.Test;

import com.google.inject.Module;

import edu.emory.cci.aiw.cvrg.eureka.common.test.AbstractTest;

import junit.framework.Assert;

public class ServicePropertiesTest extends AbstractTest {

	@Override
	protected Module[] getModules() {
		return new Module[] { new AppTestModule() };
	}

	@Test
	public void testDefaultSystemProps () {
		ServiceProperties properties = this.getInstance(ServiceProperties
			.class);
		List<String> propositionNames = properties
			.getDefaultSystemPropositions();
		for (String propositionName : propositionNames) {
			System.out.println("\"" + propositionName + "\"");
		}
		Assert.assertEquals(32,propositionNames.size());
	}
}
