package gr.forth.ics.icardea.pid;

public class iCARDEA_Patient_CONSTANTS  {
	
	public static final String ID_PREFIX = "id:";
	/* The following are used for the QPD-3 segment 
	 * (QIP - query input parameter list) of the 
	 * IHE's Patient Demographic Query
	 * @PID.3.1^6537 54210~@PID.3.4.1^ADT_ESEL~@PID.3.4.2^1.3.6.1.4.1.21367.2008. 2.3.9~@PID.3.4.3^ISO
	 * msg.addQueryPatientID("653754210", "ADT_ESEL", "1.3.6.1.4.1.21367.2008.2.3.9", "ISO");
	 * 
	 * ID_SEG_FLD
	 * IDNS_SEG_FLD icardea  - will this query on the protocol id?
	 * 
	 */
	public static final String ID_SEG_FLD = "@PID.3.1";
	public static final String IDNS_SEG_FLD = "@PID.3.4.1";
	public static final String IDOID_SEG_FLD = "@PID.3.4.2";
	public static final String IDTYPE_SEG_FLD = "@PID.3.4.3";
	public static final String FNAME_SEG_FLD = "@PID.5.1.1";
	public static final String GNAME_SEG_FLD = "@PID.5.2";
	public static final String MOT_FNAME_SEG_FLD = "@PID.6.1.1";
	public static final String MOT_GNAME_SEG_FLD = "@PID.6.2";
	public static final String DOB_SEG_FLD = "@PID.7.1";
	public static final String SEX_SEG_FLD = "@PID.8";
    public static final String ADDR_STREET_SEG_FLD = "@PID.11.1.1";
	public static final String ADDR_CITY_SEG_FLD = "@PID.11.3";
	public static final String ADDR_STATE_SEG_FLD = "@PID.11.4";
	public static final String ADDR_ZIP_SEG_FLD = "@PID.11.5";
	public static final String ADDR_COUNTRY_SEG_FLD = "@PID.11.6";
	public static final String ADDR_TYPE_SEG_FLD = "@PID.11.7";
	public static final String ACCNUM_SEG_FLD = "@PID.18.1";
	

}
