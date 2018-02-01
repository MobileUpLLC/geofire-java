package com.firebase.geofire.core;

import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.util.Base32Utils;

import java.nio.CharBuffer;

import static com.firebase.geofire.util.Base32Utils.BITS_PER_BASE32_CHAR;

public class RouteGeoHash extends GeoHash {

    private static int BIT_MASK_FOR_BASE32_CHAR = 0x1f;

    public RouteGeoHash(GeoLocation location1, GeoLocation location2, int precision) {
        super(getGeoHashFromRoute(location1, location2, precision));
    }

    private static String getGeoHashFromRoute(GeoLocation location1, GeoLocation location2, int precision) {

        final CharBuffer buffer = CharBuffer.allocate(2 * precision);

        final GeoHash geoHash1 = new GeoHash(location1);
        final GeoHash geoHash2 = new GeoHash(location2);
        final char[] originHashBuffer = geoHash1.getGeoHashString().toCharArray();
        final char[] destinationHashBuffer = geoHash2.getGeoHashString().toCharArray();
        for (int i = 0; i < precision; i++) {
            int x = Base32Utils.base32CharToValue(originHashBuffer[i]);
            int y = Base32Utils.base32CharToValue(destinationHashBuffer[i]);
            int z = interleaveBitsOf(x, y);
            buffer.put(2 * i, Base32Utils.valueToBase32Char((z & (BIT_MASK_FOR_BASE32_CHAR << BITS_PER_BASE32_CHAR)) >> BITS_PER_BASE32_CHAR));
            buffer.put(2 * i + 1, Base32Utils.valueToBase32Char(z & BIT_MASK_FOR_BASE32_CHAR));

        }

        return String.copyValueOf(buffer.array());
    }

    private static int interleaveBitsOf(int x, int y) {
        Long q = ((x * 0x0101010101010101L & 0x0040201008040201L) * 0x0102040810204081L >> 49) & 0x5555L;
        Long w = ((y * 0x0101010101010101L & 0x0040201008040201L) * 0x0102040810204081L >> 48) & 0xAAAAL;
        Long result = (q | w) & 0x3fffffffffffffffL;
        return result.intValue();
    }
}
