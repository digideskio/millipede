/**
 * 
 */
package org.chris.portmapper.router.weupnp;

//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;
import org.chris.portmapper.router.AbstractRouter;
import org.chris.portmapper.router.IRouterFactory;
import org.chris.portmapper.router.RouterException;
import org.wetorrent.upnp.GatewayDevice;
import org.wetorrent.upnp.GatewayDiscover;
import org.wetorrent.upnp.WeUPnPException;

/**
 * @author chris
 * @version $Id: WeUPnPRouterFactory.java 61 2009-08-15 14:58:46Z christoph $
 */
public class WeUPnPRouterFactory implements IRouterFactory {
//	private Log logger = LogFactory.getLog(this.getClass());
        private final static Logger logger = Logger.getLogger( WeUPnPRouterFactory.class );

	public AbstractRouter findRouter() throws RouterException {
		GatewayDiscover discover = new GatewayDiscover();
		logger.debug("Searching for gateway devices...");
		GatewayDevice device = null;
		try {
			discover.discover();
			device = discover.getValidGateway();
		} catch (WeUPnPException e) {
			throw new RouterException(
					"Could not discover a valid gateway device: "
							+ e.getMessage(), e);
		}

		return new WeUPnPRouter(device);
	}

}
