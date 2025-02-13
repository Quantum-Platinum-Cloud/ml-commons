/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.opensearch.ml.common.output.model;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.opensearch.common.io.stream.BytesStreamOutput;
import org.opensearch.common.io.stream.StreamInput;
import org.opensearch.common.xcontent.XContentType;
import org.opensearch.core.xcontent.XContentBuilder;
import org.opensearch.ml.common.TestHelper;

import java.io.IOException;
import java.nio.ByteBuffer;

import static org.junit.Assert.assertEquals;
import static org.opensearch.core.xcontent.ToXContent.EMPTY_PARAMS;

public class ModelTensorTest {

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    private ModelTensor modelTensor;

    @Before
    public void setUp() {
        modelTensor = ModelTensor.builder()
                .name("model_tensor")
                .data(new Number[]{1, 2, 3})
                .shape(new long[]{1, 2, 3,})
                .dataType(MLResultDataType.INT32)
                .byteBuffer(ByteBuffer.wrap(new byte[]{0,1,0,1}))
                .build();
    }

    @Test
    public void test_StreamInAndOut() throws IOException {
        BytesStreamOutput bytesStreamOutput = new BytesStreamOutput();
        modelTensor.writeTo(bytesStreamOutput);

        StreamInput streamInput = bytesStreamOutput.bytes().streamInput();
        ModelTensor parsedTensor = new ModelTensor(streamInput);
        assertEquals(parsedTensor, modelTensor);
    }

    @Test
    public void test_ModelTensorSuccess() throws IOException {
        XContentBuilder builder = XContentBuilder.builder(XContentType.JSON.xContent());
        modelTensor.toXContent(builder, EMPTY_PARAMS);
        String modelTensorContent = TestHelper.xContentBuilderToString(builder);
        assertEquals("{\"name\":\"model_tensor\",\"data_type\":\"INT32\",\"shape\":[1,2,3],\"data\":[1,2,3],\"byte_buffer\":{\"array\":\"AAEAAQ==\",\"order\":\"BIG_ENDIAN\"}}", modelTensorContent);
    }

    @Test
    public void toXContent_NullValue() throws IOException {
        ModelTensor tensor = ModelTensor.builder().build();
        XContentBuilder builder = XContentBuilder.builder(XContentType.JSON.xContent());
        tensor.toXContent(builder, EMPTY_PARAMS);
        String modelTensorContent = TestHelper.xContentBuilderToString(builder);
        assertEquals("{}", modelTensorContent);
    }

    @Test
    public void test_StreamInAndOut_NullValue() throws IOException {
        ModelTensor tensor = ModelTensor.builder().build();
        BytesStreamOutput bytesStreamOutput = new BytesStreamOutput();
        tensor.writeTo(bytesStreamOutput);

        StreamInput streamInput = bytesStreamOutput.bytes().streamInput();
        ModelTensor parsedTensor = new ModelTensor(streamInput);
        assertEquals(parsedTensor, tensor);
    }

    @Test
    public void test_UnknownDataType() {
        exceptionRule.expect(IllegalArgumentException.class);
        exceptionRule.expectMessage("data type is null");
        ModelTensor tensor = new ModelTensor("null_data", new Number[]{1, 2, 3}, null, MLResultDataType.UNKNOWN, ByteBuffer.wrap(new byte[]{0,1,0,1}));
    }

    @Test
    public void test_NullDataType() {
        exceptionRule.expect(IllegalArgumentException.class);
        exceptionRule.expectMessage("data type is null");
        ModelTensor tensor = new ModelTensor("null_data", new Number[]{1, 2, 3}, null, null, ByteBuffer.wrap(new byte[]{0,1,0,1}));
    }
}

