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

import static com.lyndir.lhunath.opal.system.util.ObjectUtils.*;

import com.google.common.base.Preconditions;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.Terminal;
import javax.annotation.Nonnull;


/**
 * A view that renders a title and maximizes a single subview below it.
 *
 * @author lhunath, 2013-07-20
 */
public class TitledView extends View {

    private String         title;
    private Terminal.Color backgroundColor;
    private Terminal.Color textColor;
    private Inset textPadding = new Inset( 0, 2, 0, 2 );

    public TitledView(final String title) {
        this.title = title;
    }

    @Override
    public void addChild(final View child) {
        Preconditions.checkState( getChildren().isEmpty(), "Can only add a single child to a titled view." );

        super.addChild( child );
    }

    @Override
    protected Box measuredBoxForChildInView(final View child) {
        return super.measuredBoxForChildInView( child ).shrink( new Inset( 1, 0, 0, 0 ) );
    }

    @Override
    protected void drawForeground(final Screen screen) {
        super.drawForeground( screen );

        Box contentBox = getContentBoxOnScreen().shrink( getTextPadding() );
        String titleOnScreen = getTitle().substring( 0, Math.min( getTitle().length(), contentBox.getSize().getWidth() ) );
        screen.putString( contentBox.getLeft(), contentBox.getTop(), titleOnScreen, getTextColor(), getBackgroundColor() );
    }

    @Nonnull
    public String getTitle() {
        return title;
    }

    public void setTitle(@Nonnull final String title) {
        this.title = title;
    }

    public Inset getTextPadding() {
        return textPadding;
    }

    public void setTextPadding(final Inset textPadding) {
        this.textPadding = textPadding;
    }

    @Override
    public Terminal.Color getBackgroundColor() {
        return ifNotNullElse( backgroundColor, getTheme().barBg() );
    }

    @Override
    public void setBackgroundColor(final Terminal.Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public Terminal.Color getTextColor() {
        return ifNotNullElse( textColor, getTheme().barFg() );
    }

    public void setTextColor(final Terminal.Color textColor) {
        this.textColor = textColor;
    }
}
