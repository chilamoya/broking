/*
 * 
 */
package com.tafadzwa.moyo.insurance.module.risk;



import com.innate.cresterp.medical.hospital.entities.PatientRecord;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Action;
import org.openide.actions.DeleteAction;
import org.openide.actions.PropertiesAction;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.actions.SystemAction;
import org.openide.util.lookup.Lookups;

public class CustomerChildFactory extends ChildFactory<PatientRecord> {

    private List<PatientRecord> resultList;
   

    public CustomerChildFactory(List<PatientRecord> resultList) {
        this.resultList = resultList;
    }

    @Override
    protected boolean createKeys(List<PatientRecord> list) {
        for (PatientRecord customer : resultList) {
            list.add(customer);
        }
        return true;
    }

    @Override
    protected Node createNodeForKey(PatientRecord c) {
        Node node = new AbstractNode(Children.LEAF, Lookups.singleton(c)) {

            
            @Override
            public Action[] getActions(boolean context) {
                Action[] result = new Action[]{
                    SystemAction.get(DeleteAction.class),
                    SystemAction.get(PropertiesAction.class)
                };
                return result;
            }

            @Override
            public boolean canDestroy() {
                PatientRecord customer = this.getLookup().lookup(PatientRecord.class);
                return customer != null;
            }

            @Override
            public void destroy() throws IOException {
                if (deleteCustomer(this.getLookup().lookup(PatientRecord.class).getId().intValue())) {
                    super.destroy();
                    CustomerTopComponent.refreshNode();
                }
            }

        };
        node.setDisplayName(c.getSurname()+ " "+ c.getName()+ " - "+c.getHospitalNumber());
        node.setShortDescription(c.getSurname());
       
        node.setPreferred(true);
        
        return node;
    }

    private static boolean deleteCustomer(int customerId) {
          try {
//    PatientRecordJpaController controller = new PatientRecordJpaController();
//                  controller.destroy(new Long(""+customerId));
              
        } catch(Exception e) {
            Logger.getLogger(CustomerChildFactory.class.getName()).log(
                    Level.WARNING, "Cannot delete a customer with id {0}, cause: {1}", new Object[]{customerId, e});
         }
        return true;
    }
}
