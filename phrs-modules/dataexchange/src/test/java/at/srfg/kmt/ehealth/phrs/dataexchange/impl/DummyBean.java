/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/*
 * Project :iCardea
 * File : DummyBean.java 
 * Encoding : UTF-8
 * Date : Mar 31, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.dataexchange.impl;

import java.util.Date;

/**
 * Test bean used only by test purposes.
 * 
 * @version 0.1
 * @since 0.1
 * @author Mihai
 */
public class DummyBean {
    private String name;
    // please notice that I use the primitive wrapper
    private Boolean superHero;
    private Date firstApperance;

    public Date getFirstApperance() {
        return firstApperance;
    }

    public void setFirstApperance(Date firstApperance) {
        this.firstApperance = firstApperance;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean isSuperHero() {
        return superHero;
    }

    public void setSuperHero(boolean superHero) {
        this.superHero = superHero;
    }
    
}
