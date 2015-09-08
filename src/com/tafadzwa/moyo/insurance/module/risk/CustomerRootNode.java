/*
 
 */
package com.tafadzwa.moyo.insurance.module.risk;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;

public class CustomerRootNode extends AbstractNode {

    public CustomerRootNode(Children kids) {
        super(kids);
        setDisplayName("Clients");
        
    }

    @Override
    public Action[] getActions(boolean context) {
        Action[] result = new Action[]{
            new RefreshAction()
        };
        return result;
    }

    private final class RefreshAction extends AbstractAction {

        public RefreshAction() {
            putValue(Action.NAME, "Refresh");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            CustomerTopComponent.refreshNode();
        }
    }

}
