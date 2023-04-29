/**
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 */
package com.excellus.sqa.rxcc.cosmos;

/**
 * Exceptions for Cosmos result count
 *
 * @author Garrett Cosmiano (gcosmian)
 * @since 02/05/2022
 */
public class UnexpectedCosmosResultCount extends RuntimeException{

	private static final long serialVersionUID = 1L;

	public UnexpectedCosmosResultCount(String errorMessage) {
		super(errorMessage);
	}
}
