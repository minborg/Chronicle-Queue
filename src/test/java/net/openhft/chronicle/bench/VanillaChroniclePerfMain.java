package net.openhft.chronicle.bench;

import net.openhft.chronicle.Chronicle;
import net.openhft.chronicle.ExcerptAppender;
import net.openhft.chronicle.ExcerptTailer;
import net.openhft.chronicle.core.OS;
import net.openhft.chronicle.core.io.IOTools;
import net.openhft.chronicle.core.util.Histogram;
import net.openhft.lang.io.Bytes;

import java.io.IOException;

import static net.openhft.chronicle.ChronicleQueueBuilder.vanilla;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class VanillaChroniclePerfMain {
    // blackholes to avoid code elimination.
    static int s32;
    static long s64;
    static float f32;
    static double f64;
    static String s;

    public static void main(String[] args) throws IOException {
        final int count = 1_000_000;
        final int size = 4 << 10;
        for (int t = 0; t < 2; t++) {
            doPerfTest(new TestWriter<Bytes>() {
                @Override
                public void writeTo(Bytes bytes) {
                    writeMany(bytes, size);
                }
            }, new TestReader<Bytes>() {
                @Override
                public void readFrom(Bytes bytes) {
                    readMany(bytes, size);
                }
            }, t == 0 ? 100_000 : count, t > 0);
        }
    }

    static void doPerfTest(TestWriter<Bytes> writer, TestReader<Bytes> reader, int count, boolean print) throws IOException {
        Histogram writeHdr = new Histogram(30, 7);
        Histogram readHdr = new Histogram(30, 7);
        String file = OS.TARGET + "/deleteme-" + System.nanoTime();
        try (Chronicle chronicle = vanilla(file).dataBlockSize(64 << 20).cleanupOnClose(true).build()) {
            ExcerptAppender appender = chronicle.createAppender();
            for (int i = 0; i < count; i++) {
                long start = System.nanoTime();
                appender.startExcerpt();
                writer.writeTo(appender);
                appender.finish();
                long time = System.nanoTime() - start;
                writeHdr.sample(time);
            }

            ExcerptTailer tailer = chronicle.createTailer();
            for (int i = 0; i < count; i++) {
                long start2 = System.nanoTime();
                assertTrue(tailer.nextIndex());
                reader.readFrom(tailer);
                tailer.finish();
                long time2 = System.nanoTime() - start2;
                readHdr.sample(time2);
            }
        }
        if (print) {
            System.out.println("Write latencies " + writeHdr.toMicrosFormat());
            System.out.println("Read latencies " + readHdr.toMicrosFormat());
        }
        IOTools.deleteDirWithFiles(file, 3);
    }

    static void writeMany(Bytes bytes, int size) {
        for (int i = 0; i < size; i += 32) {
            bytes.writeInt(i);// 4 bytes
            bytes.writeFloat(i);// 4 bytes
            bytes.writeLong(i);// 8 bytes
            bytes.writeDouble(i);// 8 bytes
            bytes.writeUTFΔ("Hello!!"); // 8 bytes
        }
    }

    static void readMany(Bytes bytes, int size) {
        for (int i = 0; i < size; i += 32) {
            s32 = bytes.readInt();// 4 bytes
            f32 = bytes.readFloat();// 4 bytes
            s64 = bytes.readLong();// 8 bytes
            f64 = bytes.readDouble();// 8 bytes
            s = bytes.readUTFΔ(); // 8 bytes
            assertEquals("Hello!!", s);
        }
    }

    interface TestWriter<T> {
        void writeTo(T t);
    }

    interface TestReader<T> {
        void readFrom(T t);
    }
}
