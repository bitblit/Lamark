import com.erigir.lamark.gui.LamarkGui;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Group;
import javafx.scene.Scene;

import javax.swing.*;


/**
 * A Applet wrapper around the LamarkGUI class to permit display on website.
 * &lt;p /&gt;
 * This is a simple JApplet class to allow the LamarkGUI class to
 * be shown on the website.  It is a companion to the LamarkApplication
 * class.  Since this is the applet, it assumes no privs, and only allows
 * display of the built-in demonstration classes.
 *
 * @author cweiss
 * @since 11/07
 */
public class LamarkApplet extends JApplet {
    private Scene scene;
    private Group root;

    /**
     * Wrapped gui object *
     */
    private LamarkGui gui = new LamarkGui(getParameter("initialLocation"), getParameter("initialSelection"));

    @Override
    public final void init() { // This method is invoked when applet is loaded
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                initSwing();
            }
        });
    }

    private void initSwing() { // This method is invoked on Swing thread
        final JFXPanel fxPanel = new JFXPanel();
        add(fxPanel);

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                initFX(fxPanel);
                initApplet();
            }
        });
    }

    private void initFX(JFXPanel fxPanel) { // This method is invoked on JavaFX thread
        root = new Group();
        scene = new Scene(root);
        root.getChildren().add(gui);
        fxPanel.setScene(scene);
    }

    public void initApplet() {
        // Add custom initialization code here
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
