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
package com.qcadoo.mes.workPlans.pdf.document.operation.grouping.container;

import java.util.Map;

import com.qcadoo.mes.columnExtension.constants.ColumnAlignment;
import com.qcadoo.mes.technologies.dto.OperationProductComponentWithQuantityContainer;
import com.qcadoo.mes.workPlans.pdf.document.operation.product.column.OperationProductColumn;
import com.qcadoo.mes.workPlans.pdf.document.order.column.OrderColumn;
import com.qcadoo.model.api.Entity;

public class NoDistinctionGroupingContainer extends AbstractGroupingContainer {

    public NoDistinctionGroupingContainer(Map<OrderColumn, ColumnAlignment> orderColumnToAlignment,
            Map<Long, Map<OperationProductColumn, ColumnAlignment>> operationComponentIdProductInColumnToAlignment,
            Map<Long, Map<OperationProductColumn, ColumnAlignment>> operationComponentIdProductOutColumnToAlignment,
            String titleAppend) {
        super(orderColumnToAlignment, operationComponentIdProductInColumnToAlignment,
                operationComponentIdProductOutColumnToAlignment, titleAppend);
    }

    @Override
    public void add(Entity order, Entity operationComponent, OperationProductComponentWithQuantityContainer productQuantities) {
        store(titleAppend, order, operationComponent);
    }

}
