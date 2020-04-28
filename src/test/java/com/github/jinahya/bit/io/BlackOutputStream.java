package com.github.jinahya.bit.io;

/*-
 * #%L
 * bit-io
 * %%
 * Copyright (C) 2014 - 2019 Jinahya, Inc.
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

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.OutputStream;

/**
 * An output stream whose {@link OutputStream#write(int)} method ignores specified byte.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see WhiteInputStream
 * @see BlackByteChannel
 */
@Slf4j
final class BlackOutputStream extends OutputStream {

    // -----------------------------------------------------------------------------------------------------------------
    static final OutputStream INSTANCE = new BlackOutputStream();

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Writes the specified byte into this output stream. The {@code write(int)} method of {@code BlackOutputStream}
     * class does nothing.
     *
     * @param b the byte.
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public void write(final int b) throws IOException {
        // does nothing.
    }
}
