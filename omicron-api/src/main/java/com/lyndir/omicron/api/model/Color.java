package com.lyndir.omicron.api.model;

import com.google.common.base.Splitter;
import com.lyndir.lhunath.opal.system.util.MetaObject;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.*;
import java.util.regex.Pattern;
import javax.annotation.Nullable;


/**
 * <i>10 15, 2012</i>
 *
 * @author lhunath
 */
public class Color extends MetaObject implements Serializable {

    private static final Random RANDOM = new Random();

    public static Color random() {
        int bound = 0xFF + 1;
        return new Color( RANDOM.nextInt( bound ), RANDOM.nextInt( bound ), RANDOM.nextInt( bound ) );
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


    private final int red;
    private final int green;
    private final int blue;

    public Color(final int red, final int green, final int blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    public int getRed() {
        return red;
    }

    public int getGreen() {
        return green;
    }

    public int getBlue() {
        return blue;
    }

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
        return new Color( Integer.parseInt( componentIt.next(), 16 ), //
                          Integer.parseInt( componentIt.next(), 16 ), //
                          Integer.parseInt( componentIt.next(), 16 ) );
    }

    @Override
    public int hashCode() {
        return Objects.hash( red, green, blue );
    }

    @Override
    public boolean equals(@Nullable final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Color)) {
            return false;
        }

        Color o = (Color) obj;
        return red == o.red && green == o.green && blue == o.blue;
    }
}
