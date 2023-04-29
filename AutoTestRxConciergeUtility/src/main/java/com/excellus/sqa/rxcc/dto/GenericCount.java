/* @copyright 2022 Excellus BCBS
* All rights reserved.
* 
 */
package com.excellus.sqa.rxcc.dto;

import java.util.List;

import org.junit.jupiter.api.DynamicNode;

/**
* Generic JSON object that holds an id and number
* 
 * @author Garrett Cosmiano(gcosmian)
* @since 05/19/2022
*/
public class GenericCount extends AbstractJsonDTO<GenericCount>
{

    private String id;
    
	private String intId;

	private String typeId;

	private int num;
    
    /*
     * Validations
     */
    
	@Override
	public List<DynamicNode> compare(GenericCount dto) {
		// Do nothing - no validation is required
		return null;
	}


    /*
     * Setter and Getter 
     */
 
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    
    public String getIntId() {
		return intId;
	}


	public void setIntId(String intId) {
		this.intId = intId;
	}
	
	public String getTypeId() {
		return typeId;
	}

	public void setTypeId(String typeId) {
		this.typeId = typeId;
	}
 
    public int getNum() {
        return num;
    }

    public void setNum(float num) {
        this.num = (int)num;
    }

}
