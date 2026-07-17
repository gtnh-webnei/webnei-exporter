package moe.takochan.webnei.exporter.bundle.tsv;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

class TsvCellCodecTest {

    @Test
    void roundTripsSchemaV4Escapes() {
        String value = "path\\name\tfirst\rsecond\nthird";
        String encoded = TsvCellCodec.encode(value);

        assertEquals("path\\\\name\\tfirst\\rsecond\\nthird", encoded);
        assertFalse(encoded.contains("\t"));
        assertFalse(encoded.contains("\r"));
        assertFalse(encoded.contains("\n"));
        assertEquals(value, TsvCellCodec.decode(encoded));
    }
}
