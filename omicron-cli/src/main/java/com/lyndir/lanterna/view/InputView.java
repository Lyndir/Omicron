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

import com.google.common.base.Optional;
import com.googlecode.lanterna.input.Key;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.Terminal;
import com.lyndir.omicron.cli.OmicronCLI;
import com.lyndir.omicron.cli.command.RootCommand;
import java.util.Deque;
import java.util.LinkedList;
import javax.annotation.Nonnull;


/**
 * @author lhunath, 2013-07-21
 */
public abstract class InputView extends View {

    private final Deque<String> inputHistory = new LinkedList<>();
    private final Deque<String> inputFuture  = new LinkedList<>();
    private final StringBuilder inputText    = new StringBuilder();
    private       Inset         textPadding  = new Inset( 0, 2, 0, 2 );
    private       String        promptText   = "> ";
    private Terminal.Color textColor;
    private Terminal.Color promptTextColor;
    private Terminal.Color backgroundColor;
    private TextView       controlTextView;

    @Override
    protected void drawForeground(final Screen screen) {
        super.drawForeground( screen );

        Box contentBox = getContentBoxOnScreen().shrink( getTextPadding() );
        int inputOnScreenLength = Math.min( getInputText().length(), contentBox.getSize().getWidth() - getPromptText().length() );
        String inputOnScreen = getInputText().substring( getInputText().length() - inputOnScreenLength, getInputText().length() );
        screen.putString( contentBox.getLeft(), contentBox.getTop(), getPromptText(), //
                          getPromptTextColor(), getBackgroundColor() );
        screen.putString( contentBox.getLeft() + getPromptText().length(), contentBox.getTop(), inputOnScreen, //
                          getTextColor(), getBackgroundColor() );
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
            if (getInputText().length() > 0)
                getInputText().deleteCharAt( getInputText().length() - 1 );
        }

        // ESC: Erase input.
        else if (key.getKind() == Key.Kind.Escape)
            clearInputText();

            // ENTER: Execute input.
        else if (key.getKind() == Key.Kind.Enter) {
            onEnterText( getInputText().toString() );
            getInputHistory().push( getInputText().toString() );
            clearInputText();
        }

        // UP/DOWN: History navigation, ALT UP/DOWN: Scroll Control View
        else if (key.getKind() == Key.Kind.ArrowUp) {
            if (key.isAltPressed() && getControlTextView() != null)
                getControlTextView().updateTextOffset( 1 );
            else {
                if (getInputText().length() > 0)
                    getInputHistory().push( getInputText().toString() );
                clearInputText();
                if (!getInputFuture().isEmpty()) {
                    getInputText().append( getInputFuture().pop() );
                }
            }
        } else if (key.getKind() == Key.Kind.ArrowDown) {
            if (key.isAltPressed() && getControlTextView() != null)
                getControlTextView().updateTextOffset( -1 );
            else {
                if (!getInputHistory().isEmpty()) {
                    if (getInputText().length() > 0)
                        getInputFuture().push( getInputText().toString() );
                    clearInputText();
                    getInputText().append( getInputHistory().pop() );
                }
            }
        }

        // LEFT/RIGHT: Cursor character navigation, ALT LEFT/RIGHT: Cursor word navigation.
        else if (key.getKind() == Key.Kind.ArrowLeft) {
            // TODO
        } else if (key.getKind() == Key.Kind.ArrowRight) {
            // TODO
        }

        // OTHERS: Add character to input.
        else
            getInputText().append( key.getCharacter() );

        return true;
    }

    protected abstract void onEnterText(final String text);

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

    public Deque<String> getInputHistory() {
        return inputHistory;
    }

    public Deque<String> getInputFuture() {
        return inputFuture;
    }
}
