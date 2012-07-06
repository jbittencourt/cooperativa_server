/*
 * Coop.java
 *
 * Created on September 19, 2003, 5:06 PM
 */

import java.util.ArrayList;

/**
 *
 * @author  maicon
 */
public class Coop {
    
    /** Creates a new instance of Coop */
    public Coop() {
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        CoopServer server = new CoopServer();
        server.loadPreferences();
        server.startMainServer();
    }
    
}
