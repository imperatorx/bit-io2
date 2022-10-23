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
import java.util.Map;
import java.util.WeakHashMap;

/**
 * A writer for writing {@code float} values.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
public class FloatWriter
        extends FloatBase
        implements BitWriter<Float> {

    private static final class SignBitOnly
            extends FloatWriter {

        private SignBitOnly() {
            super(FloatConstants.SIZE_MIN_EXPONENT, FloatConstants.SIZE_MIN_SIGNIFICAND);
        }

        private void writeBits(final BitOutput output, final int bits) throws IOException {
            output.writeInt(true, 1, bits >> FloatConstants.SHIFT_SIGN_BIT);
        }

        private void writeBits(final BitOutput output, final float value) throws IOException {
            writeBits(output, Float.floatToRawIntBits(value));
        }

        @Override
        public void write(final BitOutput output, final Float value) throws IOException {
            writeBits(output, value);
        }
    }

    /**
     * A bit writer for writing {@code ±0.0f} in a compressed manner.
     *
     * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
     */
    public static final class CompressedZero
            implements BitWriter<Float> {

        private static final class Holder {

            private static final BitWriter<Float> INSTANCE = new CompressedZero();

            private static final class Nullable {

                private static final BitWriter<Float> INSTANCE = new FilterBitWriter.Nullable<>(Holder.INSTANCE);

                private Nullable() {
                    throw new AssertionError(BitIoConstants.MESSAGE_INSTANTIATION_IS_NOT_ALLOWED);
                }
            }

            private Holder() {
                throw new AssertionError(BitIoConstants.MESSAGE_INSTANTIATION_IS_NOT_ALLOWED);
            }
        }

        /**
         * Returns the instance of this class.
         *
         * @return the instance.
         * @see #getInstanceNullable()
         */
        public static BitWriter<Float> getInstance() {
            return Holder.INSTANCE;
        }

        /**
         * Returns the instance handles {@code null} values.
         *
         * @return the instance handles {@code null} values.
         * @see #getInstance()
         */
        public static BitWriter<Float> getInstanceNullable() {
            return Holder.Nullable.INSTANCE;
        }

        private CompressedZero() {
            super();
        }

        @Override
        public BitWriter<Float> nullable() {
            return getInstanceNullable();
        }

        @Override
        public void write(final BitOutput output, final Float value) throws IOException {
            delegate.write(output, value);
        }

        private final BitWriter<Float> delegate = new SignBitOnly();
    }

    /**
     * A bit writer for writing either {@link Float#NEGATIVE_INFINITY} or {@link Float#POSITIVE_INFINITY}.
     *
     * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
     */
    public static final class CompressedInfinity
            implements BitWriter<Float> {

        private static final class Holder {

            private static final BitWriter<Float> INSTANCE = new CompressedInfinity();

            private static final class Nullable {

                private static final BitWriter<Float> INSTANCE = new FilterBitWriter.Nullable<>(Holder.INSTANCE);

                private Nullable() {
                    throw new AssertionError(BitIoConstants.MESSAGE_INSTANTIATION_IS_NOT_ALLOWED);
                }
            }

            private Holder() {
                throw new AssertionError(BitIoConstants.MESSAGE_INSTANTIATION_IS_NOT_ALLOWED);
            }
        }

        /**
         * Returns the instance of this class which is singleton.
         *
         * @return the instance.
         * @see #getInstanceNullable()
         */
        public static BitWriter<Float> getInstance() {
            return Holder.INSTANCE;
        }

        /**
         * Returns the instance handles {@code null} values.
         *
         * @return the instance handles {@code null} values.
         * @see #getInstance()
         */
        public static BitWriter<Float> getInstanceNullable() {
            return Holder.Nullable.INSTANCE;
        }

        private CompressedInfinity() {
            super();
        }

        @Override
        public BitWriter<Float> nullable() {
            return getInstanceNullable();
        }

        @Override
        public void write(final BitOutput output, final Float value) throws IOException {
            delegate.write(output, value);
        }

        private final BitWriter<Float> delegate = new SignBitOnly();
    }

    /**
     * A writer for writing {@code NaN} values in a compressed manner.
     *
     * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
     */
    public static class CompressedNaN
            implements BitWriter<Float> {

        private static final Map<FloatCacheKey, BitWriter<Float>> CACHED_INSTANCES = new WeakHashMap<>();

        private static final Map<FloatCacheKey, BitWriter<Float>> CACHED_INSTANCES_NULLABLE = new WeakHashMap<>();

        static BitWriter<Float> getCachedInstance(final int significandSize) {
            return CACHED_INSTANCES.computeIfAbsent(
                    FloatCacheKey.of(significandSize),
                    k -> new CompressedNaN(k.getSignificandSize()) {
                        @Override
                        public BitWriter<Float> nullable() {
                            return CACHED_INSTANCE_NULLABLE.computeIfAbsent(FloatCacheKey.copyOf(k), k2 -> super.nullable());
                        }
                    }
            );
        }

        public CompressedNaN(final int significandSize) {
            super();
            this.significandSize = FloatConstraints.requireValidSignificandSize(significandSize);
            mask = FloatConstants.MASK_SIGNIFICAND_LEFT_MOST_BIT | BitIoUtils.bitMaskSingle(this.significandSize - 1);
        }

        @Override
        public void write(final BitOutput output, final Float value) throws IOException {
            final int significandBits = Float.floatToRawIntBits(value) & mask;
            if (significandBits == 0) {
                throw new IllegalArgumentException("significand bits are all zeros");
            }
            output.writeInt(true, 1, significandBits >> FloatConstants.SHIFT_SIGNIFICAND_LEFT_MOST_BIT);
            output.writeInt(true, significandSize - 1, significandBits);
        }

        private final int significandSize;

        private final int mask;
    }

    /**
     * A writer for writing {@code subnormal} values in a compressed manner.
     *
     * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
     */
    public static class CompressedSubnormal
            implements BitWriter<Float> {

        private static final Map<FloatCacheKey, BitWriter<Float>> CACHED_INSTANCES = new WeakHashMap<>();

        private static final Map<FloatCacheKey, BitWriter<Float>> CACHED_INSTANCES_NULLABLE = new WeakHashMap<>();

        static BitWriter<Float> getCachedInstance(final int significandSize) {
            return CACHED_INSTANCES.computeIfAbsent(
                    FloatCacheKey.of(significandSize),
                    k -> new CompressedSubnormal(k.getSignificandSize()) {
                        @Override
                        public BitWriter<Float> nullable() {
                            return CACHED_INSTANCE_NULLABLE.computeIfAbsent(FloatCacheKey.copyOf(k), k2 -> super.nullable());
                        }
                    }
            );
        }

        public CompressedSubnormal(final int significandSize) {
            super();
            this.significandSize = FloatConstraints.requireValidSignificandSize(significandSize);
            this.shift = FloatConstants.SIZE_SIGNIFICAND - this.significandSize;
            mask = BitIoUtils.bitMaskSingle(this.significandSize);
        }

        @Override
        public void write(final BitOutput output, final Float value) throws IOException {
            final int bits = Float.floatToRawIntBits(value);
            signBitWriter.writeBits(output, bits);
            final int significandBits = (bits >> shift) & mask;
            if (significandBits == 0) {
                throw new IllegalArgumentException("significand bits are all zeros");
            }
            output.writeInt(true, significandSize, significandBits);
        }

        private final SignBitOnly signBitWriter = new SignBitOnly();

        private final int significandSize;

        private final int shift;

        private final int mask;
    }

    private static void writeExponentBits(final BitOutput output, final int size, final int bits) throws IOException {
        output.writeInt(false, size, ((bits << 1) >> 1) >> FloatConstants.SIZE_SIGNIFICAND);
    }

    private static void writeSignificandBits(final BitOutput output, final int size, final int bits) throws IOException {
        output.writeInt(true, 1, bits >> FloatConstants.SHIFT_SIGNIFICAND_LEFT_MOST_BIT);
        output.writeInt(true, size - 1, bits);
    }

    static void write(final BitOutput output, final int exponentSize, final int significandSize, final float value) throws IOException {
        if (exponentSize == FloatConstants.SIZE_EXPONENT && significandSize == FloatConstants.SIZE_SIGNIFICAND) {
            output.writeInt(false, Integer.SIZE, Float.floatToRawIntBits(value));
            return;
        }
        final int bits = Float.floatToRawIntBits(value);
        output.writeInt(true, 1, bits >> FloatConstants.SHIFT_SIGN_BIT);
        writeExponentBits(output, exponentSize, bits);
        writeSignificandBits(output, significandSize, bits);
    }

    private static final Map<FloatCacheKey, BitWriter<Float>> CACHED_INSTANCE = new WeakHashMap<>();

    private static final Map<FloatCacheKey, BitWriter<Float>> CACHED_INSTANCE_NULLABLE = new WeakHashMap<>();

    /**
     * Returns a cached instance for specified sizes of exponent part and significand part, respectively.
     *
     * @param exponentSize    the number of bits for the exponent part; between
     *                        {@value FloatConstants#SIZE_MIN_EXPONENT} and {@value FloatConstants#SIZE_EXPONENT}, both
     *                        inclusive.
     * @param significandSize the number of bits for the significand part; between
     *                        {@value FloatConstants#SIZE_MIN_SIGNIFICAND} and {@value FloatConstants#SIZE_SIGNIFICAND},
     *                        both inclusive.
     * @return a cached instance.
     */
    static BitWriter<Float> getCachedInstance(final int exponentSize, final int significandSize) {
        return CACHED_INSTANCE.computeIfAbsent(
                FloatCacheKey.of(exponentSize, significandSize),
                k -> new FloatWriter(k.getExponentSize(), k.getSignificandSize()) {
                    @Override
                    public BitWriter<Float> nullable() {
                        return CACHED_INSTANCE_NULLABLE.computeIfAbsent(FloatCacheKey.copyOf(k), k2 -> super.nullable());
                    }
                }
        );
    }

    /**
     * Creates a new instance with specified exponent size and significand size.
     *
     * @param exponentSize    the number of bits for the exponent part; between
     *                        {@value FloatConstants#SIZE_MIN_EXPONENT} and {@value FloatConstants#SIZE_EXPONENT}, both
     *                        inclusive.
     * @param significandSize the number of bits for the significand part; between
     *                        {@value FloatConstants#SIZE_MIN_SIGNIFICAND} and {@value FloatConstants#SIZE_SIGNIFICAND},
     *                        both inclusive.
     */
    public FloatWriter(final int exponentSize, final int significandSize) {
        super(exponentSize, significandSize);
    }

    @Override
    public void write(final BitOutput output, final Float value) throws IOException {
        write(output, exponentSize, significandSize, value);
    }
}
