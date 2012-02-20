package at.srfg.kmt.ehealth.phrs.model.baseform;

import at.srfg.kmt.ehealth.phrs.PhrsConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * Model to support viewing of shared information One row per resourceType
 * 
 */
public class MonitorInfoItem {
	// current user
    private String currentUserId;
    private String currentUserOwnerUri;
    private String currentUserRole;

	// boolean currentUserAllowedViewContent=false;

    private String ownerUri;
    private String protocolId;
    private String name;

    private String pixQueryIdUser;
    private String pixQueryIdNamespace;

    private int sortOrder=2;

    private String resourceType;// consent
	// String resourceTypeLabelCode;//I18
	// row and content display
    private boolean allowedViewRow = false;
	// local permission result
    private boolean allowedViewContent = false;
	// external tool has priority
    private boolean consentViewContent = false;

    private String message;
    private List<String> messages = new ArrayList<String>();

    private String cacheId;

    public int getSortOrder() {
        return sortOrder;
    }
    public void addMessages(String message){
         messages.add(message);
    }
    public List<String> getMessages(){
        return messages;
    }
    public String toHtmlMessages(){
        StringBuffer sb = new StringBuffer();
        for(String m:messages){
            sb.append(m);
            if(sb.length()>1)  sb.append("<br/>");
       }

        return sb.toString();
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

    public boolean getAllowedViewRow() {

		return allowedViewRow || consentViewContent || allowedViewContent;
	}

	public boolean isAllowedViewRow() {
		return getAllowedViewRow();
	}

	public boolean getAllowedViewContent() {
		// override when true
		return allowedViewContent || consentViewContent;
	}

	public boolean isAllowedViewContent() {
		return getAllowedViewContent();
	}

	public void setAllowedViewContent(boolean allowedViewContent) {
		this.allowedViewContent = allowedViewContent;
	}

	public boolean getConsentViewContent() {

		return consentViewContent;
	}

	public boolean isConsentViewContent() {
		return consentViewContent;
	}

	public void setConsentViewContent(boolean consentViewContent) {
		this.consentViewContent = consentViewContent;
	}

	public void setAllowedViewRow(boolean allowedViewRow) {
		this.allowedViewRow = allowedViewRow;
	}

	public String getOwnerUri() {
		return ownerUri;
	}

	public void setOwnerUri(String ownerUri) {
		this.ownerUri = ownerUri;
	}

	public String getProtocolId() {
		return protocolId;
	}

	public void setProtocolId(String protocolId) {
		this.protocolId = protocolId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getResourceType() {
		return resourceType;
	}

	public void setResourceType(String resourceType) {
		this.resourceType = resourceType;
	}

	/**
	 * Takes resourse type code and converts it a I18 code Must update I18 files
	 * for new codes!
	 * 
	 * @return
	 */
	public String getResourceTypeLabelCode() {
		String code = PhrsConstants.AUTHORIZE_RESOURCE_CODE_PHRS_UNKNOWN;// default

		if (getResourceType() != null) {
			code = getResourceType();
			code = code.replace(":", "_");

		}
		return code;
	}

	/*
	 * public String getResourceTypeLabelCode() { return resourceTypeLabelCode;
	 * }
	 * 
	 * public void setResourceTypeLabelCode(String resourceTypeLabelCode) {
	 * this.resourceTypeLabelCode = resourceTypeLabelCode; }
	 */

	public String getCacheId() {
		return cacheId;
	}

	public void setCacheId(String cacheId) {
		this.cacheId = cacheId;
	}

	public String getCurrentUserId() {
		return currentUserId;
	}

	public void setCurrentUserId(String currentUserId) {
		this.currentUserId = currentUserId;
	}

	public String getCurrentUserOwnerUri() {
		return currentUserOwnerUri;
	}

	public void setCurrentUserOwnerUri(String currentUserOwnerUri) {
		this.currentUserOwnerUri = currentUserOwnerUri;
	}

	public String getCurrentUserRole() {
		return currentUserRole;
	}

	public void setCurrentUserRole(String currentUserRole) {
		this.currentUserRole = currentUserRole;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}


    public String getPixQueryIdUser() {
        return pixQueryIdUser;
    }

    public void setPixQueryIdUser(String pixQueryIdUser) {
        this.pixQueryIdUser = pixQueryIdUser;
    }

    public String getPixQueryIdNamespace() {
        return pixQueryIdNamespace;
    }

    public void setPixQueryIdNamespace(String pixQueryIdNamespace) {
        this.pixQueryIdNamespace = pixQueryIdNamespace;
    }


}
