package pl.nlogn.aws;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class TestUtil {
    public ByteBuffer byteBuffer() {
        return ByteBuffer.wrap("abc".getBytes(StandardCharsets.UTF_8));
    }

    public String encodedString() {
        return "YWJj";
    }

    public ByteBuffer encodedByteBuffer() {
        return ByteBuffer.wrap(encodedString().getBytes(StandardCharsets.UTF_8));
    }

    public int encodedLength() {
        return 4;
    }
}
