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
package edu.emory.cci.aiw.cvrg.eureka.services.conversion;

import edu.emory.cci.aiw.cvrg.eureka.common.entity.ExtendedDataElement;
import edu.emory.cci.aiw.cvrg.eureka.common.entity.FrequencyEntity;
import edu.emory.cci.aiw.cvrg.eureka.common.entity.SystemProposition;
import edu.emory.cci.aiw.cvrg.eureka.common.entity.SystemProposition.SystemType;
import edu.emory.cci.aiw.cvrg.eureka.common.entity.TimeUnit;
import edu.emory.cci.aiw.cvrg.eureka.services.test.AbstractServiceTest;
import org.junit.Before;
import org.junit.Test;
import org.protempa.PropositionDefinition;

import java.util.List;
import java.util.Set;
import org.junit.After;

import static org.junit.Assert.assertEquals;
import org.protempa.HighLevelAbstractionDefinition;
import org.protempa.SimpleGapFunction;
import org.protempa.TemporalExtendedPropositionDefinition;
import org.protempa.proposition.interval.Relation;
import org.protempa.proposition.value.AbsoluteTimeUnit;

public class FrequencySliceAbstractionConverterTest extends
		AbstractServiceTest {
	private SystemProposition event;
	private FrequencyEntity frequency;
	private HighLevelAbstractionDefinition sliceDef;
	private List<PropositionDefinition> propDefs;
	private SimpleGapFunction gf;

	@Before
	public void setUp() {
		PropositionDefinitionConverterVisitor converterVisitor = 
				this.getInstance
				(PropositionDefinitionConverterVisitor.class);
		FrequencyNonConsecutiveConverter converter = 
				new FrequencyNonConsecutiveConverter();
		converter.setVisitor(converterVisitor);
		
		event = new SystemProposition();
		event.setId(1L);
		event.setKey("test-event1");
		event.setDisplayName("test-event1-display");
		event.setDescription("test-event1-abbrev");
		event.setInSystem(true);
		event.setSystemType(SystemType.EVENT);
		
		TimeUnit dayUnit = new TimeUnit();
		dayUnit.setName("day");
		
		frequency = new FrequencyEntity();
		frequency.setId(2L);
		frequency.setKey("test-slice-key");
		frequency.setAtLeastCount(3);
		frequency.setWithinAtLeast(1);
		frequency.setWithinAtLeastUnits(dayUnit);
		frequency.setWithinAtMost(90);
		frequency.setWithinAtMostUnits(dayUnit);
		
		ExtendedDataElement af = new ExtendedDataElement();
		af.setDataElementEntity(event);
		
		frequency.setExtendedProposition(af);
		propDefs = converter.convert(frequency);
		sliceDef = converter.getPrimaryPropositionDefinition();
		gf = (SimpleGapFunction) sliceDef.getGapFunction();
	}
	
	@After
	public void tearDown() {
		event = null;
		frequency = null;
		sliceDef = null;
		propDefs = null;
		gf = null;
	}
	
	@Test
	public void testNumberOfPropositionDefinitionsCreated() {
		assertEquals("wrong number of proposition definitions created", 1,
				propDefs.size());
	}
	
	@Test
	public void testId() {
		assertEquals("wrong ID", 
				frequency.getKey() + ConversionUtil.PRIMARY_PROP_ID_SUFFIX, 
				sliceDef.getId());
	}
	
	@Test
	public void testMinIndex() {
		assertEquals("wrong min index", 3, sliceDef.getExtendedPropositionDefinitions().size());
	}
	
	@Test
	public void testAbstractedFromSize() {
		assertEquals("wrong abstracted from size", 
				1, sliceDef.getAbstractedFrom().size());
	}
	
	@Test
	public void testAbstractedFrom() {
		assertEquals("wrong abstracted from", event.getKey(),
				sliceDef.getAbstractedFrom().iterator().next());
	}
	
	@Test
	public void testGapMax() {
		assertEquals("wrong gap max", Integer.valueOf(0), gf.getMaximumGap());
	}
	
	@Test
	public void testGapMaxUnit() {
		assertEquals("wrong gap max unit", null, gf.getMaximumGapUnit());
	}
	
	@Test
	public void minDistanceBetween() {
		Set<List<TemporalExtendedPropositionDefinition>> tepdPairs = 
				sliceDef.getTemporalExtendedPropositionDefinitionPairs();
		for (List<TemporalExtendedPropositionDefinition> tepdPair : tepdPairs) {
			Relation rel = sliceDef.getRelation(tepdPair);
			assertEquals("wrong min distance between", Integer.valueOf(1), rel.getMinDistanceBetween());
		}
	}
	
	@Test
	public void minDistanceBetweenUnits() {
		Set<List<TemporalExtendedPropositionDefinition>> tepdPairs = 
				sliceDef.getTemporalExtendedPropositionDefinitionPairs();
		for (List<TemporalExtendedPropositionDefinition> tepdPair : tepdPairs) {
			Relation rel = sliceDef.getRelation(tepdPair);
			assertEquals("wrong min distance between units", AbsoluteTimeUnit.DAY, rel.getMinDistanceBetweenUnits());
		}
	}
}
