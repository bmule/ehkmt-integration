package at.srfg.kmt.ehealth.phrs.usermgt

import org.apache.commons.lang.builder.HashCodeBuilder

import com.google.code.morphia.annotations.Entity;

/**
 * 
 * @deprecated
 *
 */
//@Entity
@groovy.transform.EqualsAndHashCode
class UserPersonAuthority implements Serializable {

	UserPerson userPerson
	Authority authority

	boolean equals(other) {
		if (!(other instanceof UserPersonAuthority)) {
			return false
		}
//		if(other && other.userPerson )
//			return other.userPerson.id == userPerson.id && other.authority.id == authority.id
		return false
	}

	int hashCode() {
		def builder = new HashCodeBuilder()
		if (userPerson) builder.append(userPerson.id)
		if (authority) builder.append(authority.id)
		builder.toHashCode()
	}

	static UserPersonAuthority get(long userPersonId, long authorityId) {
		//find 'from UserPersonAuthority where userPerson.id=:userPersonId and authority.id=:authorityId',
		//	[userPersonId: userPersonId, authorityId: authorityId]
	}

	static UserPersonAuthority create(UserPerson userPerson, Authority authority, boolean flush = false) {

		UserPersonAuthority upa =new UserPersonAuthority(userPerson: userPerson, authority: authority)
		//upa.save(flush: flush, insert: true)
		return upa
	}

	static boolean remove(UserPerson userPerson, Authority authority, boolean flush = false) {
		UserPersonAuthority instance
		//instance = UserPersonAuthority.findByUserPersonAndAuthority(userPerson, authority)
		if (!instance) {
			return false
		}

		//instance.delete(flush: flush)
		return true
	}

	static void removeAll(UserPerson userPerson) {
		//executeUpdate 'DELETE FROM UserPersonAuthority WHERE userPerson=:userPerson', [userPerson: userPerson]
	}

	static void removeAll(Authority authority) {
		//executeUpdate 'DELETE FROM UserPersonAuthority WHERE authority=:authority', [authority: authority]
	}


}
