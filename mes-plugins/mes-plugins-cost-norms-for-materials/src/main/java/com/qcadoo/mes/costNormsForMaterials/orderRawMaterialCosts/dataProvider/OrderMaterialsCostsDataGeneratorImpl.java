package com.qcadoo.mes.costNormsForMaterials.orderRawMaterialCosts.dataProvider;

import static com.qcadoo.model.api.search.SearchRestrictions.in;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Optional;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.qcadoo.commons.functional.FluentOptional;
import com.qcadoo.commons.functional.Optionals;
import com.qcadoo.mes.basicProductionCounting.BasicProductionCountingService;
import com.qcadoo.mes.basicProductionCounting.constants.ProductionCountingQuantityFields;
import com.qcadoo.mes.costNormsForMaterials.constants.TechnologyInstOperProductInCompFields;
import com.qcadoo.mes.costNormsForMaterials.orderRawMaterialCosts.OrderMaterialsCostDataGenerator;
import com.qcadoo.mes.costNormsForMaterials.orderRawMaterialCosts.domain.ProductWithCosts;
import com.qcadoo.mes.orders.constants.OrderFields;
import com.qcadoo.mes.orders.states.constants.OrderState;
import com.qcadoo.mes.technologies.TechnologyService;
import com.qcadoo.mes.technologies.constants.OperationProductInComponentFields;
import com.qcadoo.mes.technologies.constants.TechnologiesConstants;
import com.qcadoo.mes.technologies.tree.dataProvider.TechnologyRawInputProductComponentsCriteria;
import com.qcadoo.mes.technologies.tree.dataProvider.TechnologyRawInputProductComponentsDataProvider;
import com.qcadoo.model.api.DataDefinitionService;
import com.qcadoo.model.api.Entity;
import com.qcadoo.model.api.search.JoinType;
import com.qcadoo.model.api.search.SearchProjection;
import com.qcadoo.model.api.search.SearchRestrictions;
import com.qcadoo.model.api.utils.EntityUtils;

@Service
final class OrderMaterialsCostsDataGeneratorImpl implements OrderMaterialsCostDataGenerator {

    private static final SearchProjection PRODUCT_WITH_COSTS_PROJECTION = ProductWithCostsBuilder
            .buildProjectionForProduct(TechnologyRawInputProductComponentsCriteria.PRODUCT_ALIAS);

    @Autowired
    private OrderMaterialCostsEntityBuilder orderMaterialCostsEntityBuilder;

    @Autowired
    private TechnologyRawInputProductComponentsDataProvider technologyRawInputProductComponentsDataProvider;

    @Autowired
    private OrderMaterialCostsDataProvider orderMaterialCostsDataProvider;

    @Autowired
    private BasicProductionCountingService basicProductionCountingService;

    @Autowired
    private TechnologyService technologyService;

    @Autowired
    private DataDefinitionService dataDefinitionService;

    @Override
    public List<Entity> generateUpdatedMaterialsListFor(final Entity order) {
        for (Long technologyId : extractTechnologyIdFrom(order).asSet()) {
            List<ProductWithCosts> allTechnologyRawProductsWithCosts = findRawInputProductsFor(technologyId);
            List<Entity> existingOrderMaterialCosts;
            if (OrderState.PENDING.getStringValue().equals(order.getStringField(OrderFields.STATE))) {
                final Set<Long> technologyRawProductIds = allTechnologyRawProductsWithCosts.stream()
                        .map(ProductWithCosts.EXTRACT_ID::apply).collect(Collectors.toSet());
                existingOrderMaterialCosts = findExistingOrderMaterialCosts(order, technologyRawProductIds);
            } else {
                existingOrderMaterialCosts = findExistingOrderMaterialCosts(order, null);
            }
            return createMissingOrderMaterialCostsEntities(order, allTechnologyRawProductsWithCosts, existingOrderMaterialCosts);
        }
        return ImmutableList.of();
    }

