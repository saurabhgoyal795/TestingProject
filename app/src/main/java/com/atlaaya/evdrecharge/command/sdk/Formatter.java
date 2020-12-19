package com.atlaaya.evdrecharge.command.sdk;

/**
 * Class for formatting
 */
public class Formatter {
    /** The format that is being build on */
    private byte[] mFormat;

    byte[] arrayOfByte1 = { 27, 33, 0 };

    public Formatter() {
        // Default:
        mFormat = new byte[]{27, 33, 0};
    }

    /**
     * Method to get the Build result
     *
     * @return the format
     */
    public byte[] get() {
        return mFormat;
    }

    public Formatter bold() {
        // Apply bold:
        mFormat[2] = ((byte) (0x8 | mFormat[2]));
        return this;
    }

    public Formatter boldWithMedium() {
        // Apply bold:
        mFormat[2] = ((byte) (0x20 | arrayOfByte1[2]));
        return this;
    }

    public Formatter boldWithLarge() {
        // Apply bold:
        mFormat[2] = ((byte) (0x10 | arrayOfByte1[2]));
        return this;
    }

    public Formatter small() {
        mFormat[2] = ((byte) (0x1 | mFormat[2]));
        return this;
    }

    public Formatter height() {
        mFormat[2] = ((byte) (0x10 | mFormat[2]));
        return this;
    }

    public Formatter width() {
        mFormat[2] = ((byte) (0x20 | mFormat[2]));
        return this;
    }

    public Formatter underlined() {
        mFormat[2] = ((byte) (0x80 | mFormat[2]));
        return this;
    }

    public static byte[] rightAlign(){
        return new byte[]{0x1B, 'a', 0x02};
    }

    public static byte[] leftAlign(){
        return new byte[]{0x1B, 'a', 0x00};
    }

    public static byte[] centerAlign(){
        return new byte[]{0x1B, 'a', 0x01};
    }

    public static byte[] enableBold(){
        return new byte[]{0x1b, 0x45, 0x01};
    }

    public static byte[] smallSize(){
        return new byte[]{0x1d, 0x21, 0x9};
    }
    public static byte[] mediumSize(){
        return new byte[]{0x1d, 0x21, 0x11};
    }
    public static byte[] largeSize(){
        return new byte[]{0x1d, 0x21, 0x22};
    }

    public static byte[] boldWithMediumBytes(){
        return new byte[]{0x1B,0x21,0x20};
    }

    public static byte[] boldWithLargeBytes(){
        return new byte[]{0x1B,0x21,0x10};
    }
}