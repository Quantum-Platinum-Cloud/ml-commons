/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.opensearch.ml.common.transport.deploy;

import lombok.Builder;
import lombok.Data;
import org.opensearch.common.io.stream.StreamInput;
import org.opensearch.common.io.stream.StreamOutput;
import org.opensearch.common.io.stream.Writeable;
import org.opensearch.ml.common.MLTask;

import java.io.IOException;

@Data
public class MLDeployModelInput implements Writeable {
    private String modelId;
    private String taskId;
    private String modelContentHash;
    private Integer nodeCount;
    private String coordinatingNodeId;
    private MLTask mlTask;

    public MLDeployModelInput(StreamInput in) throws IOException {
        this.modelId = in.readString();
        this.taskId = in.readString();
        this.modelContentHash = in.readOptionalString();
        this.nodeCount = in.readInt();
        this.coordinatingNodeId = in.readString();
        this.mlTask = new MLTask(in);
    }

    @Builder
    public MLDeployModelInput(String modelId, String taskId, String modelContentHash, Integer nodeCount, String coordinatingNodeId, MLTask mlTask) {
        this.modelId = modelId;
        this.taskId = taskId;
        this.modelContentHash = modelContentHash;
        this.nodeCount = nodeCount;
        this.coordinatingNodeId = coordinatingNodeId;
        this.mlTask = mlTask;
    }

    public MLDeployModelInput() {
    }

    @Override
    public void writeTo(StreamOutput out) throws IOException {
        out.writeString(modelId);
        out.writeString(taskId);
        out.writeOptionalString(modelContentHash);
        out.writeInt(nodeCount);
        out.writeString(coordinatingNodeId);
        mlTask.writeTo(out);
    }

}
