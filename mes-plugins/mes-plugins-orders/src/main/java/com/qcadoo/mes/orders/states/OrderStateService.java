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
package com.qcadoo.mes.orders.states;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qcadoo.commons.dateTime.DateRange;
import com.qcadoo.mes.orders.constants.OrderFields;
import com.qcadoo.mes.orders.util.OrderDatesService;
import com.qcadoo.mes.states.StateChangeContext;
import com.qcadoo.model.api.Entity;

@Service
public class OrderStateService {

    @Autowired
    private OrderDatesService orderDatesService;

    public boolean isSynchronized(final Entity order) {
        return order.getBooleanField(OrderFields.EXTERNAL_SYNCHRONIZED);
    }

    public void checkOrderDates(final StateChangeContext stateChangeContext) {
        final Entity order = stateChangeContext.getOwner();
        DateRange orderDateRange = orderDatesService.getCalculatedDates(order);
        Date dateFrom = orderDateRange.getFrom();
        Date dateTo = orderDateRange.getTo();

        if (dateFrom == null || dateTo == null || dateTo.after(dateFrom)) {
            return;
        }

        stateChangeContext.addValidationError("orders.validate.global.error.datesOrder.overdue");
    }

}
