
package edu.eci.arep.framework.webServices;

import edu.eci.arep.framework.core.Web;

/**
 * WebServiceHello
 */
public class WebServiceHello {

    @Web("cuadrado")
    public static String square() {
        return "<p>Hello i'm a simple square</p>";
    }
    @Web("template")
    public static String textTemplate(String p) {
        return "<h1>I could be an awesome template</h1><p>with the text from the query string: "+p+"</p>";
    }
}