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

package be.fedict.eid.idp.admin.webapp.bean;

import be.fedict.eid.idp.admin.webapp.Authenticator;
import be.fedict.eid.idp.model.admin.AdminManager;
import org.apache.commons.logging.LogFactory;
import org.jboss.ejb3.annotation.LocalBinding;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.security.Credentials;
import org.jboss.seam.security.Identity;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.security.cert.X509Certificate;

@Stateless
@Name("idpAuthenticator")
@LocalBinding(jndiBinding = "fedict/eid/idp/admin/webapp/AuthenticatorBean")
public class AuthenticatorBean implements Authenticator {

    private static final org.apache.commons.logging.Log LOG = LogFactory.getLog(AuthenticatorBean.class);

    @In
    private Credentials credentials;

    @In
    private Identity identity;

    @In(value = "eid.certs.authn", scope = ScopeType.SESSION)
    private X509Certificate authenticatedCertificate;

    @EJB
    private AdminManager adminManager;

    @Override
    public boolean authenticate() {

        LOG.debug("authenticate: ");
        if (this.adminManager.isAdmin(this.authenticatedCertificate)) {
            this.identity.addRole("admin");
        }
        return true;
    }
}