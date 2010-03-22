package org.teiid.connector.xml.file;


import java.util.List;

import junit.framework.TestCase;

import org.mockito.Mockito;
import org.teiid.connector.api.Connector;
import org.teiid.connector.api.ConnectorException;
import org.teiid.connector.api.ExecutionContext;
import org.teiid.connector.xmlsource.file.FileConnector;
import org.teiid.connector.xmlsource.file.FileManagedConnectionFactory;

import com.metamatrix.cdk.api.ConnectorHost;

public class TestFileConnectorLong extends TestCase {

	Connector connector;
	ConnectorHost host;
	
	@Override
	public void setUp() throws Exception {
		connector = new FileConnector();
		FileManagedConnectionFactory env = FakeFileManagedConnectionfactory.getDefaultFileProps();
		env.setFileName("purchaseOrders.xml");
		
		String vdbPath = FakeFileManagedConnectionfactory.getDocumentsFolder() + "/purchase_orders.vdb";
		host = new ConnectorHost(connector, env, vdbPath);
		
		ExecutionContext context = Mockito.mock(ExecutionContext.class);
		host.setExecutionContext(context);	
	}
	
	public void testSelect() throws ConnectorException {
		List result = host.executeCommand("SELECT * FROM file_po_list.ITEM");
		assertEquals(5968, result.size());
	}

}