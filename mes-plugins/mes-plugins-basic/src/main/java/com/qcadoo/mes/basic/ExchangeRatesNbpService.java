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
package com.qcadoo.mes.basic;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Map;

public interface ExchangeRatesNbpService {

    String CRON_LAST_ALL = "0 20 13 ? * MON-FRI";     // working days  07:45 - 08:15 -> 08:20

    Map<String, BigDecimal> get(NbpProperties nbpProperties);

    Map<String, BigDecimal> parse(InputStream inputStream, NbpProperties nbpProperties);

    public enum NbpProperties {

        LAST_A("LastA"){
            @Override
            public String fieldName() {
                return "kurs_sredni";
            }
        },

        LAST_B("LastB"){
            @Override
            public String fieldName() {
                return "kurs_sredni";
            }
        },

        LAST_C("LastC"){
            @Override
            public String fieldName() {
                return "kurs_sprzedazy";
            }
        };

        private String xmlFileName;

        NbpProperties(String xmlFileName) {
            this.xmlFileName = xmlFileName;
        }

        public String getUrl() {
            return "http://www.nbp.pl/kursy/xml/" + xmlFileName + ".xml";
        }

        public abstract String fieldName();
    }
}
