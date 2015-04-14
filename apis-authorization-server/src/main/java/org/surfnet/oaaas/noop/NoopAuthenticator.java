/*
 * Copyright 2012 SURFnet bv, The Netherlands
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.surfnet.oaaas.noop;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.surfnet.oaaas.auth.AbstractAuthenticator;
import org.surfnet.oaaas.auth.principal.AuthenticatedPrincipal;

/**
 * A minimalistic implementation of AbstractAuthenticator that contains no authentication but only fulfills the
 * contract of Authenticators.
 * Useful for testing and demonstration purposes only, of course not safe for production.
 */
public class NoopAuthenticator extends AbstractAuthenticator {
  private static final Logger LOG = LoggerFactory.getLogger(NoopAuthenticator.class);

  @Override
  public boolean canCommence(HttpServletRequest request) {
    return getAuthStateValue(request) != null;
  }

  @Override
  public void authenticate(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
      String authStateValue, String returnUri) throws IOException, ServletException {
    LOG.debug("Hitting Noop Authenticator");
    LOG.debug("Get a request: {}", request);
      Enumeration<String> headers = request.getHeaderNames();
      while(headers.hasMoreElements())  {
          String header = headers.nextElement();
          LOG.debug("Header: {}, {}", header, request.getHeader(header));
      }
    LOG.debug("The auth state is: {}", authStateValue);
    super.setAuthStateValue(request, authStateValue);
    AuthenticatedPrincipal principal = getAuthenticatedPrincipal();
    super.setPrincipal(request, principal);
    chain.doFilter(request, response);
  }

  protected AuthenticatedPrincipal getAuthenticatedPrincipal() {
    return new AuthenticatedPrincipal("noop");
  }
}
