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

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import static com.github.jinahya.bit.io.BitIoTestUtils.wr1u;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * A class for testing {@link DoubleWriter.CompressedNaN} and {@link DoubleReader.CompressedNaN}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class Double_Wr_CompressedNaN_Test {

    private static LongStream bitsStream() {
        return DoubleTestParameters.bitsStream();
    }

    static Stream<Double> valueStream() {
        return DoubleTestParameters.valueStream()
                .filter(v -> (Double.doubleToRawLongBits(v) & DoubleConstants.MASK_SIGNIFICAND) > 0);
    }

    @MethodSource({"valueStream"})
    @ParameterizedTest
    void wr__(final Double value) throws IOException {
        {
            final var actual = wr1u(o -> {
                new DoubleWriter.CompressedNaN(DoubleConstants.SIZE_SIGNIFICAND).write(o, value);
                return i -> new DoubleReader.CompressedNaN(DoubleConstants.SIZE_SIGNIFICAND).read(i);
            });
            assertThat(actual).isNaN();
        }
        {
            final var actual = wr1u(o -> {
                DoubleWriter.CompressedNaN.getCachedInstance(DoubleConstants.SIZE_SIGNIFICAND).write(o, value);
                return i -> DoubleReader.CompressedNaN.getCachedInstance(DoubleConstants.SIZE_SIGNIFICAND).read(i);
            });
            assertThat(actual).isNaN();
        }
    }

    @MethodSource({"valueStream"})
    @ParameterizedTest
    void wr__Nullable(final Double value) throws IOException {
        {
            final var actual = wr1u(o -> {
                new DoubleWriter.CompressedNaN(DoubleConstants.SIZE_SIGNIFICAND).nullable().write(o, value);
                return i -> new DoubleReader.CompressedNaN(DoubleConstants.SIZE_SIGNIFICAND).nullable().read(i);
            });
            assertThat(actual).isNaN();
        }
        {
            final var actual = wr1u(o -> {
                DoubleWriter.CompressedNaN.getCachedInstance(DoubleConstants.SIZE_SIGNIFICAND).nullable()
                        .write(o, value);
                return i -> DoubleReader.CompressedNaN.getCachedInstance(DoubleConstants.SIZE_SIGNIFICAND).nullable()
                        .read(i);
            });
            assertThat(actual).isNaN();
        }
    }
}
