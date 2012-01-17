package at.srfg.kmt.ehealth.phrs.model.basesupport

import at.srfg.kmt.ehealth.phrs.model.baseform.CommonModelProps;

import com.google.code.morphia.annotations.Entity

@Entity
public class Order extends CommonModelProps{
		private String _orderName;
		private String _orderNumber;
		private String test1
		private String test2
		
		public Order(){}
		
		public void setOrderName(String o) {
		_orderName = o;
		}
		public String getOrderName() {
			return _orderName;
		}
		public void setOrderNumber(String n) {
			_orderNumber = n;
		}
		public String getOrderNumber() {
			return _orderNumber;
		}
		public String getTest1(){
			return test1;
		}
		
		public String getTest2(){
			return test2;
		}
		public void setTest1(String test1){
			this.test1=test1
		}
		public void setTest2(String test2){
			this.test2=test2
		}
		
	}
