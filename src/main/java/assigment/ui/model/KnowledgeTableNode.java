package assigment.ui.model;

import javafx.scene.control.ContextMenu;

/**
 * @author sergey
 *
 */
public interface KnowledgeTableNode
{

    /**
     * @return creates a menu of action that can be performed on the node
     */
    ContextMenu createMenu();

}
