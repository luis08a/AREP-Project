
package edu.eci.arep.framework.webServices;

import edu.eci.arep.framework.core.Web;

/**
 * WebServiceHello
 */
public class WebServiceHello {

    @Web("cuadrado")
    public static String square() {
        return "<p>Hello</p>";
    }
    
    public static String square(String p) {
        return "<p>"+p+"</p>";
    }
}