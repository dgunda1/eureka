/*
 * #%L
 * Eureka Services
 * %%
 * Copyright (C) 2012 Emory University
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

import edu.emory.cci.aiw.cvrg.eureka.common.entity.DataElementEntity;
import edu.emory.cci.aiw.cvrg.eureka.common.entity.ExtendedDataElement;
import edu.emory.cci.aiw.cvrg.eureka.common.entity.Relation;
import edu.emory.cci.aiw.cvrg.eureka.common.entity.SequenceEntity;
import org.protempa.HighLevelAbstractionDefinition;
import org.protempa.PropertyConstraint;
import org.protempa.PropositionDefinition;
import org.protempa.TemporalExtendedPropositionDefinition;
import org.protempa.proposition.value.ValueComparator;
import org.protempa.proposition.value.ValueType;

import java.util.ArrayList;
import java.util.List;

import static edu.emory.cci.aiw.cvrg.eureka.services.conversion.PropositionDefinitionConverterUtil.unit;
import java.util.HashMap;
import java.util.Map;

final class SequenceConverter
		implements
		PropositionDefinitionConverter<SequenceEntity, HighLevelAbstractionDefinition> {

	private PropositionDefinitionConverterVisitor converterVisitor;
	private HighLevelAbstractionDefinition primary;
	private final Map<Long, TemporalExtendedPropositionDefinition> extendedProps;

	public SequenceConverter() {
		this.extendedProps = new HashMap<Long, TemporalExtendedPropositionDefinition>();
	}

	@Override
	public HighLevelAbstractionDefinition getPrimaryPropositionDefinition() {
		return primary;
	}

	public void setConverterVisitor(PropositionDefinitionConverterVisitor inVisitor) {
		converterVisitor = inVisitor;
	}

	@Override
	public List<PropositionDefinition> convert(SequenceEntity sequenceEntity) {
		List<PropositionDefinition> result = new ArrayList<PropositionDefinition>();
		HighLevelAbstractionDefinition primary = new HighLevelAbstractionDefinition(
				sequenceEntity.getKey());
		System.err.println("STARTED WITH " + sequenceEntity);
		if (sequenceEntity.getRelations() != null) {
			for (Relation rel : sequenceEntity.getRelations()) {
				DataElementEntity lhs =
						rel.getLhsExtendedDataElement().getDataElementEntity();
				lhs.accept(converterVisitor);
				result.addAll(
						converterVisitor.getPropositionDefinitions());
				TemporalExtendedPropositionDefinition tepdLhs = buildExtendedProposition(rel
						.getLhsExtendedDataElement());

				DataElementEntity rhs =
						rel.getRhsExtendedDataElement().getDataElementEntity();
				rhs.accept(converterVisitor);
				result.addAll(converterVisitor.getPropositionDefinitions());
				TemporalExtendedPropositionDefinition tepdRhs = buildExtendedProposition(rel
						.getRhsExtendedDataElement());

				primary.add(tepdLhs);
				primary.add(tepdRhs);
				primary.setRelation(tepdLhs, tepdRhs, buildRelation(rel));
			}
		}

		this.primary = primary;
		System.err.println("CONVERTED " + primary);
		result.add(primary);
		return result;
	}

	private TemporalExtendedPropositionDefinition buildExtendedProposition(
			ExtendedDataElement ep) {
		TemporalExtendedPropositionDefinition tepd = 
				this.extendedProps.get(ep.getId());
		if (tepd == null) {
			tepd = new TemporalExtendedPropositionDefinition(
					ep.getDataElementEntity().getKey());

			if (ep.getPropertyConstraint() != null) {
				PropertyConstraint pc = new PropertyConstraint();
				pc.setPropertyName(
						ep.getPropertyConstraint().getPropertyName());
				pc.setValue(ValueType.VALUE.parse(ep.getPropertyConstraint()
						.getValue()));
				pc.setValueComp(ValueComparator.EQUAL_TO);

				tepd.getPropertyConstraints().add(pc);
			}
			tepd.setMinLength(ep.getMinDuration());
			tepd.setMinLengthUnit(unit(ep.getMinDurationTimeUnit()));
			tepd.setMaxLength(ep.getMaxDuration());
			tepd.setMaxLengthUnit(unit(ep.getMaxDurationTimeUnit()));
			this.extendedProps.put(ep.getId(), tepd);
		}

		return tepd;
	}

	private org.protempa.proposition.interval.Relation buildRelation(
			Relation rel) {
		return new org.protempa.proposition.interval.Relation(null, null, null,
				null, rel.getMins1f2(), unit(rel.getMins1f2TimeUnit()),
				rel.getMaxs1f2(), unit(rel.getMaxs1f2TimeUnit()),
				rel.getMinf1s2(), unit(rel.getMinf1s2TimeUnit()),
				rel.getMaxf1s2(), unit(rel.getMaxf1s2TimeUnit()), null, null,
				null, null);
	}
}
