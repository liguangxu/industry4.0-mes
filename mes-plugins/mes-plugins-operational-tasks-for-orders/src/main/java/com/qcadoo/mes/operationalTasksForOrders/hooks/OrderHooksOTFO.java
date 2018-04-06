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
package com.qcadoo.mes.operationalTasksForOrders.hooks;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qcadoo.mes.operationalTasks.constants.OperationalTaskFields;
import com.qcadoo.mes.operationalTasksForOrders.OperationalTasksForOrdersService;
import com.qcadoo.mes.orders.constants.OrderFields;
import com.qcadoo.model.api.DataDefinition;
import com.qcadoo.model.api.Entity;

@Service
public class OrderHooksOTFO {

    @Autowired
    private OperationalTasksForOrdersService operationalTasksForOrdersService;

    public void changedProductionLineInOperationalTasksWhenChanged(final DataDefinition orderDD, final Entity order) {
        Long orderId = order.getId();

        if (orderId == null) {
            return;
        }

        Entity orderFromDB = orderDD.get(orderId);

        Entity productionLine = order.getBelongsToField(OrderFields.PRODUCTION_LINE);
        Entity orderProductionLine = orderFromDB.getBelongsToField(OrderFields.PRODUCTION_LINE);

        if ((productionLine == null) || (orderProductionLine == null)) {
            return;
        } else {
            if (!orderProductionLine.getId().equals(productionLine.getId())) {
                changedProductionLineInOperationalTasks(orderFromDB, productionLine);
            }
        }
    }

    private void changedProductionLineInOperationalTasks(final Entity order, final Entity productionLine) {
        List<Entity> operationalTasks = operationalTasksForOrdersService.getOperationalTasksForOrder(order);

        for (Entity operationalTask : operationalTasks) {
            operationalTask.setField(OperationalTaskFields.PRODUCTION_LINE, productionLine);

            operationalTask.getDataDefinition().save(operationalTask);
        }
    }

}
