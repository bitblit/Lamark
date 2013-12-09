import javax.swing.JApplet;

import com.erigir.lamark.gui.LamarkGui;


/**
 * A Applet wrapper around the LamarkGUI class to permit display on website.
 * 
 * This is a simple JApplet class to allow the LamarkGUI class to
 * be shown on the website.  It is a companion to the LamarkApplication
 * class.  Since this is the applet, it assumes no privs, and only allows 
 * display of the built-in demonstration classes. 
 * 
 * @author cweiss
 * @since 11/07
 *
 */
public class LamarkApplet extends JApplet
{
    /** Wrapped gui object **/
    private LamarkGui gui = new LamarkGui();
    
    /**
     * Bootstraps the LamarkGUI, and if a resource was supplied, load it.
     */
    @Override
    public void init()
    {
        super.init();
        String openResource = getParameter("resource");
        add(gui);
        if (openResource!=null)
        {
            gui.openPropertyResource(openResource);
        }
    }

    /**
     * Stop the applet, and abort the runner if its running.
     */
    @Override
    public void stop()
    {
        gui.abortIfRunning();
        super.stop();
    }

    /**
     * Destroy the applet, and abort the runner if its running.
     */
    @Override
    public void destroy()
    {
        gui.abortIfRunning();
        super.destroy();
    }

}
