package io.cloudevents.nats.impl;

import org.junit.jupiter.api.Test;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

public class NatsHeadersTest {

    public static final String emojiHeader = "Euro%20%E2%82%AC%20%F0%9F%98%80";
    public static final String emojiRaw = "Euro € 😀";
    public static final String rawTime = "2018-04-26T14:48:09+02:00";
    public static final String fullUrl = "http://localhost:8080/my/frog-died";

    @Test
    public void probeUnicode() throws UnsupportedEncodingException {
        final String unicodePokeTest = "ÿĀƝ Капрельянц";

        final String urlDecode = URLEncoder.encode(unicodePokeTest,
            StandardCharsets.UTF_8.name()).replace("+", "%20");

        assertThat(NatsHeaders.escapeHeaderValue(unicodePokeTest)).isEqualTo(urlDecode);
        assertThat(NatsHeaders.unescapeHeaderValue(urlDecode.toLowerCase())).isEqualTo(unicodePokeTest);
        assertThat(URLDecoder.decode(urlDecode.toLowerCase())).isEqualTo(unicodePokeTest);
    }

    @Test
    public void natsBindingSpecAttributeHeaderEncodingTest() {
        assertThat(NatsHeaders.escapeHeaderValue(emojiRaw)).isEqualTo( emojiHeader);
    }

    @Test
    public void natsBindingSpecAttributeHeaderDecodingTest() {
        assertThat(NatsHeaders.unescapeHeaderValue(emojiHeader)).isEqualTo(emojiRaw);
    }

    @Test
    public void unescapePhase1Test() {
        assertThat(NatsHeaders.unescapePhase1("\"\\\"\\\ttab me\"\\\"")).isEqualTo("\"\ttab me\"");
    }

    @Test
    public void unescapePhase2Test() {
        assertThat(NatsHeaders.unescapePhase2(emojiHeader)).isEqualTo(emojiRaw);
        assertThat(NatsHeaders.unescapePhase2(emojiHeader + "fred")).isEqualTo(emojiRaw + "fred");
        assertThat(NatsHeaders.unescapePhase2("++++")).isEqualTo("++++");
    }

    @Test void standardAsciiCharactersMapAsExpected() {
        assertThat(NatsHeaders.escapeHeaderValue(rawTime)).isEqualTo(rawTime);
        assertThat(NatsHeaders.escapeHeaderValue(fullUrl)).isEqualTo(fullUrl);
        assertThat(NatsHeaders.escapeHeaderValue(emojiRaw + "sausage\"")).isEqualTo(emojiHeader + "sausage%22");
    }


}
