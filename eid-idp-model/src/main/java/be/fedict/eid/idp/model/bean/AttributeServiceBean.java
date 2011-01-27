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

package be.fedict.eid.idp.model.bean;

import be.fedict.eid.idp.entity.*;
import be.fedict.eid.idp.model.AttributeService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Iterator;
import java.util.List;

@Stateless
public class AttributeServiceBean implements AttributeService {

        private static final Log LOG = LogFactory.getLog(AttributeServiceBean.class);

        @PersistenceContext
        private EntityManager entityManager;

        @Override
        public List<AttributeEntity> listAttributes() {
                return AttributeEntity.listAttributes(this.entityManager);
        }

        @Override
        public List<AttributeProtocolUriEntity> listAttributeUris() {
                return AttributeProtocolUriEntity.listAll(this.entityManager);
        }

        @Override
        public AttributeEntity saveAttribute(String uri) {

                AttributeEntity attribute = this.entityManager.find(
                        AttributeEntity.class, uri);
                if (null == attribute) {
                        LOG.debug("Add attribute : " + uri);
                        attribute = new AttributeEntity(uri);
                        this.entityManager.persist(attribute);
                }
                return attribute;
        }

        @Override
        public RPEntity setAttributes(RPEntity rp, List<String> attributes) {

                LOG.debug("set attributes: " + rp.getName());

                RPEntity attachedRp = this.entityManager.find(RPEntity.class, rp.getId());
                List<RPAttributeEntity> oldRpAttributes = attachedRp.getAttributes();

                // add new ones
                for (String attributeName : attributes) {

                        boolean found = false;
                        for (RPAttributeEntity oldRpAttribute : oldRpAttributes) {
                                if (oldRpAttribute.getAttribute().getUri().equals(attributeName)) {
                                        // already in, ok
                                        found = true;
                                        break;
                                }
                        }

                        if (!found) {

                                // new one
                                AttributeEntity attribute = this.entityManager.
                                        find(AttributeEntity.class, attributeName);
                                RPAttributeEntity rpAttribute =
                                        new RPAttributeEntity(attachedRp, attribute);
                                this.entityManager.persist(rpAttribute);
                                attachedRp.getAttributes().add(rpAttribute);
                        }
                }

                // remove old ones
                Iterator<RPAttributeEntity> iter = attachedRp.getAttributes().iterator();
                while (iter.hasNext()) {
                        RPAttributeEntity rpAttribute = iter.next();
                        if (!attributes.contains(rpAttribute.getAttribute().getUri())) {
                                // removed one
                                iter.remove();
                                this.entityManager.remove(rpAttribute);
                        }
                }

                return attachedRp;
        }

        @Override
        public AttributeProtocolUriEntity createAttributeUri(String protocolId,
                                                             String attributeUri,
                                                             String protocolUri) {

                LOG.debug("create attribute URI: protocol=" + protocolId +
                        " attribute=" + attributeUri + " URI=" + protocolUri);

                AttributeEntity attribute = getAttribute(attributeUri);

                AttributeProtocolUriEntity attributeProtocolUri =
                        this.entityManager.find(AttributeProtocolUriEntity.class,
                                new AttributeProtocolUriPK(protocolId, attribute));
                if (null == attributeProtocolUri) {
                        LOG.debug("not yet configured, adding...");
                        attributeProtocolUri = new AttributeProtocolUriEntity(
                                protocolId, attribute, protocolUri);
                        this.entityManager.persist(attributeProtocolUri);
                }

                return attributeProtocolUri;
        }

        @Override
        public String getUri(String protocolId, String attributeUri) {

                LOG.debug("get attribute URI: protocol=" + protocolId +
                        " attribute=" + attributeUri);

                AttributeEntity attribute = getAttribute(attributeUri);

                AttributeProtocolUriEntity attributeProtocolUri =
                        this.entityManager.find(AttributeProtocolUriEntity.class,
                                new AttributeProtocolUriPK(protocolId, attribute));

                if (null != attributeProtocolUri &&
                        null != attributeProtocolUri.getUri() &&
                        !attributeProtocolUri.getUri().isEmpty()) {
                        return attributeProtocolUri.getUri();
                }

                return attributeUri;
        }

        @Override
        public void saveAttributeUris(List<AttributeProtocolUriEntity> attributeUris) {

                LOG.debug("save attribute URIs");
                for (AttributeProtocolUriEntity attributeUri : attributeUris) {

                        AttributeProtocolUriEntity attachedAttributeUri =
                                this.entityManager.find(
                                        AttributeProtocolUriEntity.class,
                                        attributeUri.getPk());
                        if (null == attachedAttributeUri) {
                                throw new RuntimeException("Attribute URI not " +
                                        "found ?! ( attribute=" +
                                        attributeUri.getAttribute().getUri() +
                                        " protocol=" +
                                        attributeUri.getPk().getProtocolId());
                        }
                        attachedAttributeUri.setUri(attributeUri.getUri());
                }
        }

        private AttributeEntity getAttribute(String uri) {

                AttributeEntity attribute = this.entityManager.find(
                        AttributeEntity.class, uri);
                if (null == attribute) {
                        throw new RuntimeException("Attribute \"" + uri +
                                "\" not found!");
                }
                return attribute;
        }
}
