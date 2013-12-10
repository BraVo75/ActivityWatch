/**
 * 
 */
package org.bravo.activitywatch.entity;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Volker Braun
 */
@XmlRootElement(name="activity")
public class Activity {
	
	private Date startDate;
	private String name;
	private Long elapsedMillis;

	/**
	 * @return the startDate
	 */
	public Date getStartDate() {
		return startDate;
	}
	/**
	 * @param startDate the startDate to set
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the elapsedMillis
	 */
	public Long getElapsedMillis() {
		return elapsedMillis;
	}
	/**
	 * @param elapsedMillis the elapsedMillis to set
	 */
	public void setElapsedMillis(Long elapsedMillis) {
		this.elapsedMillis = elapsedMillis;
	}
}
