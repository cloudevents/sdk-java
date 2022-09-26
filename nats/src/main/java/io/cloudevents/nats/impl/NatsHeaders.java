package io.cloudevents.nats.impl;

import io.cloudevents.core.v1.CloudEventV1;

import java.io.CharArrayWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.BitSet;
import java.util.stream.Collectors;

public class NatsHeaders {
    public static final String CE_EVENT_FORMAT =  "ce-event-format";
    protected static final String CE_PREFIX = "ce-";

    public static final String CONTENT_TYPE = "content-type";

    static BitSet skipEncoding;

    public static int defaultBufferSizeForHeaderEncoding = 1024;

    static {
        /*
        The following characters MUST be percent-encoded:

        * Space (U+0020)
        * Double-quote (U+0022)
        * Percent (U+0025)
        * Any characters outside the printable ASCII range of U+0021-U+007E inclusive

        Attribute values are already constrained to prohibit characters in the range U+0000-U+001F inclusive and U+007F-U+009F inclusive;
        however for simplicity and to account for potential future changes, it is RECOMMENDED that any NATS header encoding implementation
        treats such characters as requiring percent-encoding.
         */
        skipEncoding = new BitSet(256);
        for(int counter = 0x21; counter <= 0x7E; counter ++) {
            skipEncoding.set(counter);
        }
        skipEncoding.clear(0x22); // "
        skipEncoding.clear(0x25); // %
    }

    static class Collecting {
        public final CharArrayWriter caw;
        public boolean unicodeByte2Check = false;

        Collecting(String value) {
            this.caw  = new CharArrayWriter(Math.max(value.length(), defaultBufferSizeForHeaderEncoding));;
        }
    }

    /**
     * The rules around NATS header encoding are quite specific and annoying. We cannot use the URLEncoder and just search and replace
     * specific % combinations because we don't know their context.
     *
     * @param value the header to be encoded
     * @return the encoded value
     */
    protected static String escapeHeaderValue(String value) {
        int len = value.length();
        CharArrayWriter caw = new CharArrayWriter(Math.max(len, defaultBufferSizeForHeaderEncoding));

        Collecting collecting = new Collecting(value);

        return value.chars().mapToObj(i -> {
            char c = (char)i;
            if (!collecting.unicodeByte2Check && skipEncoding.get(i)) {
                return encode(caw) + c;
            }

            if (collecting.unicodeByte2Check) {
                if (Character.isLowSurrogate(c)) {
                    caw.write(i);
                    collecting.unicodeByte2Check = false;
                    return "";
                }
                if (skipEncoding.get(i)) {
                    collecting.unicodeByte2Check = false;
                    return encode(caw) + i;
                }
            }

            caw.write(i);
            collecting.unicodeByte2Check = Character.isHighSurrogate(c);
            return "";
        }).collect(Collectors.joining()) + encode(caw);
    }

    private static String encode(CharArrayWriter caw) {
        if (caw.size() == 0) return "";

        StringBuilder out = new StringBuilder();

        for (byte b : new String(caw.toCharArray()).getBytes(StandardCharsets.UTF_8)) {
            /*
            from spec: Steps to encode a Unicode character:

            Encode the character using UTF-8, to obtain a byte sequence.
            Encode each byte within the sequence as %xy where x is a hexadecimal representation of the most
            significant 4 bits of the byte, and y is a hexadecimal representation of the least significant 4 bits of the byte.
             */
            out.append('%');
            out.append(byteToChar(b >> 4));
            out.append(byteToChar(b & 0xF ));
        }

        caw.reset();

        return out.toString();
    }

    private static final int forceUpperCase = ('a' - 'A');
    // Percent-encoding SHOULD be performed using upper-case for values A-F, and forDigit always returns lower case
    private static char byteToChar(int b) {
        char ch = Character.forDigit(b & 0xF, 16);
        // forDigit returns lower case and we need upper case
        if (Character.isLetter(ch)) {
            ch -= forceUpperCase;
        }
        return ch;
    }

    protected static String unescapeHeaderValue(String value) {
        return  unescapePhase2(unescapePhase1(value));
    }

    private static int sign(int val) {
        return (val > 127) ? (val - 256) : val;
    }

    /*
    A single round of percent-decoding MUST then be performed as described below

    When performing percent-decoding (when decoding an NATS message to a CloudEvent), values that have been
    unncessarily percent-encoded MUST be accepted, but encoded byte sequences which are invalid in UTF-8 MUST be rejected.
    (For example, "%C0%A0" is an overlong encoding of U+0020, and MUST be rejected.)

    We cannot support the later requirement, the Character set decoder does not ignore when set to ignore. Otherwise the
    URLDecoder does a perfect job of this.
     */
    protected static String unescapePhase2(String value) {
        try {
            return URLDecoder.decode(value.replace("+", "%2B"), StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }


    /*
    When decoding an NATS message into a CloudEvent, any NATS header value MUST first be unescaped
    with respect to double-quoted strings, as described in RFC7230, section 3.2.6. NATS headers for CloudEvent attribute
    values do not support parenthetical comments, so the initial unescaping only needs to handle double-quoted values, including
    processing backslash escapes within double-quoted values. Header values produced via the percent-encoding described here will
    never include double-quoted values, but they MUST be supported when receiving events, for compatibility with older versions of this
    specification which did not require double-quote and space characters to be percent-encoded.
     */
    protected static String unescapePhase1(String value) {
        int slen = value.length();

        StringBuilder out = new StringBuilder();

        boolean changed = false;

        for(int index = 0; index < slen; index ++) {
            char c = value.charAt(index);
            if (c == '\\') {
                changed = true;
                if (index + 1 < slen) {
                    index ++;
                    out.append(value.charAt(index));
                }
            } else if (c != '"') {
                out.append(c);
            } else {
                changed = true;
            }
        }
        return changed ? out.toString() : value;
    }

    protected static String headerMapper(String header) {
        if (CloudEventV1.DATACONTENTTYPE.equals(header)) {
            return CONTENT_TYPE;
        }

        return CE_PREFIX + header;
    }

    protected static String headerUnmapper(String header) {
        if (CONTENT_TYPE.equals(header)) {
            return CloudEventV1.DATACONTENTTYPE;
        }

        return header.substring(CE_PREFIX.length());
    }


    public static final String SPEC_VERSION = headerMapper(CloudEventV1.SPECVERSION);
}
