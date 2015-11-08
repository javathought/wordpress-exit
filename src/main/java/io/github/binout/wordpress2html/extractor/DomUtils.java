/*
 * Copyright 2014 Benoît Prioux
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.binout.wordpress2html.extractor;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.XMLConstants;
import javax.xml.xpath.*;
import java.io.Reader;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

class DomUtils {

    static Stream<Node> streamOf(NodeList articles) {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(new NodeListIterator(articles), Spliterator.DISTINCT | Spliterator.IMMUTABLE | Spliterator.NONNULL), false);
    }

    static String findChildTextContent(Node n, String nodeName) {
        return streamOf(n.getChildNodes()).filter(m -> m.getNodeName().equals(nodeName)).map(Node::getTextContent).findFirst().orElse("");
    }

    // <category domain="post_tag" nicename="netbeans"><![CDATA[Netbeans]]></category>
    static Stream<String> findChildTextContentListOnAttribute(Node n, String nodeName, String attribute, String attributeValue) {
        return streamOf(n.getChildNodes()).filter(m -> {
            return (m.getNodeName().equals(nodeName)
                    && m.getAttributes().getNamedItem(attribute).getTextContent().equals(attributeValue)
                    // && m.getAttributes().getNamedItem(attribute).getNodeName().equals(attributeValue)
            );
        }).map(Node::getTextContent);
    }


    static NodeList getNodeList(Reader reader, String expression) throws XPathFactoryConfigurationException, XPathExpressionException {
        InputSource src = new InputSource(reader);
        XPathFactory xPathFactory = XPathFactory.newInstance();
        xPathFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, false);
        XPath xPath = xPathFactory.newXPath();
        return (NodeList) xPath.evaluate(expression, src, XPathConstants.NODESET);
    }
}
