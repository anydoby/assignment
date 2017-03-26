package assigment.ui;

import static java.util.prefs.Preferences.userNodeForPackage;

import java.io.IOException;
import java.util.prefs.Preferences;

import javax.xml.bind.JAXBException;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;

import assigment.service.XmlStore;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Main coordinator of scenes and a spring configuration for the app.
 * 
 * @author sergey
 *
 */
@Configuration
@Lazy
@ComponentScan("assigment.ui")
@EnableSpringConfigured
public class ScenesCoordinator implements InitializingBean, DisposableBean
{

    private int width;
    private int height;
    private int lastX;
    private int lastY;

    private Stage primaryStage;
    private String lastPath;

    @Autowired
    ApplicationContext ctx;

    /**
     * Sets primary stage
     * 
     * @param primaryStage
     * @return this
     */
    public ScenesCoordinator init(Stage primaryStage)
    {
        primaryStage.setTitle("KnowledgeBase");
        this.primaryStage = primaryStage;
        if (lastX != -1)
        {
            primaryStage.setX(lastX);
            primaryStage.setY(lastY);
        }
        return this;
    }

    /**
     * Shows main screen.
     * 
     * @throws IOException
     */
    public void showMain() throws IOException
    {
        Scene scene = new Scene(resolveView("main"), width, height);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private Parent resolveView(String view) throws IOException
    {
        // here javafx meets spring
        FXMLLoader loader = new FXMLLoader(getClass().getResource(view + ".fxml"));
        loader.setControllerFactory(clazz -> ctx.getBean(clazz));
        return loader.load();
    }

    @Override
    public void destroy() throws Exception
    {
        Preferences preferences = userNodeForPackage(getClass());
        preferences.putInt("last.width", (int) primaryStage.getWidth());
        preferences.putInt("last.height", (int) primaryStage.getHeight());
        preferences.putInt("last.x", (int) primaryStage.getX());
        preferences.putInt("last.y", (int) primaryStage.getY());
        preferences.put("last.file", lastPath);
    }

    @Override
    public void afterPropertiesSet() throws Exception
    {
        Preferences preferences = userNodeForPackage(getClass());
        width = preferences.getInt("last.width", 800);
        height = preferences.getInt("last.height", 600);
        lastX = preferences.getInt("last.x", -1);
        lastY = preferences.getInt("last.y", -1);
        lastPath = preferences.get("last.file", "./knowledgebase.xml");
    }

    public String getLastPath()
    {
        return lastPath;
    }

    /**
     * @return the storage component
     * @throws JAXBException
     */
    @Bean
    public XmlStore store() throws JAXBException
    {
        return new XmlStore();
    }

    public void setLastPath(String absolutePath)
    {
        lastPath = absolutePath;
    }

}
