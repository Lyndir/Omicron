/*
 *   Copyright 2009, Maarten Billemont
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
package com.lyndir.omicron.webapp.data;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.gson.annotations.Expose;
import com.lyndir.lhunath.opal.system.i18n.Localized;
import com.lyndir.lhunath.opal.system.i18n.MessagesFactory;
import com.lyndir.lhunath.opal.system.util.MetaObject;
import com.lyndir.omicron.api.model.Color;
import com.lyndir.omicron.api.model.PlayerKey;
import java.io.Serializable;
import java.util.List;
import javax.annotation.Nonnull;
import javax.persistence.*;


/**
 * @author lhunath
 */
@Entity(name = "OUser")
public class User extends MetaObject implements Localized {

    private static final Messages msgs = MessagesFactory.create( Messages.class );

    @Id
    @GeneratedValue
    private final long               id             = 0;
    @Expose
    @OneToMany(mappedBy = "user")
    private final List<EmailAddress> emailAddresses = Lists.newLinkedList();
    @Nonnull
    private final PlayerKey          playerKey      = new PlayerKey();

    @Nonnull
    private String name;
    @Nonnull
    private Color  color;

    @Deprecated
    public User() {
    }

    public User(@Nonnull final EmailAddress emailAddress, @Nonnull final String name) {
        emailAddress.setUser( this );
        this.name = name;
        color = Color.random();
    }

    public long getId() {
        return id;
    }

    @Nonnull
    public List<EmailAddress> getEmailAddresses() {
        return emailAddresses;
    }

    @Nonnull
    public PlayerKey getPlayerKey() {
        return playerKey;
    }

    @Nonnull
    public String getName() {
        return name;
    }

    public void setName(@Nonnull final String name) {
        this.name = name;
    }

    @Nonnull
    public Color getColor() {
        return color;
    }

    public void setColor(@Nonnull final Color color) {
        this.color = color;
    }

    @Override
    public String getLocalizedType() {
        return msgs.type();
    }

    @Override
    public String getLocalizedInstance() {
        return msgs.instance( name );
    }

    interface Messages {

        String type();

        String instance(Serializable userName);
    }
}
