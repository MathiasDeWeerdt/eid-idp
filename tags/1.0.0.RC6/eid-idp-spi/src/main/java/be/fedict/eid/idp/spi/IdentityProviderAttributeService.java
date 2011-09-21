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

package be.fedict.eid.idp.spi;

import be.fedict.eid.idp.common.Attribute;

import javax.servlet.ServletContext;
import java.util.Map;

/**
 * eID IdP Service Attribute Interface for derived attributes.
 * 
 * @author Wim Vandenhaute
 */
public interface IdentityProviderAttributeService {

	/**
	 * Initializes this attribute service handler.
	 * 
	 * @param servletContext
	 *            servlet context
	 */
	void init(ServletContext servletContext);

	void addAttribute(Map<String, Attribute> attributeMap);
}
