package at.srfg.kmt.ehealth.phrs.model.basesupport

import org.bson.types.ObjectId

import com.google.code.morphia.annotations.Entity
import com.google.code.morphia.annotations.Id

@Entity
	public class Person {
		@Id
		ObjectId id
		public String name;
		public String title;

		public Person(String name, String title) {
			this.name = name;
			this.title = title;
		}

		public Person(){
			
		}
	}
