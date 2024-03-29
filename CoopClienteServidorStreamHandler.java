/*
 * File:           CoopClienteServidorStreamHandler.java
 * Date:           December 5, 2003  1:54 PM
 *
 * @author  maicon
 * @version generated by NetBeans XML module
 */
/*
 * File:           CoopClienteServidorStreamHandler.java
 * Date:           September 25, 2003  4:32 PM
 *
 * @author  maicon
 * @version generated by NetBeans XML module
 */
import org.xml.sax.*;

public interface CoopClienteServidorStreamHandler {
    
    /**
     * An empty element event handling method.
     * @param data value or null
     *
     */
    public void handle_change_object_state(final Attributes meta) throws SAXException;
    
    /**
     * An empty element event handling method.
     * @param data value or null
     *
     */
    public void handle_request_enter_cenario(final Attributes meta) throws SAXException;
    
    /**
     * An empty element event handling method.
     * @param data value or null
     *
     */
    public void handle_identify(final Attributes meta) throws SAXException;
    
    /**
     * A container element start event handling method.
     * @param meta attributes
     *
     */
    public void start_talk(final Attributes meta) throws SAXException;
    
    /**
     * A data element event handling method.
     * @param data value or null
     * @param meta attributes
     *
     */
    public void handle_talk(final java.lang.String data, final Attributes meta) throws SAXException;
    
    /**
     * A container element end event handling method.
     *
     */
    public void end_talk() throws SAXException;
    
    /**
     * An empty element event handling method.
     * @param data value or null
     *
     */
    public void handle_inventory_add_item(final Attributes meta) throws SAXException;
    
    /**
     * An empty element event handling method.
     * @param data value or null
     *
     */
    public void handle_move_to(final Attributes meta) throws SAXException;
    
    /**
     * An empty element event handling method.
     * @param data value or null
     *
     */
    public void handle_inventory_drop_item(final Attributes meta) throws SAXException;
    
    /**
     * A container element start event handling method.
     * @param meta attributes
     *
     */
    public void start_cooperativa_cliente_servidor(final Attributes meta) throws SAXException;
    
    /**
     * A container element end event handling method.
     *
     */
    public void end_cooperativa_cliente_servidor() throws SAXException;
    
    /**
     * A container element start event handling method.
     * @param meta attributes
     *
     */
    public void start_navigate(final Attributes meta) throws SAXException;
    
    /**
     * A container element end event handling method.
     *
     */
    public void end_navigate() throws SAXException;
    
    /**
     * An empty element event handling method.
     * @param data value or null
     *
     */
    public void handle_logout(final Attributes meta) throws SAXException;
    
    /**
     * An empty element event handling method.
     * @param data value or null
     *
     */
    public void handle_enter_cenario(final Attributes meta) throws SAXException;
    
    /**
     * A container element start event handling method.
     * @param meta attributes
     *
     */
    public void start_chat(final Attributes meta) throws SAXException;
    
    /**
     * A container element end event handling method.
     *
     */
    public void end_chat() throws SAXException;
    
    /**
     * An empty element event handling method.
     * @param data value or null
     *
     */
    public void handle_create_user(final Attributes meta) throws SAXException;
    
}

