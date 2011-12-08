/**
 * @author Antti Relander / Eficode
 * @verison 0.1
 * @since 2011-12-07
 */

package org.jvnet.hudson.plugins;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

//XML parsing
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.kohsuke.stapler.export.Exported;
import org.xml.sax.SAXException;

import hudson.model.AbstractBuild;
import hudson.model.User;
import hudson.scm.ChangeLogParser;
import hudson.scm.ChangeLogSet;
import hudson.scm.ChangeLogSet.Entry;
import org.jvnet.hudson.plugins.VaultSCMChangeLogSet;
import org.jvnet.hudson.plugins.VaultSCMChangeLogSet.VaultSCMChangeLogSetEntry;
import hudson.scm.EditType;

public class VaultSCMChangeLogParser extends ChangeLogParser {

	@Override
	public ChangeLogSet<? extends Entry> parse(AbstractBuild build,
			File changelogFile) throws IOException, SAXException {
		
		 String userName = null;
		 String date = null;
		 String comment = null;
		 String version = null;
		//open the changelog File
		VaultSCMChangeLogSet cls = new VaultSCMChangeLogSet(build);		
		 try {
			  
			  DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			  DocumentBuilder db = dbf.newDocumentBuilder();
			  Document doc = db.parse(changelogFile);
			  doc.getDocumentElement().normalize();
			  NodeList nodeLst = doc.getElementsByTagName("item");
			  
			  for (int s = 0; s < nodeLst.getLength(); s++) {
			  
			  Element mostRecentChange = (Element)nodeLst.item(s);
			  userName = mostRecentChange.getAttribute("user");
			  date = mostRecentChange.getAttribute("date");
			  comment = mostRecentChange.getAttribute("comment");
			  version = mostRecentChange.getAttribute("version");
			  

				
				VaultSCMChangeLogSetEntry next = new VaultSCMChangeLogSetEntry(comment,version,date,cls,userName);
				if (!cls.addEntry(next)) //terminate on error
					break;
		 	}
			  
		  } catch (Exception e) {
			    e.printStackTrace();
			  }	
			  
		
		return cls;
	}

	

}

