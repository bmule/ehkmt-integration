/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.srfg.kmt.ehealth.phrs.security.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation associate all the business methods for
 * class a certain group or role. This annotation can be only used on classes,
 * this class can not be used on fields or methods.<br>
 * This annotation must be applied only on implementation of :
 * <ul>
 * <li> <code>RunAsGroup</code>
 * <li> <code>RunAsRole</code>
 * </ul>
 * otherwise it may have no effect. <br>
 * The next code snippet shows the usage :
 *
 * <pre>
 * public interface MyBusiness {
 *   void doStuff();
 * }
 * ....
 *
 * @RunAs
 * public class MyBean implements RunAsGroup, RunAsRole, MyBusiness {
 *
 * @Override
 * public Set<Group> getGroups() {
 *     return myGroups;
 * }
 *
 * @Override
 * public Set<Role> getRoles() {
 *     return myRoles;
 * }
 *
 * @Override
 * public void doStuff() {
 * .....
 * }
 *
 * </pre>
 *
 * When the beans business method  <code>doStuff</code>
 * (defined in the <code>MyBusiness</code> interface) runs the
 * container will assigns the myGroups and myRoles to the running
 * method. This is a convenient way to change roles/groups during
 * run-time by manipulating the result for the
 *
 * @author mradules
 * @see Role
 * @see Group
 * @see RunAsGroup
 * @see RunAsRole
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RunAs {
}
