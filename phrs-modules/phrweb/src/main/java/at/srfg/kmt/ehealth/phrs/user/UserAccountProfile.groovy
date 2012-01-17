package at.srfg.kmt.ehealth.phrs.user

import java.io.Serializable

import at.srfg.kmt.ehealth.phrs.model.baseform.CommonModelProps
import at.srfg.kmt.ehealth.phrs.model.basesupport.HealthProfile

import com.google.code.morphia.annotations.Entity
import com.google.code.morphia.annotations.Reference

@Entity
/**
 * deprecated
 */
public class  UserAccountProfile  extends CommonModelProps implements Serializable {
		
	String userId
	String fedEmail
	String fedExt2RequestToken
	String fedClaimedId //required, then we make a userId based on this
	String fedType
/*
http://code.google.com/intl/de/apis/accounts/docs/OpenID.html
openid.claimed_id
openid.ax.value.email
openid.ext2.request_token	 
 */
	String expired
	Boolean disabledAccount
	String userLabel //any label user wants
	
	Map<String,String> params =[:]
	@Reference
	HealthProfile healthProfile
	
	public UserAccountProfile(){
		
	}
	public UserAccountProfile(Map params){
		
		if(params) map.putAll(params)
	}
	
	
}
