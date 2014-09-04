import com.erigir.lamark.MyFirstLamarkCLI;
import com.erigir.lamark.config.IntrospectLamarkFactory;
import com.erigir.lamark.gui.LamarkGui;

import javax.swing.*;


/**
 * A Applet wrapper around the LamarkGUI class to permit display on website.
 * <p/>
 * This is a simple JApplet class to allow the LamarkGUI class to
 * be shown on the website.  It is a companion to the LamarkApplication
 * class.  Since this is the applet, it assumes no privs, and only allows
 * display of the built-in demonstration classes.
 *
 * @author cweiss
 * @since 11/07
 */
public class LamarkApplet extends JApplet {
    /**
     * Wrapped gui object *
     * TODO: impl dynamic feature
     */
    private LamarkGui gui = new LamarkGui(new IntrospectLamarkFactory(new MyFirstLamarkCLI()));

    /**
     * Bootstraps the LamarkGUI, and if a resource was supplied, load it.
     */
    @Override
    public void init() {
        super.init();
        add(gui);
    }

    /**
     * Stop the applet, and abort the runner if its running.
     */
    @Override
    public void stop() {
        gui.abortIfRunning();
        super.stop();
    }

    /**
     * Destroy the applet, and abort the runner if its running.
     */
    @Override
    public void destroy() {
        gui.abortIfRunning();
        super.destroy();
    }

}
