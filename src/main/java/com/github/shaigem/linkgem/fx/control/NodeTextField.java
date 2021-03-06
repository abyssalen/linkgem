/**
 * Copyright (c) 2013, 2016 ControlsFX
 * All rights reserved.
 * <p>
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * * Neither the name of ControlsFX, any associated website, nor the
 * names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 * <p>
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL CONTROLSFX BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.github.shaigem.linkgem.fx.control;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.Skin;
import javafx.scene.control.TextField;

/**
 * Textfield which allows nodes to be placed on the left and right part of the textfield.
 * Taken from ControlsFX. There was a bug in Skin of the control. When users set the left or right side of the nodes to null,
 * the left or right side of the nodes do not disappear correctly. The bug has been fixed in NodeTextFieldSkin.
 */
public class NodeTextField extends TextField {


    /**************************************************************************
     *
     * Private fields
     *
     **************************************************************************/


    /**************************************************************************
     *
     * Constructors
     *
     **************************************************************************/

    /**
     * Instantiates a default CustomTextField.
     */
    public NodeTextField() {
        getStyleClass().add("custom-text-field"); //$NON-NLS-1$
    }


    /**************************************************************************
     *
     * Properties
     *
     **************************************************************************/

    // --- left
    private ObjectProperty<Node> left = new SimpleObjectProperty<>(this, "left"); //$NON-NLS-1$

    /**
     * @return An ObjectProperty wrapping the {@link Node} that is placed
     * on the left ofthe text field.
     */
    public final ObjectProperty<Node> leftProperty() {
        return left;
    }

    /**
     * @return the {@link Node} that is placed on the left of
     * the text field.
     */
    public final Node getLeft() {
        return left.get();
    }

    /**
     * Sets the {@link Node} that is placed on the left of
     * the text field.
     *
     * @param value
     */
    public final void setLeft(Node value) {
        left.set(value);
    }


    // --- right
    private ObjectProperty<Node> right = new SimpleObjectProperty<>(this, "right"); //$NON-NLS-1$

    /**
     * Property representing the {@link Node} that is placed on the right of
     * the text field.
     *
     * @return An ObjectProperty.
     */
    public final ObjectProperty<Node> rightProperty() {
        return right;
    }

    /**
     * @return The {@link Node} that is placed on the right of
     * the text field.
     */
    public final Node getRight() {
        return right.get();
    }

    /**
     * Sets the {@link Node} that is placed on the right of
     * the text field.
     *
     * @param value
     */
    public final void setRight(Node value) {
        right.set(value);
    }


    /**************************************************************************
     *
     * Public API
     *
     **************************************************************************/

    /**
     * {@inheritDoc}
     */
    @Override
    protected Skin<?> createDefaultSkin() {
        return new NodeTextFieldSkin(this) {
            @Override
            public ObjectProperty<Node> leftProperty() {
                return NodeTextField.this.leftProperty();
            }

            @Override
            public ObjectProperty<Node> rightProperty() {
                return NodeTextField.this.rightProperty();
            }
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUserAgentStylesheet() {
        return org.controlsfx.control.textfield.CustomTextField.class.getResource("customtextfield.css").toExternalForm(); //$NON-NLS-1$
    }

}
