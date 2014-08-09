package com.lyndir.omicron.api.core;

import com.google.common.base.Splitter;
import com.lyndir.lhunath.opal.system.util.MetaObject;
import java.io.Serializable;
import java.util.*;
import javax.annotation.Nullable;


/**
 * <i>10 15, 2012</i>
 *
 * @author lhunath
 */
public class Color extends MetaObject implements Serializable {

    private static final byte[] randomRGB = new byte[3];
    private static final Random RANDOM    = new Random();

    public static Color random() {
        RANDOM.nextBytes( randomRGB );
        return new Color( randomRGB[0], randomRGB[1], randomRGB[2] );
    }

    public enum Template {

        RED( ofHex( "FF0000" ) ),
        GREEN( ofHex( "00FF00" ) ),
        BLUE( ofHex( "0000FF" ) ),
        BLACK( ofHex( "000000" ) ),
        WHITE( ofHex( "FFFFFF" ) ),
        GRAY( ofHex( "AAAAAA" ) );

        private static final Random random = new Random();
        private final Color color;

        Template(final Color color) {
            this.color = color;
        }

        public Color get() {
            return color;
        }

        public static Color randomColor() {
            return values()[random.nextInt( values().length )].get();
        }
    }


    private final byte red;
    private final byte green;
    private final byte blue;

    public Color(final byte red, final byte green, final byte blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    /**
     * @return The 1-byte red component of this RGB color.
     */
    public byte getRed() {
        return red;
    }

    /**
     * @return The 1-byte green component of this RGB color.
     */
    public byte getGreen() {
        return green;
    }

    /**
     * @return The 1-byte blue component of this RGB color.
     */
    public byte getBlue() {
        return blue;
    }

    /**
     * @param colorString A color string.
     *
     * @return A color object parsed either as a named color (from {@link Color.Template}) or from a string of 6 hexadecimal digits of the
     * format RRGGBB.
     */
    public static Color of(final String colorString) {
        for (final Template template : Template.values()) {
            if (template.name().equalsIgnoreCase( colorString )) {
                return template.get();
            }
        }

        return ofHex( colorString );
    }

    private static Color ofHex(final String hexColorString) {
        Iterator<String> componentIt = Splitter.fixedLength( 2 ).split( hexColorString ).iterator();
        return new Color( Byte.parseByte( componentIt.next(), 16 ), //
                          Byte.parseByte( componentIt.next(), 16 ), //
                          Byte.parseByte( componentIt.next(), 16 ) );
    }

    @Override
    public int hashCode() {
        return Objects.hash( red, green, blue );
    }

    @Override
    public boolean equals(@Nullable final Object obj) {
        if (obj == this)
            return true;
        if (!(obj instanceof Color))
            return false;

        Color o = (Color) obj;
        return red == o.red && green == o.green && blue == o.blue;
    }
}
