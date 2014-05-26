package org.bravo.activitywatch.entity;

import java.net.URL;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Volker Braun
 */
@XmlRootElement(name="version")
public class AWVersion {

	private Long id;
	private String versionNumber;
	private String storeVersion;
	private VersionPath path;
	private URL url;
	private Platform platform;

	public enum Platform {
		WIN, MAC, UNIX;
	}

	public enum VersionPath {
		BETA, RELEASE;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getVersionNumber() {
		return versionNumber;
	}

	public void setVersionNumber(String versionNumber) {
		this.versionNumber = versionNumber;
	}

	public String getStoreVersion() {
		return storeVersion;
	}

	public void setStoreVersion(String storeVersion) {
		this.storeVersion = storeVersion;
	}

	public VersionPath getPath() {
		return path;
	}

	public void setPath(VersionPath path) {
		this.path = path;
	}

	public URL getUrl() {
		return url;
	}

	public void setUrl(URL url) {
		this.url = url;
	}
	
	public Platform getPlatform() {
		return platform;
	}

	public void setPlatform(Platform platform) {
		this.platform = platform;
	}
}
