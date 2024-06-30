package com.github.mertakdut;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.github.mertakdut.exception.ReadingException;

//package.opf
public class Package extends BaseFindings {

	private static final Logger log = LoggerFactory.getLogger(Package.class);

	private final Metadata metadata;
	private final Manifest manifest;
	private final Spine spine;
	private final Guide guide;

	private boolean isMetadataFound;
	private boolean isManifestFound;
	private boolean isSpineFound;
	private boolean isGuideFound;

	public Package() {
		metadata = new Metadata();
		manifest = new Manifest();
		spine = new Spine();
		guide = new Guide();
	}

	public static class Metadata {
		// Required Terms
		private String title;
		private String language;
		private String identifier;

		// Optional Terms
		private String creator;
		private String contributor;
		private String publisher;
		private String[] subject;
		private String description;
		private String date;
		private String type;
		private String format;
		private String source;
		private String relation;
		private String coverage;
		private String rights;
		private String coverImageId;

		public String getRights() {
			return rights;
		}

		public String getIdentifier() {
			return identifier;
		}

		public String getContributor() {
			return contributor;
		}

		public String getCreator() {
			return creator;
		}

		public String getTitle() {
			return title;
		}

		public String getLanguage() {
			return language;
		}

		public String[] getSubjects() {
			return subject;
		}

		public String getDescription() {
			return description;
		}

		public String getPublisher() {
			return publisher;
		}

		public String getDate() {
			return date;
		}

		public String getType() {
			return type;
		}

		public String getFormat() {
			return format;
		}

		public String getSource() {
			return source;
		}

		public String getRelation() {
			return relation;
		}

		public String getCoverage() {
			return coverage;
		}

		public String getCoverImageId() {
			return coverImageId;
		}

		void setCoverImageId(String coverImageId) {
			this.coverImageId = coverImageId;
		}

		void fillAttributes(NodeList nodeList) throws ReadingException {
			
			Field[] fields = Package.Metadata.class.getDeclaredFields();

			List<String> subjectList = null;

			for (int i = 0; i < nodeList.getLength(); i++) {
				Node node = nodeList.item(i);

				if (node.getNodeValue() != null && node.getNodeValue().matches("\\s+")) {
					continue;
				}

				String nodeName = node.getNodeName();

				if (nodeName.contains(Character.toString(Constants.COLON))) {
					nodeName = ContextHelper.getTextAfterCharacter(nodeName, Constants.COLON);
				}

				if (nodeName.equals("meta")) {
					if (node.hasAttributes()) {
						NamedNodeMap nodeMap = node.getAttributes();

						boolean isCoverImageNodeFound = false;
						for (int j = 0; j < nodeMap.getLength(); j++) {
							Node attribute = nodeMap.item(j);

							if (!isCoverImageNodeFound && attribute.getNodeName().equals("name") && attribute.getNodeValue().equals("cover")) { // This node states cover-image id.
								isCoverImageNodeFound = true;
								j = -1; // Start the search from the beginng to find 'content' value.
							} else if (isCoverImageNodeFound && attribute.getNodeName().equals("content")) {
								this.coverImageId = attribute.getNodeValue();
								break;
							}

						}
					}
				}

                for (Field field : fields) {

                    if (nodeName.equals(field.getName())) {

                        if (field.getName().equals("subject")) {
                            if (subjectList == null) {
                                subjectList = new ArrayList<>();
                            }
                            subjectList.add(nodeList.item(i).getTextContent());
                        } else {
                            field.setAccessible(true);

                            try {
                                field.set(this, nodeList.item(i).getTextContent());
                                break;
                            } catch (IllegalArgumentException | IllegalAccessException e) {
                                e.printStackTrace();
                                throw new ReadingException("Exception while parsing " + Constants.EXTENSION_OPF + " content: " + e.getMessage());
                            }
                        }
                    }
                }
			}

			if (subjectList != null) {
				Field field;
				try {
					field = Package.Metadata.class.getDeclaredField("subject");
					field.setAccessible(true);
					field.set(this, subjectList.toArray(new String[subjectList.size()]));
				} catch (IllegalArgumentException | IllegalAccessException | NegativeArraySizeException | NoSuchFieldException | SecurityException e) {
					e.printStackTrace();
					throw new ReadingException("Exception while parsing subjects " + Constants.EXTENSION_OPF + " content: " + e.getMessage());
				}
			}
		}

