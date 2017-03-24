package assigment.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import assigment.service.XmlStore;
import assigment.ui.components.KnowledgeCell;
import assigment.ui.model.KnowledgeTableNode;
import assigment.ui.model.RootNode;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

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
    XmlStore store;

    @FXML
    TreeView<KnowledgeTableNode> knowledgeTree;

    /**
     * @throws Exception
     * 
     */
    @FXML
    public void initialize() throws Exception
    {
        knowledgeTree.setRoot(new TreeItem<KnowledgeTableNode>(new RootNode()));
        knowledgeTree.setCellFactory(view -> new KnowledgeCell(view));
    }

    public void showNodeContext()
    {

    }

    /**
     * Closes app
     */
    public void close()
    {
        Platform.exit();
    }

}
