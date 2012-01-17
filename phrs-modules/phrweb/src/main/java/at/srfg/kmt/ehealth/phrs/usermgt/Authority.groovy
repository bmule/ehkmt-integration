package at.srfg.kmt.ehealth.phrs.usermgt

import java.io.Serializable

import org.bson.types.ObjectId

import com.google.code.morphia.annotations.Entity
import com.google.code.morphia.annotations.Id


@Entity
@groovy.transform.EqualsAndHashCode
class Authority implements Serializable{

	@Id
	ObjectId id
	
	String authority //unique
}
