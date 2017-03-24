package assigment.ui;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Entry point to notes application using javafx.
 * 
 * @author sergey
 *
 */
public class FXApplication extends Application
{

    private AnnotationConfigApplicationContext ctx;

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        ctx.getBean(ScenesCoordinator.class).init(primaryStage).showMain();
    }

    @Override
    public void init() throws Exception
    {
        ctx = new AnnotationConfigApplicationContext(ScenesCoordinator.class);
    }

    @Override
    public void stop() throws Exception
    {
        ctx.close();
    }

    /**
     * @param args
     */
    public static void main(String[] args)
    {
        // forward all jul logging to logback
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
        Logger.getLogger("global").setLevel(Level.FINEST);
        Application.launch(args);
    }

}
