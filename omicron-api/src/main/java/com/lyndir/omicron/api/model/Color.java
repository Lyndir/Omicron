package com.lyndir.omicron.api.model;

import com.lyndir.lhunath.opal.system.util.MetaObject;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.Random;
import javax.annotation.Nullable;


/**
 * <i>10 15, 2012</i>
 *
 * @author lhunath
 */
public class Color extends MetaObject implements Serializable {

    private static final Random RANDOM = new Random();

    public static Color random() {
        byte[] colorBytes = new byte[3];
        RANDOM.nextBytes( colorBytes );

        return new Color( colorBytes[0], colorBytes[1], colorBytes[2] );
    }

    public enum Template {

        RED( new Color( 0xFF, 0, 0 ) ),
        GREEN( new Color( 0, 0xFF, 0 ) ),
        BLUE( new Color( 0, 0, 0xFF ) ),
        BLACK( new Color( 0, 0, 0 ) ),
        WHITE( new Color( 0xFF, 0xFF, 0xFF ) ),
        GRAY( new Color( 0xAA, 0xAA, 0xAA ) );

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

    public Color(final int red, final int green, final int blue) {
        this( (byte) red, (byte) green, (byte) blue );
    }

    public Color(final byte red, final byte green, final byte blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    public byte getRed() {
        return red;
    }

    public byte getGreen() {
        return green;
    }

    public byte getBlue() {
        return blue;
    }

    public static Color of(final String colorString) {
        for (final Template template : Template.values())
            if (template.name().equalsIgnoreCase( colorString ))
                return template.get();

        int colorInteger = Integer.parseInt( colorString );
        ByteBuffer colorBuffer = ByteBuffer.allocate( Byte.SIZE * 3 ).putInt( colorInteger );
        colorBuffer.flip();
        byte r = colorBuffer.get();
        byte g = colorBuffer.get();
        byte b = colorBuffer.get();

        return new Color( r, g, b );
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
