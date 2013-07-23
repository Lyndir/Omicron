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

import static com.lyndir.lhunath.opal.system.util.ObjectUtils.*;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.googlecode.lanterna.input.Key;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.Terminal;
import com.lyndir.lhunath.opal.system.logging.Logger;
import com.lyndir.lhunath.opal.system.util.StringUtils;
import com.lyndir.omicron.cli.CLITheme;
import com.lyndir.omicron.cli.CLIThemes;
import java.util.LinkedList;
import java.util.List;
import javax.annotation.Nonnull;


/**
 * @author lhunath, 2013-07-19
 */
public class View {

    Logger logger = Logger.get( getClass() );

    private final List<View> children = new LinkedList<>();
    private View parent;
    private Rectangle  measuredBoxInParent = new Rectangle();
    private Rectangle  padding             = new Rectangle();
    private Rectangle  margin              = new Rectangle();
    private Terminal.Color backgroundColor;
    private Terminal.Color backgroundPatternColor;
    private String         backgroundPattern;
    private CLITheme       theme;

    public final void measure(final Screen screen) {
        measureInParent( screen );
        measureChildren( screen );
        //                logger.dbg( "measured: %s: %s", getClass().getSimpleName(), getMeasuredBoxOnScreen() );

        for (final View child : getChildren())
            child.measure( screen );
    }

    protected void measureInParent(final Screen screen) {
        if (getParent().isPresent())
            measuredBoxInParent = getParent().get().getMeasuredBoxForChild( this );
        else
            measuredBoxInParent = new Rectangle( 0, screen.getTerminalSize().getColumns(), screen.getTerminalSize().getRows(), 0 );
    }

    protected void measureChildren(final Screen screen) {
    }

    protected Rectangle getMeasuredBoxForChild(final View child) {
        return new Rectangle( 0, getMeasuredBoxInParent().getSize().getWidth(), getMeasuredBoxInParent().getSize().getHeight(), 0 );
    }

    public void addChild(final View child) {
        Preconditions.checkArgument( !child.getParent().isPresent(), "Cannot add child: child is already added to a parent." );

        getChildren().add( child );
        child.setParent( this );
    }

    public final void draw(final Screen screen) {
        drawBackground( screen );
        drawForeground( screen );
        //        drawLayoutDebug( screen );
        drawChildren( screen );
    }

    @SuppressWarnings({ "ForLoopThatDoesntUseLoopVariable", "AssignmentToForLoopParameter", "UnusedDeclaration" })
    private void drawLayoutDebug(final Screen screen) {
        int parents = 0;
        for (View v = this; v.getParent().isPresent(); ++parents)
            v = v.getParent().get();
        int p = 0;
        Rectangle drawBox = getDrawBoxOnScreen();
        for (int y = drawBox.getTop(); y <= drawBox.getBottom(); ++y)
            screen.putString( drawBox.getLeft(), y, StringUtils.repeat( "+", parents ) + p++, //
                              Terminal.Color.WHITE, Terminal.Color.RED );
    }

    protected void drawBackground(final Screen screen) {
        int p = 0;
        Rectangle drawBox = getDrawBoxOnScreen();
        for (int y = drawBox.getTop(); y <= drawBox.getBottom(); ++y)
            for (int x = drawBox.getLeft(); x <= drawBox.getRight(); ++x) {
                int backgroundPatternOffset = p++ % getBackgroundPattern().length();

                screen.putString( x, y, getBackgroundPattern().substring( backgroundPatternOffset, backgroundPatternOffset + 1 ), //
                                  getBackgroundColor(), getBackgroundColor() );
            }
    }

    protected void drawForeground(final Screen screen) {
    }

    protected void drawChildren(final Screen screen) {
        for (final View child : getChildren())
            child.draw( screen );
    }

    @Nonnull
    public Optional<?> layoutValue(final LayoutParameter layoutParameter) {
        return Optional.absent();
    }

    public final boolean handleKey(final Key key) {
        if (onKey( key ))
            return true;

        for (final View child : getChildren())
            if (child.handleKey( key ))
                return true;

        return false;
    }

    /**
     * Handle key input.
     *
     * @param key The key that was read.
     *
     * @return true if the view handled the key (which will consume the key and stop other views from seeing it).
     */
    protected boolean onKey(final Key key) {
        return false;
    }

    @Nonnull
    public Optional<View> getParent() {
        return Optional.fromNullable( parent );
    }

    private void setParent(final View parent) {
        this.parent = parent;
    }

    protected List<View> getChildren() {
        return children;
    }

    public Rectangle getMeasuredBoxInParent() {
        return measuredBoxInParent;
    }

    public final Rectangle getMeasuredBoxOnScreen() {
        if (!getParent().isPresent())
            return measuredBoxInParent;

        Rectangle parentContentBox = getParent().get().getContentBoxOnScreen();
        return measuredBoxInParent.translate( parentContentBox.getOrigin() );
    }

    protected final Rectangle getDrawBoxOnScreen() {
        return getMeasuredBoxOnScreen().shrink( getMargin() );
    }

    protected final Rectangle getContentBoxOnScreen() {
        return getDrawBoxOnScreen().shrink( getPadding() );
    }

    public Rectangle getPadding() {
        return padding;
    }

    public void setPadding(final Rectangle padding) {
        this.padding = padding;
    }

    public Rectangle getMargin() {
        return margin;
    }

    public void setMargin(final Rectangle margin) {
        this.margin = margin;
    }

    public Terminal.Color getBackgroundColor() {
        return ifNotNullElse( backgroundColor, getTheme().bg() );
    }

    public void setBackgroundColor(final Terminal.Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public Terminal.Color getBackgroundPatternColor() {
        return ifNotNullElse( backgroundPatternColor, getTheme().fg() );
    }

    public void setBackgroundPatternColor(final Terminal.Color backgroundPatternColor) {
        this.backgroundPatternColor = backgroundPatternColor;
    }

    public String getBackgroundPattern() {
        return ifNotNullElse( backgroundPattern, getTheme().bgPattern() );
    }

    public void setBackgroundPattern(final String backgroundPattern) {
        this.backgroundPattern = backgroundPattern;
    }

    public CLITheme getTheme() {
        if (theme != null)
            return theme;

        if (getParent().isPresent())
            return getParent().get().getTheme();

        return CLIThemes.DEFAULT;
    }

    public void setTheme(final CLITheme theme) {
        this.theme = theme;
    }
}
