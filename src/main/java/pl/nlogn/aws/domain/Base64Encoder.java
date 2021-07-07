package pl.nlogn.aws.domain;

import org.springframework.stereotype.Component;
import reactor.util.function.Tuple3;
import reactor.util.function.Tuples;

import java.nio.ByteBuffer;
import java.util.Base64;

@Component
public class Base64Encoder {
    public Tuple3<ByteBuffer, Integer, String> encode(ByteBuffer byteBuffer) {
        ByteBuffer encodedBuffer = Base64.getEncoder().encode(byteBuffer);
        int encodedContentLength = encodedBuffer.remaining();
        String encodedBufferString = new String(encodedBuffer.array());
        return Tuples.of(encodedBuffer, encodedContentLength, encodedBufferString);
    }
}
