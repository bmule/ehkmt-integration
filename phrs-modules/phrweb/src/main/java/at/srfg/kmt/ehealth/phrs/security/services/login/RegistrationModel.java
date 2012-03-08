package at.srfg.kmt.ehealth.phrs.security.services.login;


import at.srfg.kmt.ehealth.phrs.PhrsConstants;

import java.util.Date;

public class RegistrationModel {

    private Date createDate;

    private String openId;
    private String fullName;
    private String emailAddress;
    private String phoneNumber;
    private String role;
    private String dateOfBirth;
    private String is_verified;

    private String nickname;

    private String provider;
    private String claimedId;

    private String localShortId;
    
    private String lastName;
    private String firstName;

    public RegistrationModel() {
        createDate = new Date();
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    /**
     * @return fullname, otherwise nickname
     */
    public String getFullName() {
        if (fullName != null && !fullName.isEmpty()) return fullName;
        return  getGreetName();
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public void setIs_verified(String is_verified) {
        this.is_verified = is_verified;
    }

    public String getIs_verified() {
        return is_verified;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Only particular roles are accepted and transformed to known PHR roles
     * @param role
     */
    public void setRole(String role) {
        this.role = LoginUtils.processRole(role);
        //this.role = role;
    }

    public String getRole() {
        if(openId !=null && openId.contains("xrypa")){
            //
            role= PhrsConstants.AUTHORIZE_ROLE_SUBJECT_CODE_DOCTOR;
        }
        return role;
    }
    public String getGreetName(){
        if (nickname != null && !nickname.isEmpty()) return nickname;
        if (fullName != null && !fullName.isEmpty()) return fullName;
        if (firstName != null && !firstName.isEmpty()) return firstName;
        return lastName;
    }
    /**
     * @return nickname otherwise full name
     */
    public String getNickname() {
        if (nickname != null && !nickname.isEmpty()) return nickname;
        return getGreetName();
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getClaimedId() {
        if (claimedId != null && !claimedId.isEmpty()) return claimedId;
        return openId;
    }

    public void setClaimedId(String claimedId) {
        this.claimedId = claimedId;
    }

    /**
     * @return If none, returns openId
     */
    public String getLocalShortId() {
        if (localShortId != null && !localShortId.isEmpty()) return localShortId;

        return openId;
    }


    public void setLocalShortId(String localShortId) {
        this.localShortId = localShortId;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;

    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }
}

