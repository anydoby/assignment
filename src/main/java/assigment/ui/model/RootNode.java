package assigment.ui.model;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

/**
 * @author sergey
 *
 */
public class RootNode implements KnowledgeTableNode
{

    private ContextMenu menu;

    /**
     * Empty ctor for the root node
     */
    public RootNode()
    {
        menu = new ContextMenu();
        menu.getItems().add(new MenuItem("Add a stub"));
    }

    @Override
    public String toString()
    {
        return "KnowledgeTable";
    }

    @Override
    public ContextMenu createMenu()
    {
        return menu;
    }

}
