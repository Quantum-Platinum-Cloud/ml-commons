/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.opensearch.ml.indices;

import static org.opensearch.ml.common.CommonValue.ML_MODEL_INDEX;
import static org.opensearch.ml.common.CommonValue.ML_MODEL_INDEX_MAPPING;
import static org.opensearch.ml.common.CommonValue.ML_MODEL_INDEX_SCHEMA_VERSION;
import static org.opensearch.ml.common.CommonValue.ML_TASK_INDEX;
import static org.opensearch.ml.common.CommonValue.ML_TASK_INDEX_MAPPING;
import static org.opensearch.ml.common.CommonValue.ML_TASK_INDEX_SCHEMA_VERSION;

public enum MLIndex {
    MODEL(ML_MODEL_INDEX, false, ML_MODEL_INDEX_MAPPING, ML_MODEL_INDEX_SCHEMA_VERSION),
    TASK(ML_TASK_INDEX, false, ML_TASK_INDEX_MAPPING, ML_TASK_INDEX_SCHEMA_VERSION);

    private final String indexName;
    // whether we use an alias for the index
    private final boolean alias;
    private final String mapping;
    private final Integer version;

    MLIndex(String name, boolean alias, String mapping, Integer version) {
        this.indexName = name;
        this.alias = alias;
        this.mapping = mapping;
        this.version = version;
    }

    public String getIndexName() {
        return indexName;
    }

    public boolean isAlias() {
        return alias;
    }

    public String getMapping() {
        return mapping;
    }

    public Integer getVersion() {
        return version;
    }
}
