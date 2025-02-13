/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.opensearch.ml.action.handler;

import static org.opensearch.rest.RestStatus.BAD_REQUEST;
import static org.opensearch.rest.RestStatus.INTERNAL_SERVER_ERROR;

import lombok.extern.log4j.Log4j2;

import org.opensearch.OpenSearchStatusException;
import org.opensearch.action.ActionListener;
import org.opensearch.action.search.SearchRequest;
import org.opensearch.action.search.SearchResponse;
import org.opensearch.client.Client;
import org.opensearch.common.util.concurrent.ThreadContext;
import org.opensearch.core.xcontent.NamedXContentRegistry;
import org.opensearch.index.IndexNotFoundException;
import org.opensearch.indices.InvalidIndexNameException;
import org.opensearch.ml.common.exception.MLException;
import org.opensearch.ml.common.exception.MLResourceNotFoundException;
import org.opensearch.rest.RestStatus;

import com.google.common.base.Throwables;

/**
 * Handle general get and search request in ml common.
 */
@Log4j2
public class MLSearchHandler {
    private final Client client;
    private NamedXContentRegistry xContentRegistry;

    public MLSearchHandler(Client client, NamedXContentRegistry xContentRegistry) {
        this.client = client;
        this.xContentRegistry = xContentRegistry;
    }

    public void search(SearchRequest request, ActionListener<SearchResponse> actionListener) {
        ActionListener<SearchResponse> listener = wrapRestActionListener(actionListener, "Fail to search");
        try (ThreadContext.StoredContext context = client.threadPool().getThreadContext().stashContext()) {
            client.search(request, listener);
        } catch (Exception e) {
            log.error("Failed to search", e);
            listener.onFailure(e);
        }
    }

    /**
     * Wrap action listener to avoid return verbose error message and wrong 500 error to user.
     * Suggestion for exception handling in ML common:
     * 1. If the error is caused by wrong input, throw IllegalArgumentException exception.
     * 2. For other errors, please use MLException or its subclass, or use
     *    OpenSearchStatusException.
     *
     * TODO: tune this function for wrapped exception, return root exception error message
     *
     * @param actionListener action listener
     * @param generalErrorMessage general error message
     * @param <T> action listener response type
     * @return wrapped action listener
     */
    public static <T> ActionListener<T> wrapRestActionListener(ActionListener<T> actionListener, String generalErrorMessage) {
        return ActionListener.<T>wrap(r -> { actionListener.onResponse(r); }, e -> {
            log.error("Wrap exception before sending back to user", e);
            Throwable cause = Throwables.getRootCause(e);
            if (isProperExceptionToReturn(e)) {
                actionListener.onFailure(e);
            } else if (isProperExceptionToReturn(cause)) {
                actionListener.onFailure((Exception) cause);
            } else {
                RestStatus status = isBadRequest(e) ? BAD_REQUEST : INTERNAL_SERVER_ERROR;
                String errorMessage = generalErrorMessage;
                if (isBadRequest(e) || e instanceof MLException) {
                    errorMessage = e.getMessage();
                } else if (cause != null && (isBadRequest(cause) || cause instanceof MLException)) {
                    errorMessage = cause.getMessage();
                }
                actionListener.onFailure(new OpenSearchStatusException(errorMessage, status));
            }
        });
    }

    public static boolean isProperExceptionToReturn(Throwable e) {
        return e instanceof OpenSearchStatusException || e instanceof IndexNotFoundException || e instanceof InvalidIndexNameException;
    }

    public static boolean isBadRequest(Throwable e) {
        return e instanceof IllegalArgumentException || e instanceof MLResourceNotFoundException;
    }
}
