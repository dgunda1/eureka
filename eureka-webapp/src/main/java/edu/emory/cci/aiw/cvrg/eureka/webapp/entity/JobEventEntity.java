/*
 * #%L
 * Eureka Common
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
package edu.emory.cci.aiw.cvrg.eureka.webapp.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.codehaus.jackson.annotate.JsonBackReference;

import javax.persistence.Column;
import javax.persistence.Lob;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.eurekaclinical.eureka.client.comm.JobEvent;
import org.eurekaclinical.eureka.client.comm.JobStatus;

/**
 * An event associated with a job.
 *
 * @author hrathod
 *
 */
@Entity
@Table(name = "job_events")
public class JobEventEntity {

	/**
	 * The unique identifier for the job event.
	 */
	@Id
	@SequenceGenerator(name = "JOBEVENT_SEQ_GENERATOR",
			sequenceName = "JOBEVENT_SEQ", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE,
			generator = "JOBEVENT_SEQ_GENERATOR")
	private Long id;
	/**
	 * The job for which the event was generated.
	 */
	@ManyToOne
	@JoinColumn(name = "job_id", nullable = false)
	private JobEntity job;
	/**
	 * The status of the event.
	 */
	@Column(nullable = false, name="state")
	private JobStatus status;

	/**
	 * The exception stack trace. The name is prefixed with a z to force
	 * hibernate to populate this field last in insert and update statements to
	 * avoid the dreaded
	 * <code>ORA-24816: Expanded non LONG bind data supplied after actual LONG or LOB column</code>
	 * error message from Oracle. Hibernate apparently orders fields
	 * alphabetically.
	 */
	@Lob
	@Column(name = "exceptionstacktrace")
	private String zExceptionStackTrace;
	/**
	 * The time stamp for the event.
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable = false)
	private Date timeStamp;
	/**
	 * The message generated for the event.
	 */
	@Lob
	private String message;

	public JobEventEntity() {
		this.timeStamp = new Date();
	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return this.id;
	}

	/**
	 * @param inId the id to set
	 */
	public void setId(Long inId) {
		this.id = inId;
	}

	/**
	 * @return the job
	 */
	@JsonBackReference("job-event")
	public JobEntity getJob() {
		return this.job;
	}

	/**
	 * @param inJob the job to set
	 */
	public void setJob(JobEntity inJob) {
		if (this.job != inJob) {
			if (this.job != null) {
				this.job.removeJobEvent(this);
			}
			this.job = inJob;
			if (this.job != null) {
				this.job.addJobEvent(this);
			}
		}
	}

	/**
	 * @return the status
	 */
	public JobStatus getStatus() {
		return this.status;
	}

	/**
	 * @param inStatus the status to set
	 */
	public void setStatus(JobStatus inStatus) {
		this.status = inStatus;
	}

	/**
	 * @return the exceptionStackTrace
	 */
	public String getExceptionStackTrace() {
		return this.zExceptionStackTrace;
	}

	/**
	 * @param inExceptionStackTrace the exceptionStackTrace to set
	 */
	public void setExceptionStackTrace(String inExceptionStackTrace) {
		this.zExceptionStackTrace = inExceptionStackTrace;
	}

	/**
	 * @return the timeStamp
	 */
	public Date getTimeStamp() {
		return new Date(this.timeStamp.getTime());
	}

	/**
	 * @param inTimeStamp the timeStamp to set
	 */
	public void setTimeStamp(Date inTimeStamp) {
		if (inTimeStamp == null) {
			this.timeStamp = new Date();
		} else {
			this.timeStamp = new Date(inTimeStamp.getTime());
		}
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return this.message;
	}

	/**
	 * @param inMessage the message to set
	 */
	public void setMessage(String inMessage) {
		this.message = inMessage;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
	public JobEvent toJobEvent() {
		JobEvent jobEvent = new JobEvent();
		jobEvent.setId(this.id);
		jobEvent.setMessage(this.message);
		jobEvent.setStatus(this.status);
		jobEvent.setTimeStamp(this.timeStamp);
		jobEvent.setExceptionStackTrace(this.zExceptionStackTrace);
		return jobEvent;
	}

}
