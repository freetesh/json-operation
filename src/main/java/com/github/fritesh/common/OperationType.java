package com.github.fritesh.common;

import com.github.fritesh.difference.DifferenceOperation;
import com.github.fritesh.operation.Add;
import com.github.fritesh.operation.Remove;
import com.github.fritesh.operation.Replace;

public enum OperationType {
    ADD {
        @Override
        JsonOperation operation(final DifferenceOperation op) {
            return new Add(op.getPath(), op.getNewValue(), op.getOldValue(), op.getLocator());
        }
    },
    REMOVE {
        @Override
        JsonOperation operation(final DifferenceOperation op) {
            return new Remove(op.getPath(), op.getNewValue(), op.getOldValue(), op.getLocator());
        }
    },
    REPLACE {
        @Override
        JsonOperation operation(final DifferenceOperation op) {
            return new Replace(op.getPath(), op.getNewValue(), op.getOldValue(), op.getLocator());
        }
    },;

    abstract JsonOperation operation(final DifferenceOperation op);
}
