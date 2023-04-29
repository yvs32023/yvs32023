/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */

package com.excellus.sqa.rxcc.dto;

/**
 * 
 * 
 * @author Garrett Cosmiano(gcosmian)
 * @since 08/01/2022
 */
public enum MainMenu 
{
    INTERVENTION_QUEUE         (1, "Intervention Queue"),
    PROVIDER_DIRECTORY        (2, "Provider Directory"),
    PHARMACY_DIRECTORY         (3, "Pharmacy Directory"),
    INTERVENTION_RULE_LIBRARY  (4, "Intervention Rule Library"),
    SIMULATION_QUEUE           (5, "Simulation Queue"),     
    FORMULARY_MAINTENANCE      (6, "Formulary Maintenance"),    
    FAX_DISCREPANCY_QUEUE      (7, "Fax Discrepancy Queue"); 
 
    private int menuIndex;
    private String item;
    
    private MainMenu(int menuIndex, String item)
    {
        this.menuIndex = menuIndex; 
        this.item = item;
    }
    
    public int getMenuIndex()
    {
        return this.menuIndex;
    }
    
    public String getToolTip()
    {
        return item;
    }
    
    @Override
    public String toString() {
        return item;
    }
}
