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

package be.fedict.eid.idp.protocol.saml2.artifact;

import be.fedict.eid.idp.spi.IdentityProviderFlow;
import org.opensaml.saml2.metadata.EntityDescriptor;

import javax.servlet.http.HttpServletRequest;

public class SAML2ProtocolServiceIdent extends AbstractSAML2ArtifactProtocolService {

        @Override
        protected IdentityProviderFlow getAuthenticationFlow() {
                return IdentityProviderFlow.IDENTIFICATION;
        }

        @Override
        protected EntityDescriptor getEntityDescriptor(HttpServletRequest request) {

                return new SAML2MetadataHttpServletIdent()
                        .getEntityDescriptor(request, null);
        }
}
