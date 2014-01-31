package com.lyndir.omicron.webapp.resource;

import static com.lyndir.lhunath.opal.system.util.StringUtils.*;

import com.google.inject.Inject;
import com.lyndir.lhunath.opal.system.util.URLUtils;
import com.lyndir.omicron.api.model.Color;
import com.lyndir.omicron.webapp.data.User;
import com.lyndir.omicron.webapp.data.service.EmailAddressUnavailableException;
import com.lyndir.omicron.webapp.data.service.UserDAO;
import edu.umd.cs.findbugs.annotations.*;
import java.util.Collection;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


/**
 * @author lhunath, 2013-10-15
 */
@Path("/user")
public class UserResource {

    private final UserDAO userDAO;

    @Inject
    public UserResource(final UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @GET
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Collection<User> list() {
        return userDAO.listUsers();
    }

    @GET
    @Path("/{emailAddress}")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response get(@PathParam("emailAddress") final String emailAddress) {
        // Check input.
        User user = userDAO.findUser( emailAddress );
        if (user == null)
            return Response.status( Response.Status.NOT_FOUND ).entity( str( "No user with `emailAddress`: {0}", emailAddress ) ).build();

        // Response.
        return Response.ok( user ).build();
    }

    @POST
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED })
    public Response post(final PostObject input)
            throws EmailAddressUnavailableException {
        // Check input.
        if (input.emailAddress == null)
            return Response.serverError().entity( "Missing `emailAddress`." ).build();

        // Handle.
        User user = userDAO.newUser( input.emailAddress, input.name );

        // Response.
        return Response.created( URLUtils.newURI( "%s", user.getEmailAddresses().iterator().next().getAddress() ) ).build();
    }

    @PUT
    @Path("/{emailAddress}")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED })
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response put(@PathParam("emailAddress") final String emailAddress, final PutObject input) {
        // Check input.
        User user = userDAO.findUser( emailAddress );
        if (user == null)
            return Response.status( Response.Status.NOT_FOUND ).entity( str( "No user with `emailAddress`: {0}", emailAddress ) ).build();

        // Handle.
        if (input.name != null)
            user.setName( input.name );
        user.setPrimaryColor( input.primaryColor );
        user.setPrimaryColor( input.secondaryColor );

        // Response.
        return Response.ok( user ).build();
    }

    @SuppressFBWarnings({ "UWF_UNWRITTEN_FIELD" })
    public static class PostObject {

        String emailAddress;
        String name;
    }


    @SuppressFBWarnings({ "UWF_UNWRITTEN_FIELD" })
    public static class PutObject {

        String name;
        Color  primaryColor;
        Color  secondaryColor;
    }
}
