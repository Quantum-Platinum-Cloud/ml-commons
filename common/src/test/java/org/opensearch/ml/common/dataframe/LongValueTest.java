/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.opensearch.ml.common.dataframe;

import org.junit.Test;
import org.opensearch.common.Strings;
import org.opensearch.common.xcontent.XContentFactory;
import org.opensearch.common.xcontent.XContentType;
import org.opensearch.core.xcontent.XContentBuilder;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class LongValueTest {

    @Test
    public void longValue() {
        LongValue longValue = new LongValue((long)2);
        assertEquals(ColumnType.LONG, longValue.columnType());
        assertEquals(2L, longValue.getValue());
        assertEquals(2.0d, longValue.doubleValue(), 1e-5);
    }

    @Test
    public void testToXContent() throws IOException {
        LongValue longValue = new LongValue((long)2);
        XContentBuilder builder = XContentFactory.contentBuilder(XContentType.JSON);
        longValue.toXContent(builder);

        assertNotNull(builder);
        String jsonStr = Strings.toString(builder);
        assertEquals("{\"column_type\":\"LONG\",\"value\":2}", jsonStr);
    }
}
