/*
 * #%L
 * Eureka Common
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
package edu.emory.cci.aiw.cvrg.eureka.common.entity;

import edu.emory.cci.aiw.cvrg.eureka.common.comm.DataElement;

public final class PropositionTypeVisitor implements PropositionEntityVisitor {

	private DataElement.Type type;
	
	public DataElement.Type getType() {
		return type;
	}
	
	@Override
    public void visit(SystemProposition proposition) {
		this.type = DataElement.Type.SYSTEM;
    }

	@Override
	public void visit(Categorization categorization) {
		this.type = DataElement.Type.CATEGORIZATION;
	}

	@Override
	public void visit(HighLevelAbstraction highLevelAbstraction) {
		this.type = DataElement.Type.SEQUENCE;
	}

}