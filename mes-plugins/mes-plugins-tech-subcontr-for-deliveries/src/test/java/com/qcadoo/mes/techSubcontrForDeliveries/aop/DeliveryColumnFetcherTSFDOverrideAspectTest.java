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
package com.qcadoo.mes.techSubcontrForDeliveries.aop;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.junit.Test;

import com.qcadoo.mes.deliveries.print.DeliveryColumnFetcher;
import com.qcadoo.mes.deliveries.print.DeliveryProduct;
import com.qcadoo.model.api.Entity;

public class DeliveryColumnFetcherTSFDOverrideAspectTest {

    @Test
    public final void checkCompareProductsExecution() throws NoSuchMethodException {
        Class<?> clazz = DeliveryColumnFetcher.class;
        assertEquals("com.qcadoo.mes.deliveries.print.DeliveryColumnFetcher", clazz.getCanonicalName());
        final Method method = clazz.getDeclaredMethod("compareProducts", DeliveryProduct.class, Entity.class);
        assertNotNull(method);
        assertTrue(Modifier.isPrivate(method.getModifiers()));
        assertEquals(boolean.class, method.getReturnType());
    }

}
