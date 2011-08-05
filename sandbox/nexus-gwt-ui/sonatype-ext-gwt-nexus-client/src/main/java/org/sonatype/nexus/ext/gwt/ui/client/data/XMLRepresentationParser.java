/**
 * Copyright (c) 2008-2011 Sonatype, Inc.
 * All rights reserved. Includes the third-party code listed at http://www.sonatype.com/products/nexus/attributions.
 *
 * This program is free software: you can redistribute it and/or modify it only under the terms of the GNU Affero General
 * Public License Version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License Version 3
 * for more details.
 *
 * You should have received a copy of the GNU Affero General Public License Version 3 along with this program.  If not, see
 * http://www.gnu.org/licenses.
 *
 * Sonatype Nexus (TM) Open Source Version is available from Sonatype, Inc. Sonatype and Sonatype Nexus are trademarks of
 * Sonatype, Inc. Apache Maven is a trademark of the Apache Foundation. M2Eclipse is a trademark of the Eclipse Foundation.
 * All other trademarks are the property of their respective owners.
 */
package org.sonatype.nexus.ext.gwt.ui.client.data;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;

public class XMLRepresentationParser implements RepresentationParser {
    
    public Entity parseEntity(String representation, EntityFactory factory) {
        return parseEntity(representation, factory.create());
    }
    
    public Entity parseEntity(String representation, Entity entity) {
        Document doc = XMLParser.parse(representation);
        return parse(doc.getElementsByTagName("data").item(0), entity);
    }
    
    public List<Entity> parseEntityList(String representation, EntityFactory factory) {
        List<Entity> result = new ArrayList<Entity>();
        
        Document doc = XMLParser.parse(representation);
        
        NodeList nodes = doc.getElementsByTagName(factory.create().getType());
        
        for (int i = 0; i < nodes.getLength(); i++) {
            result.add(parse(nodes.item(i), factory.create()));
        }
        
        return result;
    }

    private Entity parse(Node node, Entity entity) {
        NodeList nodes = node.getChildNodes();
        
        for (int i = 0; i < nodes.getLength(); i++) {
            Node n = nodes.item(i);
            String nodeName = n.getNodeName();
            
            if (n.getNodeType() == Node.ELEMENT_NODE) {
                Class fieldType = entity.getFieldType(nodeName);
                Object value = null;
                
                //TODO: handle all primitive type and their wrappers
                if (String.class.equals(fieldType)) {
                    value = n.getFirstChild().getNodeValue();
                } else if (Integer.class.equals(fieldType) || int.class.equals(fieldType)) {
                    value = Integer.valueOf(n.getFirstChild().getNodeValue());
                } else if (Boolean.class.equals(fieldType) || boolean.class.equals(fieldType)) {
                    value = Boolean.valueOf(n.getFirstChild().getNodeValue());
                } else if (fieldType != null) {
                    Entity childEntity = entity.createEntity(nodeName);
                    if (childEntity != null) {
                        value = parse(n, childEntity);
                    }
                }
                
                if (value != null) {
                    entity.set(nodeName, value);
                }
            }
        }
        
        if (entity instanceof Initializable) {
            ((Initializable) entity).initialize();
        }
        
        return entity;
    }
    
    public String serializeEntity(String root, Entity entity) {
        StringBuilder sb = new StringBuilder();
        
        sb.append("<" + root + ">");
        sb.append("<data>");
        
        serialize(entity, sb);
        
        sb.append("</data>");
        sb.append("</" + root + ">");
        
        return sb.toString();
    }
    
    public void serialize(Entity entity, StringBuilder sb) {
        //TODO: don't serialize properties not belong to the resource
        for (String propertyName : entity.getPropertyNames()) {
            Object value = entity.get(propertyName);
            if (value != null) {
                sb.append("<" + propertyName + ">");
                if (value instanceof Entity) {
                    serialize((Entity) value, sb);
                } else {
                    sb.append(value.toString());
                }
                sb.append("</" + propertyName + ">");
            }
        }
    }
    
}
