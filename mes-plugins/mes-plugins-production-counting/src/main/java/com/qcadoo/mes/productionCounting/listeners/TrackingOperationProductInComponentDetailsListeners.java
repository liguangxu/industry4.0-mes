
package com.qcadoo.mes.productionCounting.listeners;

import static com.qcadoo.model.api.BigDecimalUtils.convertNullToZero;

import java.math.BigDecimal;
import java.math.MathContext;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qcadoo.mes.basic.constants.ProductFields;
import com.qcadoo.mes.productionCounting.constants.TrackingOperationProductInComponentFields;
import com.qcadoo.model.api.Entity;
import com.qcadoo.model.api.NumberService;
import com.qcadoo.view.api.ComponentState;
import com.qcadoo.view.api.ViewDefinitionState;
import com.qcadoo.view.api.components.CheckBoxComponent;
import com.qcadoo.view.api.components.FieldComponent;
import com.qcadoo.view.api.components.FormComponent;

@Service
public class TrackingOperationProductInComponentDetailsListeners {

    private static final String L_FORM = "form";

    private static final String L_PRODUCT = "product";

    private static final String L_WASTE_USED_ONLY = "wasteUsedOnly";

    private static final String L_WASTE_USED_QUANTITY = "wasteUsedQuantity";

    private static final String L_WASTE_UNIT = "wasteUnit";

    private static final String L_USED_QUANTITY = "usedQuantity";

    private static final String L_USED_QUANTITY_UNIT = "usedQuantityUNIT";

    @Autowired
    private NumberService numberService;

    @Autowired
    private TrackingOperationProductComponentDetailsListeners trackingOperationProductComponentDetailsListeners;

    private Entity getFormEntity(final ViewDefinitionState view) {
        return ((FormComponent) view.getComponentByReference(L_FORM)).getPersistedEntityWithIncludedFormValues();
    }

    public void onWasteUsedChange(final ViewDefinitionState view, final ComponentState wasteUsed, final String[] args) {
        CheckBoxComponent wasteUsedOnly = (CheckBoxComponent) view.getComponentByReference(L_WASTE_USED_ONLY);
        FieldComponent wasteUsedQuantity = (FieldComponent) view.getComponentByReference(L_WASTE_USED_QUANTITY);
        FieldComponent wasteUnit = (FieldComponent) view.getComponentByReference(L_WASTE_UNIT);

        if (((CheckBoxComponent) wasteUsed).isChecked()) {
            String productUnit = getFormEntity(view).getBelongsToField(L_PRODUCT).getStringField(ProductFields.UNIT);
            if (StringUtils.isEmpty((CharSequence) wasteUnit.getFieldValue())) {
                wasteUnit.setFieldValue(productUnit);
            }
        } else {
            wasteUsedOnly.setFieldValue(null);
            wasteUsedQuantity.setFieldValue(null);
            wasteUnit.setFieldValue(null);
        }
    }

    public void onWasteUsedOnlyChange(final ViewDefinitionState view, final ComponentState wasteUsedOnly, final String[] args) {
        if (((CheckBoxComponent) wasteUsedOnly).isChecked()) {
            Entity entity = getFormEntity(view);
            Entity savedEntity = entity.getDataDefinition().get(entity.getId());

            BigDecimal savedUsedQuantity = savedEntity.getDecimalField(TrackingOperationProductInComponentFields.USED_QUANTITY);
            BigDecimal savedWasteUsed = convertNullToZero(
                    savedEntity.getDecimalField(TrackingOperationProductInComponentFields.WASTE_USED_QUANTITY));

            if (savedUsedQuantity != null && savedUsedQuantity.compareTo(BigDecimal.ZERO) > 0) {
                FieldComponent usedQuantity = (FieldComponent) view.getComponentByReference(L_USED_QUANTITY);
                FieldComponent wasteUsedQuantity = (FieldComponent) view.getComponentByReference(L_WASTE_USED_QUANTITY);
                BigDecimal newValue = savedUsedQuantity.add(savedWasteUsed, numberService.getMathContext());
                wasteUsedQuantity.setFieldValue(numberService.format(newValue));
                usedQuantity.setFieldValue(numberService.format(BigDecimal.ZERO));
                trackingOperationProductComponentDetailsListeners.calculateQuantityToGiven(view, usedQuantity,
                        ArrayUtils.EMPTY_STRING_ARRAY);
            }
        }
    }

    public void recalculateUsedQuantity(final ViewDefinitionState view, final ComponentState wasteUsedQuantity,
            final String[] args) {
        Entity entity = getFormEntity(view);

        boolean wasteUsedOnly = entity.getBooleanField(TrackingOperationProductInComponentFields.WASTE_USED_ONLY);
        boolean sameUnits = view.getComponentByReference(L_USED_QUANTITY_UNIT).getFieldValue()
                .equals(view.getComponentByReference(L_WASTE_UNIT).getFieldValue());

        if (!wasteUsedOnly && sameUnits) {
            BigDecimal wasteUsed = entity.getDecimalField(TrackingOperationProductInComponentFields.WASTE_USED_QUANTITY);
            if (wasteUsed != null && wasteUsed.compareTo(BigDecimal.ZERO) > 0) {
                Entity savedEntity = entity.getDataDefinition().get(entity.getId());
                BigDecimal savedUsedQuantity = savedEntity
                        .getDecimalField(TrackingOperationProductInComponentFields.USED_QUANTITY);
                BigDecimal savedWasteUsed = convertNullToZero(
                        savedEntity.getDecimalField(TrackingOperationProductInComponentFields.WASTE_USED_QUANTITY));

                if (savedUsedQuantity != null) {
                    MathContext mc = numberService.getMathContext();
                    BigDecimal newValue = savedUsedQuantity.add(savedWasteUsed, mc).subtract(wasteUsed, mc);

                    if (newValue.compareTo(BigDecimal.ZERO) < 0) {
                        newValue = BigDecimal.ZERO;
                    }

                    FieldComponent usedQuantity = (FieldComponent) view.getComponentByReference(L_USED_QUANTITY);
                    usedQuantity.setFieldValue(numberService.format(newValue));
                    trackingOperationProductComponentDetailsListeners.calculateQuantityToGiven(view, usedQuantity,
                            ArrayUtils.EMPTY_STRING_ARRAY);
                }
            }
        }
    }

}
