/**
 * 
 */
package org.bravo.activitywatch.entity;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Volker Braun
 *
 */
@XmlRootElement(name="AWVersions")
public class AWVersions {

	@XmlElementWrapper(name="versions")
	@XmlElement(name="version")
	private List<AWVersion> versions;
	
	public List<AWVersion> getVersionList() {
		return versions;
	}

	public void setVersions(List<AWVersion> versions) {
		this.versions = versions;
	}

}