    private List<Entity> createMissingOrderMaterialCostsEntities(final Entity order,
                                                                 final List<ProductWithCosts> allTechnologyRawProductsWithCosts, final List<Entity> existingOrderMaterialCosts) {
        final Set<Long> existingMaterialCostIds = existingOrderMaterialCosts.stream()
                .map(EntityUtils.getBelongsToFieldExtractor(TechnologyInstOperProductInCompFields.PRODUCT)::apply)
                .map(EntityUtils.getIdExtractor()::apply).collect(Collectors.toSet());
        if (OrderState.PENDING.getStringValue().equals(order.getStringField(OrderFields.STATE))) {
            List<Entity> allOrderMaterialCosts = allTechnologyRawProductsWithCosts.stream()
                    .filter(productWithCosts -> !existingMaterialCostIds.contains(productWithCosts.getProductId()))
                    .map(productWithCosts -> orderMaterialCostsEntityBuilder.create(order, productWithCosts))
                    .collect(Collectors.toList());
            allOrderMaterialCosts.addAll(existingOrderMaterialCosts);
            allOrderMaterialCosts = allOrderMaterialCosts
                    .stream()
                    .filter(materialCost -> !isIntermediate(order,
                            materialCost.getBelongsToField(TechnologyInstOperProductInCompFields.PRODUCT).getId()))
                    .collect(Collectors.toList());
            return allOrderMaterialCosts;
        } else {
            List<Entity> allOrderMaterialCosts = basicProductionCountingService
                    .getUsedMaterialsFromProductionCountingQuantities(order, true)
                    .stream()
                    .map(material -> orderMaterialCostsEntityBuilder.create(order,
                            material.getBelongsToField(ProductionCountingQuantityFields.PRODUCT))).collect(Collectors.toList());
            final Set<Long> allMaterialCostIds = allOrderMaterialCosts.stream()
                    .map(EntityUtils.getBelongsToFieldExtractor(TechnologyInstOperProductInCompFields.PRODUCT)::apply)
                    .map(EntityUtils.getIdExtractor()::apply).collect(Collectors.toSet());
            List<Entity> mergedOrderMaterialCosts = existingOrderMaterialCosts
                    .stream()
                    .filter(materialCost -> allMaterialCostIds.contains(materialCost.getBelongsToField(
                            TechnologyInstOperProductInCompFields.PRODUCT).getId())).collect(Collectors.toList());
            mergedOrderMaterialCosts.addAll(allOrderMaterialCosts
                    .stream()
                    .filter(materialCost -> !existingMaterialCostIds.contains(materialCost.getBelongsToField(
                            TechnologyInstOperProductInCompFields.PRODUCT).getId())).collect(Collectors.toList()));
            return mergedOrderMaterialCosts;
        }
    }

    private boolean isIntermediate(final Entity order, final Long productId) {

        Entity technology = order.getBelongsToField(OrderFields.TECHNOLOGY);
        List<Entity> opics = dataDefinitionService
                .get(TechnologiesConstants.PLUGIN_IDENTIFIER, TechnologiesConstants.MODEL_OPERATION_PRODUCT_IN_COMPONENT)
                .find()
                .createAlias(OperationProductInComponentFields.OPERATION_COMPONENT,
                        OperationProductInComponentFields.OPERATION_COMPONENT, JoinType.INNER)
                .createAlias(OperationProductInComponentFields.PRODUCT, OperationProductInComponentFields.PRODUCT, JoinType.INNER)
                .add(SearchRestrictions.eq(OperationProductInComponentFields.OPERATION_COMPONENT + ".technology.id",
                        technology.getId()))
                .add(SearchRestrictions.eq(OperationProductInComponentFields.PRODUCT + ".id", productId)).list().getEntities();
        return opics.stream().anyMatch(opic -> technologyService.isIntermediateProduct(opic));
    }

    private List<Entity> findExistingOrderMaterialCosts(final Entity order, final Collection<Long> productIds) {
        if (order.getId() == null) {
            return Collections.emptyList();
        }
        OrderMaterialCostsCriteria criteria = OrderMaterialCostsCriteria.forOrder(order.getId());
        if (productIds != null) {
            criteria.setProductCriteria(in("id", productIds));
        }
        return orderMaterialCostsDataProvider.findAll(criteria);
    }

    private List<ProductWithCosts> findRawInputProductsFor(final Long technologyId) {
        TechnologyRawInputProductComponentsCriteria criteria = TechnologyRawInputProductComponentsCriteria
                .forTechnology(technologyId);
        criteria.setSearchProjection(PRODUCT_WITH_COSTS_PROJECTION);
        return asProductsWithCosts(technologyRawInputProductComponentsDataProvider.findAll(criteria));
    }

    private List<ProductWithCosts> asProductsWithCosts(final List<Entity> projectionResults) {
        return FluentIterable.from(projectionResults).transform(ProductWithCostsBuilder.BUILD_FROM_PROJECTION).toList();
    }

    private Optional<Long> extractTechnologyIdFrom(final Entity order) {
        return FluentOptional.fromNullable(order)
                .flatMap(Optionals.lift(EntityUtils.getBelongsToFieldExtractor(OrderFields.TECHNOLOGY)))
                .flatMap(EntityUtils.getSafeIdExtractor()).toOpt();
    }

}
