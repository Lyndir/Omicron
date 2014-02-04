package com.lyndir.omicron.webapp.data;

import com.google.gson.annotations.Expose;
import com.lyndir.lhunath.opal.system.i18n.Localized;
import com.lyndir.lhunath.opal.system.i18n.MessagesFactory;
import com.lyndir.lhunath.opal.system.util.MetaObject;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.*;


/**
 * @author lhunath, 2013-10-14
 */
@Entity(name = "OEmailAddress")
public class EmailAddress extends MetaObject implements Localized {

    private static final Messages msgs = MessagesFactory.create( Messages.class );

    @Id
    @Expose
    private final String  address;
    @Nullable
    @ManyToOne
    private       User    user;
    @Expose
    private       boolean validated;

    @Deprecated
    public EmailAddress() {
        address = null;
    }

    public EmailAddress(final String address) {
        this.address = address;
    }

    @Nullable
    public User getUser() {
        return user;
    }

    public void setUser(@Nonnull final User user) {
        this.user = user;
        user.getEmailAddresses().add( this );
    }

    public String getAddress() {
        return address;
    }

    public boolean isValidated() {
        return validated;
    }

    public void setValidated() {
        validated = true;
    }

    @Override
    public String getLocalizedType() {
        return msgs.type();
    }

    @Override
    public String getLocalizedInstance() {
        return msgs.instance( address );
    }

    interface Messages {

        String type();

        String instance(String userName);
    }
}
