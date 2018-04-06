/**
 * ***************************************************************************
 * Copyright (c) 2018 RiceFish Limited
 * Project: SmartMES
 * Version: 1.6
 *
 * This file is part of SmartMES.
 *
 * SmartMES is Authorized software; you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation; either version 3 of the License,
 * or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * ***************************************************************************
 */
package com.qcadoo.mes.deliveries;

import com.qcadoo.model.api.DataDefinition;
import com.qcadoo.model.api.Entity;

public interface CompanyProductService {

    /**
     * Checks if product is not used
     * 
     * @param companyProduct
     *            company product
     * 
     * @param companyName
     *            belongs to product name - product
     * 
     * @param belongsToCompanyName
     *            belongs to company name - company
     * 
     * @param hasManyName
     *            has many name - companyProducts
     * 
     * @return boolean
     * 
     */
    boolean checkIfProductIsNotUsed(final Entity companyProduct, final String belongsToProductName,
            final String belongsToCompanyName, final String hasManyName);

    boolean checkIfDefaultAlreadyExists(final Entity companyProduct);

    boolean checkIfDefaultExistsForFamily(final Entity companyProduct);

    boolean checkIfDefaultExistsForParticularProduct(final Entity product);

    boolean checkIfDefaultExistsForProductFamily(final Entity product);

    /**
     * Checks if any product from given family has default supplier, if found - returns that product's number
     * 
     * @param companyProduct
     * @return
     */
    String checkIfDefaultExistsForProductsInFamily(final Entity companyProduct);
}
