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
import com.google.common.base.Splitter;
import com.googlecode.lanterna.input.Key;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.Terminal;
import com.lyndir.omicron.cli.OmicronCLI;
import com.lyndir.omicron.cli.command.RootCommand;
import java.util.Deque;
import java.util.LinkedList;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;


/**
 * @author lhunath, 2013-07-21
 */
public class InputView extends View {

    private static final Splitter commandSplitter = Splitter.on( Pattern.compile( "\\s+" ) ).omitEmptyStrings().trimResults();

    private final Deque<String> inputHistory = new LinkedList<>();
    private final Deque<String> inputFuture  = new LinkedList<>();
    private final StringBuilder inputText    = new StringBuilder();
    private       Rectangle     textPadding  = new Rectangle( 0, 2, 0, 2 );
    private       String        promptText   = "> ";
    private Terminal.Color textColor;
    private Terminal.Color promptTextColor;
    private Terminal.Color backgroundColor;
    private TextView       controlTextView;

    @Override
    protected void drawForeground(final Screen screen) {
        super.drawForeground( screen );

        Rectangle contentBox = getContentBoxOnScreen().shrink( getTextPadding() );
        //        logger.dbg( "contentBox: %s, screen: %sx%s", contentBox, screen.getTerminalSize().getColumns(), screen.getTerminalSize().getRows() );
        int inputOnScreenLength = Math.min( getInputText().length(), contentBox.getSize().getWidth() - getPromptText().length() );
        String inputOnScreen = getInputText().substring( getInputText().length() - inputOnScreenLength, getInputText().length() );
        screen.putString( contentBox.getLeft(), contentBox.getTop(), getPromptText(), getPromptTextColor(), getBackgroundColor() );
        screen.putString( contentBox.getLeft() + getPromptText().length(), contentBox.getTop(), inputOnScreen, getTextColor(),
                          getBackgroundColor() );
        screen.setCursorPosition( contentBox.getLeft() + getPromptText().length() + inputOnScreenLength, contentBox.getTop() );
    }

    @Nonnull
    @Override
    public Optional<?> layoutValue(final LayoutParameter layoutParameter) {
        if (layoutParameter == LinearView.Parameters.DESIRED_SPACE)
            return Optional.of( 1 );

        return super.layoutValue( layoutParameter );
    }

    @Override
    protected boolean onKey(final Key key) {
        // BACKSPACE: Delete last character
        if (key.getKind() == Key.Kind.Backspace) {
            if (inputText.length() > 0)
                inputText.deleteCharAt( inputText.length() - 1 );
        }

        // ESC: Erase input.
        else if (key.getKind() == Key.Kind.Escape)
            clearInputText();

        // ENTER: Execute input.
        else if (key.getKind() == Key.Kind.Enter) {
            OmicronCLI.get().getLog().add( getPromptText() + inputText );
            new RootCommand( OmicronCLI.get() ).evaluate( commandSplitter.split( inputText ).iterator() );

            inputHistory.push( inputText.toString() );
            clearInputText();
        }

        // ARROWS: ALT = Scroll Control View, NONE = History navigation.
        else if (key.getKind() == Key.Kind.ArrowUp) {
            if (key.isAltPressed() && getControlTextView() != null)
                getControlTextView().updateTextOffset( -1 );
            else {
                if (!inputHistory.isEmpty()) {
                    if (inputText.length() > 0)
                        inputFuture.push( inputText.toString() );
                    clearInputText();
                    inputText.append( inputHistory.pop() );
                }
            }
        } else if (key.getKind() == Key.Kind.ArrowDown) {
            if (key.isAltPressed() && getControlTextView() != null)
                getControlTextView().updateTextOffset( 1 );
            else {
                if (!inputFuture.isEmpty()) {
                    if (inputText.length() > 0)
                        inputHistory.push( inputText.toString() );
                    clearInputText();
                    inputText.append( inputFuture.pop() );
                }
            }
        }

        // OTHERS: Add character to input.
        else
            inputText.append( key.getCharacter() );

        return true;
    }

    private StringBuilder clearInputText() {
        return inputText.delete( 0, inputText.length() );
    }

    public String getPromptText() {
        return promptText;
    }

    public void setPromptText(final String promptText) {
        this.promptText = promptText;
    }

    public Terminal.Color getPromptTextColor() {
        return ifNotNullElse( promptTextColor, getTheme().promptFg() );
    }

    public void setPromptTextColor(final Terminal.Color promptTextColor) {
        this.promptTextColor = promptTextColor;
    }

    public Rectangle getTextPadding() {
        return textPadding;
    }

    public void setTextPadding(final Rectangle textPadding) {
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
        return ifNotNullElse( textColor, getTheme().textFg() );
    }

    public void setTextColor(final Terminal.Color textColor) {
        this.textColor = textColor;
    }

    public TextView getControlTextView() {
        return controlTextView;
    }

    public void setControlTextView(final TextView controlTextView) {
        this.controlTextView = controlTextView;
    }

    @Nonnull
    public StringBuilder getInputText() {
        return inputText;
    }
}
