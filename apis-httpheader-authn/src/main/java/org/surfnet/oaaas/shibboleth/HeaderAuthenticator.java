package org.surfnet.oaaas.shibboleth;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.surfnet.oaaas.auth.AbstractAuthenticator;
import org.surfnet.oaaas.auth.principal.AuthenticatedPrincipal;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;

/**
 * @author Wei Qiu <wei@qiu.es>
 *
 */
public class HeaderAuthenticator extends AbstractAuthenticator {
    private static final Logger LOG = LoggerFactory.getLogger(HeaderAuthenticator.class);
    private static final String RELAY_STATE_FROM_SAML = "RELAY_STATE_FROM_SAML";
    private static final String PRINCIPAL_FROM_SAML = "PRINCIPAL_FROM_SAML";
    private static final String CLIENT_SAML_ENTITY_NAME = "CLIENT_SAML_ENTITY_NAME";
    private static final String SHIB_SESSION_ID = "Shib-Session-ID";
    private static final String PERSISTENT_ID = "eppn";

    @Override
    public boolean canCommence(HttpServletRequest request) {
        //check whether the header contains shibboleth specific information;
        //return isShibRequest(request);
        return getAuthStateValue(request) != null;
    }

    @Override
    public void authenticate(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                             String authStateValue, String returnUri) throws IOException, ServletException {
        LOG.debug("Hitting HTTP Header Authenticator filter");
        LOG.debug("Get a request: {}", request);
        LOG.debug("The authStateValue  is: {}", authStateValue);
        if (isShibRequest(request)) {
            //do the authorization
            Enumeration<String> headers = request.getHeaderNames();
            while(headers.hasMoreElements())  {
                String header = headers.nextElement();
                LOG.debug("Header: {}, {}", header, request.getHeader(header));
            }
            AuthenticatedPrincipal principal = getAuthenticatedPrincipal(request);
            proceedWithChain(request, response, chain, principal, authStateValue);
        } else {
            // the request is not forwarded by mod_shib, something goes wrong
            LOG.debug("Not in a valid shib session");
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

    }

    private AuthenticatedPrincipal getAuthenticatedPrincipal(HttpServletRequest request) {
        String persistentID = request.getHeader(PERSISTENT_ID);
        AuthenticatedPrincipal principal = new AuthenticatedPrincipal(persistentID);
        LOG.debug("The internal principal is: {}", principal);
        return principal;
    }

    private void proceedWithChain(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                  AuthenticatedPrincipal principal, String authStateValue) throws IOException, ServletException {
        super.setPrincipal(request, principal);
        super.setAuthStateValue(request, authStateValue);
        chain.doFilter(request, response);
    }

    protected boolean isShibRequest(HttpServletRequest reqeust) {
        //check weather the Request is forworded by mod_shib of an apache server
        if (null == reqeust.getHeader(SHIB_SESSION_ID)) {
            return false;
        } else {
            return true;
        }
    }


}
