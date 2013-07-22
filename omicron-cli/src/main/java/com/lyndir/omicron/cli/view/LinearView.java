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

import com.google.common.base.Optional;
import com.googlecode.lanterna.screen.Screen;
import java.util.ArrayList;
import java.util.List;


/**
 * @author lhunath, 2013-07-21
 */
public class LinearView extends View {

    public enum Parameters implements LayoutParameter {
        DESIRED_SPACE
    }


    public enum Orientation {
        HORIZONTAL,
        VERTICAL
    }


    private final List<Integer> measuredChildOffsets = new ArrayList<>();
    private final Orientation orientation;

    public LinearView(final Orientation orientation) {
        this.orientation = orientation;
    }

    @Override
    protected void measureChildren(final Screen screen) {
        super.measureChildren( screen );

        // Calculate the amount of undesired space and the amount of dynamic views to divvy that space up for.
        int totalDesiredSpace = 0, amountDynamicViews = 0;
        for (final View child : getChildren()) {
            Optional<?> desiredSpace = child.layoutValue( Parameters.DESIRED_SPACE );
            if (desiredSpace.isPresent())
                totalDesiredSpace += (Integer) desiredSpace.get();
            else
                ++amountDynamicViews;
        }
        int totalRemainingSpace = getContentBoxOnScreen().getSize().getHeight() - totalDesiredSpace;
//        logger.dbg( "totalDesiredSpace: %s, amountDynamicViews: %s, totalRemainingSpace: %s, contentBox: %s", totalDesiredSpace,
//                    amountDynamicViews, totalRemainingSpace, getContentBoxOnScreen() );

        // Determine the offset for each child depending on its desired or allocated space.
        int offset = 0;
        measuredChildOffsets.clear();
        for (final View child : getChildren()) {
            measuredChildOffsets.add( offset );
//            logger.dbg( "offset for %s: %s", child.getClass().getSimpleName(), offset );

            Optional<?> desiredSpace = child.layoutValue( Parameters.DESIRED_SPACE );
            if (desiredSpace.isPresent())
                offset += (Integer) desiredSpace.get();
            else
                offset += totalRemainingSpace / amountDynamicViews;
        }
    }

    @Override
    protected Rectangle getMeasuredBoxForChild(final View child) {
        int childIndex = getChildren().indexOf( child );
        int childOffset = measuredChildOffsets.get( childIndex );
        int newChildIndex = childIndex + 1;
        switch (orientation) {

            case HORIZONTAL: {
                int newChildOffset = newChildIndex < measuredChildOffsets.size()? measuredChildOffsets.get( newChildIndex )
                        : getContentBoxOnScreen().getSize().getWidth() + 1;

                return new Rectangle( 0, newChildOffset - 1, getContentBoxOnScreen().getSize().getHeight(), childOffset );
            }
            case VERTICAL: {
                int newChildOffset = newChildIndex < measuredChildOffsets.size()? measuredChildOffsets.get( newChildIndex )
                        : getContentBoxOnScreen().getSize().getHeight() + 1;

                return new Rectangle( childOffset, getContentBoxOnScreen().getSize().getWidth(), newChildOffset - 1, 0 );
            }
        }

        throw new UnsupportedOperationException( "Unsupported orientation: " + orientation );
    }

    public Orientation getOrientation() {
        return orientation;
    }
}
