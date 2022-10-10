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
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.github.jinahya.bit.io.BitIoTestUtils.wr1u;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * A class for testing {@link BitOutput#writeFloatOfZero(float)} method and {@link BitInput#readFloatOfZero()} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class BitIo_Float_Zero_Test {

    private static IntStream bitsStream() {
        return IntStream.of(
                Integer.MIN_VALUE,
                -1,
                0,
                +1,
                Integer.MAX_VALUE,
                ThreadLocalRandom.current().nextInt() >>> 1, // random positive
                ThreadLocalRandom.current().nextInt() | Integer.MIN_VALUE // random negative
        );
    }

    static Stream<Float> valueStream() {
        return Stream.concat(
                bitsStream().mapToObj(Float::intBitsToFloat),
                Stream.of(
                        Float.NEGATIVE_INFINITY,
                        Float.POSITIVE_INFINITY
                )
        );
    }

    static void validate(final Float written, final Float read) {
        assertThat(read + .0f).isZero();
        assertThat(read.floatValue()).isZero();
        assertThat(read.floatValue()).isGreaterThanOrEqualTo(+.0f); // 이래서 .0f 로 비교하면 안된다!
        assertThat(read.floatValue()).isLessThanOrEqualTo(+.0f);
        assertThat(read.floatValue()).isGreaterThanOrEqualTo(-.0f);
        assertThat(read.floatValue()).isLessThanOrEqualTo(-.0f);
        final var valueBits = Float.floatToRawIntBits(written);
        final var actualBits = Float.floatToRawIntBits(read);
        if (valueBits >= 0) {
            assertThat(read).isEqualTo(+.0f);
            assertThat(actualBits).isEqualTo(FloatTestConstants.POSITIVE_ZERO_BITS);
        } else {
            assertThat(read).isEqualTo(-.0f);
            assertThat(actualBits).isEqualTo(FloatTestConstants.NEGATIVE_ZERO_BITS);
        }
    }

    @MethodSource({"valueStream"})
    @ParameterizedTest
    void rw__(final Float value) throws IOException {
        final var actual = wr1u(o -> {
            o.writeFloatOfZero(value);
            return BitInput::readFloatOfZero;
        });
        validate(value, actual);
    }
}
