package assigment.ui;

import static assigment.model.Node.stub;
import static assigment.service.MrProper.Mode.down;
import static assigment.service.MrProper.Mode.up;
import static java.lang.Double.POSITIVE_INFINITY;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import assigment.model.KnowledgeTable;
import assigment.model.Node;
import assigment.model.NodeType;
import assigment.service.MrProper;
import assigment.service.MrProper.Mode;
import assigment.service.XmlStore;
import assignment.util.AnyThrow;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.adapter.JavaBeanStringProperty;
import javafx.beans.property.adapter.JavaBeanStringPropertyBuilder;
import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

/**
 * Controller for main.fxml.
 * 
 * @author sergey
 *
 */
@Component
public class MainController
{

    Logger LOG = LoggerFactory.getLogger(getClass());

    @Autowired
    ScenesCoordinator navigator;

    @Autowired
    XmlStore store;

    @FXML
    TextArea content;

    @FXML
    StackPane pane;

    ObjectProperty<MrProper> tableModel = new SimpleObjectProperty<>();

    StringProperty previousContent;

    /**
     * @throws Exception
     * 
     */
    @FXML
    public void initialize() throws Exception
    {
        tableModel.addListener((obs, oldValue, newValue) -> {
            itemsModified(newValue, up);
        });
    }

    @Catch
    private void itemsModified(MrProper manager, Mode trimOrGrow)
    {
        try
        {
            manager.normalize(trimOrGrow);
        }
        catch (Exception e)
        {
            LOG.error("Unable to normalize table: " + manager.getTable(), e);
        }
        Platform.runLater(() -> renderTable());
    }

    @Catch
    private void renderTable()
    {
        pane.getChildren().clear();
        rebindContent(null);

        MrProper mrProper = tableModel.get();
        LOG.info("Re-rendering new table state");
        LOG.info(mrProper.getTable().toString());
        GridPane gridPane = new GridPane();
        gridPane.getStyleClass().add("outline");

        int depthMax = mrProper.getMaxDepth() + 1;
        List<List<Node>> matrix = new ArrayList<>();
        int maxLayerLength = 0;
        for (int layer = 0; layer < depthMax; layer++ )
        {
            List<Node> nodes = mrProper.getLayer(layer);
            matrix.add(nodes);
            maxLayerLength = Math.max(maxLayerLength, nodes.size());
        }

        for (int layer = 0; layer < matrix.size(); layer++ )
        {
            List<Node> nodes = matrix.get(layer);
            for (int column = 0; column < nodes.size(); column++ )
            {
                Label label = new Label();
                Node node = nodes.get(column);
                JavaBeanStringProperty contentText = contentProperty(node);
                label.textProperty().bind(contentText);
                label.setUserData(node);
                label.getStyleClass().add(node.getType().name());
                label.getStyleClass().add("outline");
                label.setOnMouseClicked(e -> rebindContent(contentText));
                label.setContextMenu(menuFor(node));

                GridPane.setFillWidth(label, true);
                GridPane.setFillHeight(label, true);

                // use the rest of the columns for the last component
                int colspan = last(column, nodes) ? maxLayerLength - column : 1;
                label.setMinSize(100 * colspan, 30);
                label.setMaxSize(POSITIVE_INFINITY, POSITIVE_INFINITY);
                gridPane.add(label, column, layer, colspan, 1);
            }
        }
        pane.getChildren().add(gridPane);
    }

    private ContextMenu menuFor(Node node)
    {
        ContextMenu menu = null;
        switch (node.getType())
        {
            case action:
                menu = new ContextMenu();
                if (node.getNode().isEmpty())
                {
                    menu.getItems().add(addAction(node));
                }
                if ((node.getParent().getType() == NodeType.condition
                        && node.getParent().getNode().stream().filter(n -> n.getType() == NodeType.action).count() > 1)
                        || node.getParent().getType() == NodeType.action)
                {
                    menu.getItems().add(removeAction(node));
                }
                if (menu.getItems().isEmpty())
                {
                    menu = null;
                }
                break;
            case condition:
                menu = new ContextMenu(addAction(node), addCondition(node));
                if (node.getDepth() != 0)
                {
                    menu.getItems().add(removeAction(node));
                }
                break;
            case stub:
                // nothing here, stubs are managed automatically
                break;
        }
        return menu;
    }

    private MenuItem removeAction(Node node)
    {
        MenuItem item = new MenuItem("Delete");
        item.setOnAction(e -> {
            node.remove();
            itemsModified(tableModel.get(), down);
        });
        return item;
    }

    private MenuItem addCondition(Node node)
    {
        MenuItem item = new MenuItem("Add Condition");
        item.setOnAction(e -> {
            node.addChild(Node.condition(null));
            itemsModified(tableModel.get(), up);
        });
        return item;
    }

    private MenuItem addAction(Node node)
    {
        MenuItem item = new MenuItem("Add Action");
        item.setOnAction(e -> {
            node.addChild(Node.action(null));
            itemsModified(tableModel.get(), up);
        });
        return item;
    }

    private JavaBeanStringProperty contentProperty(Node node)
    {
        try
        {
            return new JavaBeanStringPropertyBuilder().bean(node).name("content").build();
        }
        catch (NoSuchMethodException e)
        {
            throw AnyThrow.unchecked(e);
        }
    }

    private void rebindContent(StringProperty contentText)
    {
        if (previousContent != null)
        {
            content.textProperty().unbindBidirectional(previousContent);
        }
        if (contentText != null)
        {
            content.textProperty().bindBidirectional(contentText);
        }
        previousContent = contentText;
    }

    private static boolean last(int column, List<Node> nodes)
    {
        return nodes.size() - 1 == column;
    }

    public void createTable()
    {
        KnowledgeTable table = new KnowledgeTable();
        table.getNode().add(stub(null));
        tableModel.set(new MrProper(table));
    }

    @Catch
    public void open() throws FileNotFoundException, IOException, JAXBException
    {
        FileChooser chooser = createFileChooser();
        chooser.getExtensionFilters().add(new ExtensionFilter("XML knowledge base", "*.xml", "*.XML"));
        File selectedFile = chooser.showOpenDialog(null);
        if (selectedFile != null)
        {
            navigator.setLastPath(selectedFile.getAbsolutePath());
            try (FileInputStream fis = new FileInputStream(selectedFile))
            {
                KnowledgeTable table = store.load(new FileInputStream(selectedFile));
                tableModel.set(new MrProper(table));
            }
        }
    }

    private FileChooser createFileChooser()
    {
        FileChooser chooser = new FileChooser();
        File lastFile = new File(navigator.getLastPath());
        if (lastFile.getParentFile().exists())
        {
            chooser.setInitialDirectory(lastFile.getParentFile());
        }
        return chooser;
    }

    @Catch
    public void save() throws FileNotFoundException, IOException, JAXBException
    {
        FileChooser chooser = createFileChooser();
        File output = chooser.showSaveDialog(null);
        if (output != null)
        {
            if (!output.getName().endsWith(".xml"))
            {
                output = new File(output.getAbsolutePath() + ".xml");
            }
            navigator.setLastPath(output.getAbsolutePath());
            try (FileOutputStream fos = new FileOutputStream(output))
            {
                store.store(tableModel.get().getTable(), fos);
            }
        }
    }

    /**
     * Closes app
     */
    public void close()
    {
        Platform.exit();
    }

}
