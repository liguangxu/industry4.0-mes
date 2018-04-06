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
package com.qcadoo.mes.masterOrders.hooks;

import java.util.List;

import org.springframework.stereotype.Service;

import com.qcadoo.model.api.Entity;
import com.qcadoo.view.api.ViewDefinitionState;
import com.qcadoo.view.api.components.GridComponent;
import com.qcadoo.view.api.components.WindowComponent;
import com.qcadoo.view.api.ribbon.RibbonActionItem;
import com.qcadoo.view.api.ribbon.RibbonGroup;

@Service
public class MasterOrderPositionsListHooks {

    private static final String L_GRID = "grid";

    private static final String L_WINDOW = "window";

    private static final String L_ORDERS = "orders";

    private static final String L_CREATE_ORDER = "createOrder";

    public void disableButton(final ViewDefinitionState view) {
        GridComponent masterOrderPositionComponent = (GridComponent) view.getComponentByReference(L_GRID);

        WindowComponent window = (WindowComponent) view.getComponentByReference(L_WINDOW);
        RibbonGroup ordersRibbonGroup = window.getRibbon().getGroupByName(L_ORDERS);
        RibbonActionItem createOrderRibbonActionItem = ordersRibbonGroup.getItemByName(L_CREATE_ORDER);

        List<Entity> selectedEntities = masterOrderPositionComponent.getSelectedEntities();

        boolean isEnabled = (selectedEntities.size() == 1);
        createOrderRibbonActionItem.setEnabled(isEnabled);

        createOrderRibbonActionItem.requestUpdate(true);
        window.requestRibbonRender();
        createOrderRibbonActionItem.setMessage("masterOrders.masterOrder.masterOrdersPosition.lessEntitiesSelectedThanAllowed");
    }

}
