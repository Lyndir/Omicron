package com.lyndir.omnicron.api;

import com.lyndir.lhunath.opal.system.util.MetaObject;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.Random;


/**
 * <i>10 15, 2012</i>
 *
 * @author lhunath
 */
public class Color extends MetaObject implements Serializable {

    public enum Template {

        RED( new Color( (byte) 255, (byte) 0, (byte) 0 ) );

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


    private final byte r;
    private final byte g;
    private final byte b;

    public Color(final byte r, final byte g, final byte b) {

        this.r = r;
        this.g = g;
        this.b = b;
    }

    public byte getR() {

        return r;
    }

    public byte getG() {

        return g;
    }

    public byte getB() {

        return b;
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
}
