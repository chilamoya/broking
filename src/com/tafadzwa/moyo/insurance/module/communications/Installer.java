/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tafadzwa.moyo.insurance.module.communications;

import com.innate.cresterp.insurance.risk.entities.SystemRole;
import com.innate.cresterp.insurance.risk.persistence.SystemRoleJpaController;
import com.tafadzwa.moyo.insurance.module.roles.RolesEnum;
import javax.persistence.Query;
import org.openide.modules.ModuleInstall;

public class Installer extends ModuleInstall {
   SystemRoleJpaController roleManager = new SystemRoleJpaController(null);
    private void setupLookAndFeel() {
    
        //Insert into the database if the Roles are not there
     
     
          for (RolesEnum en: RolesEnum.values()){
            if (!checkIfRoleExists(en.toString())){
                   SystemRole role  = new SystemRole();
                   role.setRoleName(en.toString().trim());
                   roleManager.create(role);
                   System.out.println("Created insurance role: "+role.getRoleName());
            }
        }
        
        
    }
    
    private boolean checkIfRoleExists(String value){
      
        Query q = roleManager.getEntityManager().createQuery("Select r From SystemRole r where r.roleName =?1 ");
        q.setParameter(1, value.trim());
        boolean isEmpty = true ;
        if (q.getResultList().isEmpty())
            isEmpty = false ;
                
                return isEmpty;
    }

    @Override
    public void restored() {
        // TODO
        
            setupLookAndFeel(); 

    }

}
