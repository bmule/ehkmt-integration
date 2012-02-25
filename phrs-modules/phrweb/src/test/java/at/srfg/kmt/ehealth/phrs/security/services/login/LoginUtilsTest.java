package at.srfg.kmt.ehealth.phrs.security.services.login;

import at.srfg.kmt.ehealth.phrs.PhrsConstants;
import junit.framework.TestCase;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;


public class LoginUtilsTest extends TestCase {
    @Test
    public void testTransformRole() throws Exception {
        Object roleString="xxxnurseyyy";
        String actual = LoginUtils.processRole( roleString);
        assertNotNull("1 process role list return null",actual.contains("doctor"));

        assertEquals("1 Expected Nurse role ",
                PhrsConstants.AUTHORIZE_ROLE_SUBJECT_CODE_NURSE,
                actual);

        roleString="test1122344";
        actual = LoginUtils.processRole( roleString);
        assertNull("2 process role should be null, because we passed a unknown role", actual);

        //chooses Doctor as first role
        List list=new ArrayList();
        roleString="xxxdoctoryyy";
        list.add("xxxx");
        list.add(roleString);
        list.add("yynurseuu");

        actual = LoginUtils.processRole( list);
        assertNotNull("3 process role list return null", actual.contains("doctor"));

        assertEquals("3 Expected Doctor role ",
                PhrsConstants.AUTHORIZE_ROLE_SUBJECT_CODE_DOCTOR,
                actual);

        
    }

}
