package com.github.jinahya.bit.io;

/*-
 * #%L
 * bit-io2
 * %%
 * Copyright (C) 2020 Jinahya, Inc.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.io.IOException;
import java.util.Objects;

import static com.github.jinahya.bit.io.BitIoConstraints.requireValidSizeForInt;

abstract class PrimitiveArrayReader<T>
        implements ValueReader<T> {

    protected PrimitiveArrayReader(final int lengthSize) {
        super();
        this.lengthSize = requireValidSizeForInt(true, lengthSize);
    }

    protected int readLength(final BitInput input) throws IOException {
        Objects.requireNonNull(input, "input is null");
        return input.readUnsignedInt(lengthSize);
    }

    private final int lengthSize;
}
