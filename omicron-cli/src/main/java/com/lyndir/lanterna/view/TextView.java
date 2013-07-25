/*
 * Copyright 2010, Maarten Billemont
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.lyndir.lanterna.view;

import com.google.common.base.Splitter;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterables;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.Terminal;
import com.lyndir.lhunath.opal.system.util.ObjectUtils;
import java.util.Iterator;


/**
 * @author lhunath, 2013-07-21
 */
public class TextView extends View {

    public enum Crop {
        SHOW_FIRST, SHOW_LAST
    }


    private String text     = "";
    private Crop   textCrop = Crop.SHOW_LAST;
    private int            textOffset;
    private Terminal.Color textColor;

    @Override
    protected void drawForeground(final Screen screen) {
        super.drawForeground( screen );

        FluentIterable<String> lines = FluentIterable.from( getTextLines() );

        Box contentBox = getContentBoxOnScreen();
        int from = getTextOffset();
        switch (getTextCrop()) {

            case SHOW_FIRST:
                break;
            case SHOW_LAST:
                from = Math.max( 0, lines.size() - 1 - contentBox.getSize().getHeight() - getTextOffset() );
                break;
        }

        Iterator<String> linesIt = lines.skip( from ).iterator();
        for (int row = contentBox.getTop(); linesIt.hasNext() && row <= contentBox.getBottom(); ++row)
            screen.putString( contentBox.getLeft(), row, linesIt.next(), getTextColor(), getBackgroundColor() );

        if (getTextOffset() > 0) {
            String offsetText = String.format( "%+d", getTextOffset() );
            screen.putString( contentBox.getRight() - offsetText.length(), contentBox.getTop(), offsetText, //
                              getInfoTextColor(), getInfoBackgroundColor() );
        }
    }

    private Iterable<String> getTextLines() {
        return Splitter.on( '\n' ).split( getText() );
    }

    public Terminal.Color getTextColor() {
        return ObjectUtils.ifNotNullElse( textColor, getTheme().textFg() );
    }

    public void setTextColor(final Terminal.Color textColor) {
        this.textColor = textColor;
    }

    public Crop getTextCrop() {
        return textCrop;
    }

    public void setTextCrop(final Crop textCrop) {
        this.textCrop = textCrop;
    }

    public String getText() {
        return text;
    }

    public void setText(final String text) {
        this.text = text;

        // Update offset in case text lines shrank beyond the current offset.
        textOffset = Math.min( textOffset, Iterables.size( getTextLines() ) );
    }

    public void updateTextOffset(final int offsetDelta) {
        textOffset = Math.min( Math.max( 0, textOffset + offsetDelta ),
                               Iterables.size( getTextLines() ) - 1 - getContentBoxOnScreen().getSize().getHeight() );
    }

    public int getTextOffset() {
        return textOffset;
    }
}
