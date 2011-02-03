/*
 * eID Identity Provider Project.
 * Copyright (C) 2010 FedICT.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License version
 * 3.0 as published by the Free Software Foundation.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, see 
 * http://www.gnu.org/licenses/.
 */

package be.fedict.eid.idp.sp.protocol.saml2;

import be.fedict.eid.idp.sp.protocol.saml2.spi.AuthenticationResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Abstract authentication response servlet for several SAML v2.0 bindings.
 * <p/>
 * Passes the incoming HTTP Post to the binding specific authentication response
 * processor and puts the returned {@link AuthenticationResponse} on the HTTP
 * Session.
 */
public abstract class AbstractAuthenticationResponseServlet extends HttpServlet {

        private static final long serialVersionUID = 1L;

        private static final Log LOG = LogFactory
                .getLog(AbstractAuthenticationResponseServlet.class);

        public static final String RESPONSE_SESSION_ATTRIBUTE_INIT_PARAM =
                "ResponseSessionAttribute";
        public static final String REDIRECT_PAGE_INIT_PARAM =
                "RedirectPage";

        public static final String ERROR_PAGE_INIT_PARAM = "ErrorPage";
        public static final String ERROR_MESSAGE_SESSION_ATTRIBUTE_INIT_PARAM =
                "ErrorMessageSessionAttribute";

        private String responseSessionAttribute;
        private String redirectPage;
        private String errorPage;
        private String errorMessageSessionAttribute;

        @Override
        public void init(ServletConfig config) throws ServletException {

                this.responseSessionAttribute = getRequiredInitParameter(
                        RESPONSE_SESSION_ATTRIBUTE_INIT_PARAM, config);
                this.redirectPage = getRequiredInitParameter(
                        REDIRECT_PAGE_INIT_PARAM, config);

                this.errorPage = config.getInitParameter(ERROR_PAGE_INIT_PARAM);
                this.errorMessageSessionAttribute = config.getInitParameter(
                        ERROR_MESSAGE_SESSION_ATTRIBUTE_INIT_PARAM);

                initialize(config);
        }

        protected String getRequiredInitParameter(String parameterName,
                                                  ServletConfig config)
                throws ServletException {

                String value = config.getInitParameter(parameterName);
                if (null == value) {
                        throw new ServletException(parameterName
                                + " init-param is required");
                }
                return value;
        }

        @Override
        protected void doGet(HttpServletRequest request,
                             HttpServletResponse response)
                throws ServletException, IOException {

                showErrorPage("SAML2 response handler not available via GET", null,
                        request, response);
        }

        @Override
        protected void doPost(HttpServletRequest request,
                              HttpServletResponse response) throws ServletException, IOException {
                LOG.debug("doPost");

                // clear old session attributes
                HttpSession httpSession = request.getSession();
                clearAllSessionAttribute(httpSession);

                // process response
                AbstractAuthenticationResponseProcessor processor =
                        getAuthenticationResponseProcessor();

                AuthenticationResponse authenticationResponse;
                try {
                        authenticationResponse = processor.process(request);
                } catch (AuthenticationResponseProcessorException e) {
                        showErrorPage(e.getMessage(), e, request, response);
                        return;
                }

                // save response info to session
                httpSession.setAttribute(this.responseSessionAttribute,
                        authenticationResponse);

                // done, redirect
                response.sendRedirect(request.getContextPath() + this.redirectPage);
        }

        private void showErrorPage(String errorMessage, Throwable cause,
                                   HttpServletRequest request, HttpServletResponse response)
                throws IOException, ServletException {

                if (null == cause) {
                        LOG.error("Error: " + errorMessage);
                } else {
                        LOG.error("Error: " + errorMessage, cause);
                }
                if (null != this.errorMessageSessionAttribute) {
                        request.getSession().setAttribute(
                                this.errorMessageSessionAttribute, errorMessage);
                }
                if (null != this.errorPage) {
                        response.sendRedirect(request.getContextPath() + this.errorPage);
                } else {
                        throw new ServletException(errorMessage, cause);
                }
        }

        private void clearAllSessionAttribute(HttpSession httpSession) {

                httpSession.removeAttribute(this.responseSessionAttribute);
        }

        protected abstract void initialize(ServletConfig config)
                throws ServletException;

        protected abstract AbstractAuthenticationResponseProcessor getAuthenticationResponseProcessor()
                throws ServletException;
}
