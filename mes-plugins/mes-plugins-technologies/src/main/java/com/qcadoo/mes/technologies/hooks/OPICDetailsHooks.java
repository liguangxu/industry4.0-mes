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
package com.qcadoo.mes.technologies.hooks;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qcadoo.mes.basic.util.UnitService;
import com.qcadoo.mes.technologies.constants.OperationProductInComponentFields;
import com.qcadoo.view.api.ViewDefinitionState;

@Service
public class OPICDetailsHooks {

    @Autowired
    private UnitService unitService;

    public void fillUnitBeforeRender(final ViewDefinitionState view) {

        unitService.fillProductUnitBeforeRenderIfEmpty(view, OperationProductInComponentFields.UNIT);
        unitService.fillProductUnitBeforeRenderIfEmpty(view, OperationProductInComponentFields.GIVEN_UNIT);
    }
}
