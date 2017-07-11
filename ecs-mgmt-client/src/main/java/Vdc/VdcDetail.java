/**
 * 
 */
package Vdc;

import java.net.URI;
import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.emc.ecs.management.entity.Vdc;

/**
 * @author nlengc
 *
 */
@XmlRootElement(name = "vdc")
public class VdcDetail {

	public final static String VDC_ID = "vdcId";
	public final static String VDC_NAME = "vdcName";
	public final static String INTER_VDC_END_POINTS = "interVdcEndPoints";
	public final static String INTER_VDC_CMD_END_POINTS = "interVdcCmdEndPoints";
	public final static String SECRET_KEYS = "secretKeys";
	public final static String PERMANENTLY_FAILED = "permanentlyFailed";
	public final static String LOCAL = "local";
	public final static String MGMT_END_POINTS = "managementEndPoints";

	public final static String NAME = "name";
	public final static String ID = "id";
	public final static String LINK = "link";
	public final static String INACTIVE = "inactive";
	public final static String GLOBAL = "global";
	public final static String REMOTE = "remote";
	public final static String VDC = "vdc";
	public final static String INTERNAL = "internal";
	public final static String CREATION_TIME = "creation_time";

	private String vdcId;
	private String vdcName;
	private String interVdcEndPoints;
	private String interVdcCmdEndPoints;
	private String secretKeys;
	private Boolean permanentlyFailed;
	private Boolean local;
	private String managementEndPoints;
	private String name;
	private URI id;
	private String link;
	private Date creationTime;
	private Boolean inactive;
	private Boolean global;
	private Boolean remote;
	private Vdc vdc;
	private Boolean internal;

	@XmlElement(name = VDC_ID)
	public String getVdcId() {
		return vdcId;
	}

	public void setVdcId(String vdcId) {
		this.vdcId = vdcId;
	}

	@XmlElement(name = VDC_NAME)
	public String getVdcName() {
		return vdcName;
	}

	public void setVdcName(String vdcName) {
		this.vdcName = vdcName;
	}

	@XmlElement(name = INTER_VDC_END_POINTS)
	public String getInterVdcEndPoints() {
		return interVdcEndPoints;
	}

	public void setInterVdcEndPoints(String interVdcEndPoints) {
		this.interVdcEndPoints = interVdcEndPoints;
	}

	@XmlElement(name = INTER_VDC_CMD_END_POINTS)
	public String getInterVdcCmdEndPoints() {
		return interVdcCmdEndPoints;
	}

	public void setInterVdcCmdEndPoints(String interVdcCmdEndPoints) {
		this.interVdcCmdEndPoints = interVdcCmdEndPoints;
	}

	@XmlElement(name = SECRET_KEYS)
	public String getSecretKeys() {
		return secretKeys;
	}

	public void setSecretKeys(String secretKeys) {
		this.secretKeys = secretKeys;
	}

	@XmlElement(name = PERMANENTLY_FAILED)
	public Boolean getPermanentlyFailed() {
		return permanentlyFailed;
	}

	public void setPermanentlyFailed(Boolean permanentlyFailed) {
		this.permanentlyFailed = permanentlyFailed;
	}

	@XmlElement(name = LOCAL)
	public Boolean getLocal() {
		return local;
	}

	public void setLocal(Boolean local) {
		this.local = local;
	}

	@XmlElement(name = MGMT_END_POINTS)
	public String getManagementEndPoints() {
		return managementEndPoints;
	}

	public void setManagementEndPoints(String managementEndPoints) {
		this.managementEndPoints = managementEndPoints;
	}

	@XmlElement(name = NAME)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@XmlElement(name = ID)
	public URI getId() {
		return id;
	}

	public void setId(URI id) {
		this.id = id;
	}

	@XmlElement(name = LINK)
	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	@XmlElement(name = CREATION_TIME)
	public Date getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(Date creationTime) {
		this.creationTime = creationTime;
	}

	@XmlElement(name = CREATION_TIME)
	public Boolean getInactive() {
		return inactive;
	}

	public void setInactive(Boolean inactive) {
		this.inactive = inactive;
	}

	@XmlElement(name = GLOBAL)
	public Boolean getGlobal() {
		return global;
	}

	public void setGlobal(Boolean global) {
		this.global = global;
	}

	@XmlElement(name = REMOTE)
	public Boolean getRemote() {
		return remote;
	}

	public void setRemote(Boolean remote) {
		this.remote = remote;
	}

	@XmlElement(name = VDC)
	public Vdc getVdc() {
		return vdc;
	}

	public void setVdc(Vdc vdc) {
		this.vdc = vdc;
	}

	@XmlElement(name = INTERNAL)
	public Boolean getInternal() {
		return internal;
	}

	public void setInternal(Boolean internal) {
		this.internal = internal;
	}

}