		void print() {
			log.debug("\n\nPrinting Metadata...\n");
			log.debug("title: {}", getTitle());
			log.debug("language: {}", getLanguage());
			log.debug("identifier: {}", getIdentifier());
			log.debug("creator: {}", getCreator());
			log.debug("contributor: {}", getContributor());
			log.debug("publisher: {}", getPublisher());
			log.debug("subject: {}", (Object[]) getSubjects());
			log.debug("description: {}", getDescription());
			log.debug("date: {}", getDate());
			log.debug("type: {}", getType());
			log.debug("format: {}", getFormat());
			log.debug("source: {}", getSource());
			log.debug("relation: {}", getRelation());
			log.debug("coverage: {}", getCoverage());
			log.debug("rights: {}", getRights());
			log.debug("coverImageHref: {}", coverImageId);
		}
	}

	public class Manifest {
		private List<XmlItem> xmlItemList;

		public Manifest() {
			this.xmlItemList = new ArrayList<>();
		}

		void fillXmlItemList(NodeList nodeList) {
			this.xmlItemList = nodeListToXmlItemList(nodeList);
		}

		public List<XmlItem> getXmlItemList() {
			return this.xmlItemList;
		}

		public void print() {
			log.debug("Printing Manifest...");

			for (int i = 0; i < xmlItemList.size(); i++) {
				XmlItem xmlItem = xmlItemList.get(i);

				log.debug("xmlItem({}): value: {} attributes: {}", i, xmlItem.getValue(), xmlItem.getAttributes());
			}
		}
	}

	// <b>Ordered</b> Term of Contents, mostly filled with ids of application/xhtml+xml files in manifest node.
	public class Spine {
		private List<XmlItem> xmlItemList;

		public Spine() {
			this.xmlItemList = new ArrayList<>();
		}

		void fillXmlItemList(NodeList nodeList) {
			this.xmlItemList = nodeListToXmlItemList(nodeList);
		}

		public List<XmlItem> getXmlItemList() {
			return this.xmlItemList;
		}

		public void print() {
			log.debug("Printing Spine...");

			for (int i = 0; i < xmlItemList.size(); i++) {
				XmlItem xmlItem = xmlItemList.get(i);

				log.debug("xmlItem({}): value: {} attributes: {}", i, xmlItem.getValue(), xmlItem.getAttributes());
			}
		}
	}

	public class Guide {
		private List<XmlItem> xmlItemList;

		public Guide() {
			this.xmlItemList = new ArrayList<>();
		}

		void fillXmlItemList(NodeList nodeList) {
			this.xmlItemList = nodeListToXmlItemList(nodeList);
		}

		public List<XmlItem> getXmlItemList() {
			return this.xmlItemList;
		}

		void print() {
			log.debug("Printing Guide...");

			for (int i = 0; i < xmlItemList.size(); i++) {
				XmlItem xmlItem = xmlItemList.get(i);

				log.debug("xmlItem({}): value: {} attributes: {}", i, xmlItem.getValue(), xmlItem.getAttributes());
			}
		}
	}

	@Override
	boolean fillContent(Node node) throws ReadingException {

		String nodeName = node.getNodeName();

		if (nodeName.contains(Character.toString(Constants.COLON))) {
			nodeName = ContextHelper.getTextAfterCharacter(nodeName, Constants.COLON);
		}

		if (nodeName.equals("metadata")) {
			getMetadata().fillAttributes(node.getChildNodes());
			isMetadataFound = true;
		} else if (nodeName.equals("manifest")) {
			getManifest().fillXmlItemList(node.getChildNodes());
			isManifestFound = true;
		} else if (nodeName.equals("spine")) {
			getSpine().fillXmlItemList(node.getChildNodes());
			isSpineFound = true;
		} else if (nodeName.equals("guide")) {
			getGuide().fillXmlItemList(node.getChildNodes());
			isGuideFound = true;
		}

		return isMetadataFound && isManifestFound && isSpineFound && isGuideFound;
	}

	public Metadata getMetadata() {
		return metadata;
	}

	Manifest getManifest() {
		return manifest;
	}

	Spine getSpine() {
		return spine;
	}

	Guide getGuide() {
		return guide;
	}

	void print() {
		getMetadata().print();
		getManifest().print();
		getSpine().print();
		getGuide().print();
	}
}
