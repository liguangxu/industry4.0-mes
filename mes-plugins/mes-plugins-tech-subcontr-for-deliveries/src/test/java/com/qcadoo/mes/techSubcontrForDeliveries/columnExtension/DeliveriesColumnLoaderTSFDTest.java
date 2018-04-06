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
package com.qcadoo.mes.techSubcontrForDeliveries.columnExtension;

import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import com.qcadoo.mes.deliveries.DeliveriesColumnLoaderService;

public class DeliveriesColumnLoaderTSFDTest {

    private DeliveriesColumnLoaderTSFD deliveriesColumnLoaderTSFD;

    @Mock
    private DeliveriesColumnLoaderService deliveriesColumnLoaderService;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        deliveriesColumnLoaderTSFD = new DeliveriesColumnLoaderTSFD();

        ReflectionTestUtils.setField(deliveriesColumnLoaderTSFD, "deliveriesColumnLoaderService", deliveriesColumnLoaderService);
    }

    @Test
    public void shouldAddColumnsForDeliveriesTSFD() {
        // given

        // when
        deliveriesColumnLoaderTSFD.addColumnsForDeliveriesTSFD();

        // then
        verify(deliveriesColumnLoaderService).fillColumnsForDeliveries(Mockito.anyString());
    }

    @Test
    public void shouldDeleteTSFDcolumnsForDeliveries() {
        // given

        // when
        deliveriesColumnLoaderTSFD.deleteColumnsForDeliveriesTSFD();

        // then
        verify(deliveriesColumnLoaderService).clearColumnsForDeliveries(Mockito.anyString());
    }

    @Test
    public void shouldAddColumnsForOrdersTSFD() {
        // given

        // when
        deliveriesColumnLoaderTSFD.addColumnsForOrdersTSFD();

        // then
        verify(deliveriesColumnLoaderService).fillColumnsForOrders(Mockito.anyString());
    }

    @Test
    public void shouldDeleteColumnsForOrdersTSFD() {
        // given

        // when
        deliveriesColumnLoaderTSFD.deleteColumnsForOrdersTSFD();

        // then
        verify(deliveriesColumnLoaderService).clearColumnsForOrders(Mockito.anyString());
    }

}
