/**
 * Container classes for game data reflecting the complete game package of a set
 * of areas and a game scenario of worlds and levels.
 */
@XmlSchema(
	namespace = "http://github.com/jimbovm/isobel",
	xmlns = {
		@XmlNs(prefix = "is", namespaceURI = "http://github.com/jimbovm/isobel")
	},
	elementFormDefault = XmlNsForm.QUALIFIED)
package com.github.jimbovm.isobel.common;

import jakarta.xml.bind.annotation.XmlNs;
import jakarta.xml.bind.annotation.XmlNsForm;
import jakarta.xml.bind.annotation.XmlSchema;
