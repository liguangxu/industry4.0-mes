/*
 * **************************************************************************
 * Copyright (c) 2018 RiceFish Limited
 * Project: SmartMES Framework
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
 * **************************************************************************
 */
package com.qcadoo.mes.basic.staff.importing;

import com.qcadoo.mes.basic.constants.BasicConstants;
import com.qcadoo.model.api.DataDefinition;
import com.qcadoo.model.api.DataDefinitionService;
import com.qcadoo.model.api.Entity;
import com.qcadoo.model.api.validators.ErrorMessage;
import org.apache.poi.ss.usermodel.Cell;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
class RowProcessorFactoryForStaff {

    private final DataDefinitionService dataDefinitionService;

    private final CellBinderRegistryForStaff cellBinderRegistryForStaff;

    @Autowired
    RowProcessorFactoryForStaff(DataDefinitionService dataDefinitionService, CellBinderRegistryForStaff cellBinderRegistryForStaff) {
        this.dataDefinitionService = dataDefinitionService;
        this.cellBinderRegistryForStaff = cellBinderRegistryForStaff;
    }


    private DataDefinition getStaffDataDefinition() {
        return dataDefinitionService.get(BasicConstants.PLUGIN_IDENTIFIER, BasicConstants.MODEL_STAFF);
    }

    private Entity createEntityWithDefaultValues() {
        final Entity entity = getStaffDataDefinition().create();
        return entity;
    }

    RowProcessor create(final ImportStatus importStatus, int rowIndex) {
        return new RowProcessorImpl(importStatus, createEntityWithDefaultValues(), rowIndex);
    }

    private class RowProcessorImpl implements RowProcessor {

        private final ImportStatus importStatus;
        private final Entity entity;
        private final int currentRow;
        private boolean finished;
        private int index;
        private boolean empty = true;
        private List<ImportError> rowErrors = new ArrayList<>();

        RowProcessorImpl(ImportStatus importStatus, Entity entity, int rowIndx) {
            this.importStatus = importStatus;
            this.entity = entity;
            this.currentRow = rowIndx;
        }

        @Override
        public boolean isEmpty() {
            return empty;
        }

        @Override
        public void append(final Cell cell) {
            assureNotProcessedYet();
            if (null != cell) {
                empty = false;
            }
            final CellBinder binder = cellBinderRegistryForStaff.getCellBinder(index++);
            binder.bind(
                    cell,
                    entity,
                    errorCode -> rowErrors.add(new ImportError(currentRow, binder.getFieldName(), errorCode))
            );
        }

        private void assureNotProcessedYet() {
            if (finished) {
                throw new IllegalStateException("Row already processed");
            }
        }

        @Override
        public void process() {
            assureNotProcessedYet();
            finished = true;
            importStatus.incrementRowsProcessedCounter();
            final Entity savedEntity = getStaffDataDefinition().save(entity);
            populateImportStatusWithBindingErrors();
            populateImportStatusWithEntityErrors(savedEntity);
        }

        private void populateImportStatusWithBindingErrors() {
            rowErrors.forEach(importStatus::addError);
        }

        private void populateImportStatusWithEntityErrors(Entity entity) {
            if (!entity.isValid()) {
                for (Map.Entry<String, ErrorMessage> entry : entity.getErrors().entrySet()) {
                    importStatus.addError(
                            new ImportError(
                                    currentRow, entry.getKey(), entry.getValue().getMessage(), entry.getValue().getVars())
                    );
                }
            }
        }
    }

}
