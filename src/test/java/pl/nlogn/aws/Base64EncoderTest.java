package pl.nlogn.aws;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.nlogn.aws.domain.Base64Encoder;
import reactor.util.function.Tuple3;

import java.nio.ByteBuffer;

import static org.assertj.core.api.Assertions.assertThat;

public class Base64EncoderTest {
    private Base64Encoder sut;

    private final TestUtil testUtil = new TestUtil();

    @BeforeEach
    public void setUp() {
        sut = new Base64Encoder();
    }

    @Test
    public void testEncode() {
        Tuple3<ByteBuffer, Integer, String> encodeResult = sut.encode(testUtil.byteBuffer());

        assertThat(encodeResult.getT2() == testUtil.encodedLength());
        assertThat(encodeResult.getT3().equals(testUtil.encodedString()));
    }
}
