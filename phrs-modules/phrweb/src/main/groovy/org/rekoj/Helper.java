package org.rekoj;

/**
 * Example Java class that invoke a groovy class. 
 */
public class Helper
{
    public String help(final Example example) {
        return example.show().toString();
    }
}
