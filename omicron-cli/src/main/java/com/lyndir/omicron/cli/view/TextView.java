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

package com.lyndir.omicron.cli.view;

import com.google.common.base.Splitter;
import com.google.common.collect.FluentIterable;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.Terminal;
import com.lyndir.lhunath.opal.system.util.ObjectUtils;
import java.util.Iterator;


/**
 * @author lhunath, 2013-07-21
 */
public class TextView extends View {

    public enum Crop {
        TOP, BOTTOM
    }


    private String text     = "";
    private Crop   textCrop = Crop.BOTTOM;
    private Terminal.Color textColor;

    @Override
    protected void drawForeground(final Screen screen) {
        super.drawForeground( screen );

        FluentIterable<String> lines = FluentIterable.from( Splitter.on( '\n' ).split( getText() ) );

        Rectangle contentBox = getContentBoxOnScreen();
        int from = 0;
        switch (getTextCrop()) {

            case TOP:
                break;
            case BOTTOM:
                from = Math.max( 0, lines.size() - 1 - contentBox.getSize().getHeight() );
                break;
        }

        Iterator<String> linesIt = lines.skip( from ).iterator();
        for (int row = contentBox.getTop(); linesIt.hasNext() && row <= contentBox.getBottom(); ++row)
            screen.putString( contentBox.getLeft(), row, linesIt.next(), getTextColor(), getBackgroundColor() );
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
    }
}
