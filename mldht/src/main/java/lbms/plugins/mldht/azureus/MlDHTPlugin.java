package lbms.plugins.mldht.azureus;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.SocketException;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import java.util.logging.Level;
import java.util.logging.Logger;
import lbms.plugins.mldht.kad.DHT;
import lbms.plugins.mldht.kad.DHTConstants;
import lbms.plugins.mldht.kad.DHTLogger;
import lbms.plugins.mldht.kad.DHT.DHTtype;

//import org.eclipse.swt.graphics.Image;
//import org.gudy.azureus2.plugins.PluginException;
import org.gudy.azureus2.plugins.PluginInterface;
//import org.gudy.azureus2.plugins.PluginListener;
//import org.gudy.azureus2.plugins.UnloadablePlugin;
import org.gudy.azureus2.plugins.download.Download;
import org.chris.portmapper.model.PortMapping;
import org.chris.portmapper.model.Protocol;
import org.chris.portmapper.router.RouterException;
import org.milipede.portmapper.PortMapper;
import org.milipede.portmapper.PortMapperService;


//import org.gudy.azureus2.plugins.logging.Logger;
//import org.gudy.azureus2.plugins.logging.LoggerChannel;
//import org.gudy.azureus2.plugins.logging.LoggerChannelListener;
//import org.gudy.azureus2.plugins.ui.UIInstance;
//import org.gudy.azureus2.plugins.ui.UIManager;
//import org.gudy.azureus2.plugins.ui.UIManagerListener;
//import org.gudy.azureus2.plugins.ui.menus.MenuItem;
//import org.gudy.azureus2.plugins.ui.menus.MenuItemListener;
//import org.gudy.azureus2.plugins.ui.model.BasicPluginConfigModel;
//import org.gudy.azureus2.plugins.ui.model.BasicPluginViewModel;
//import org.gudy.azureus2.plugins.ui.tables.TableContextMenuItem;
//import org.gudy.azureus2.plugins.ui.tables.TableManager;
//import org.gudy.azureus2.plugins.ui.tables.TableRow;
//import org.gudy.azureus2.ui.swt.plugins.UISWTInstance;

//import com.aelitis.azureus.plugins.upnp.UPnPPlugin;

/**
 * @author Damokles
 * 
 */
public class MlDHTPlugin { //implements UnloadablePlugin, PluginListener {

	private PluginInterface			pluginInterface;
	private Map<DHTtype,DHT>		dhts;
	private Tracker					tracker;
//	private BasicPluginConfigModel	config_model;

//	private BasicPluginViewModel	view_model;
//	private Logger					logger;
//	private LoggerChannel			logChannel;
//	private LoggerChannelListener	logListener;
//	private UIManagerListener		uiListener;
//	private UISWTInstance			swtInstance	= null;
//	private Image					dhtStatusEntryIcon;

	//private Display					display;

//	private SWTHelper				swtHelper;

//	private Object					mlDHTProvider;
        public org.gudy.azureus2.plugins.dht.mainline.MainlineDHTProvider      mlDHTProvider;
	private static MlDHTPlugin		singleton = new MlDHTPlugin();

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gudy.azureus2.plugins.Plugin#initialize(org.gudy.azureus2.plugins.PluginInterface)
	 */

       
	public void initialize () {
//            final PluginInterface pluginInterface
//        })
//			throws PluginException {
//		if (singleton != null) {
//			throw new IllegalStateException("Plugin already initialized");
//		}
//		singleton = this;

//		this.pluginInterface = pluginInterface;
//		UIManager ui_manager = pluginInterface.getUIManager();
//		config_model = ui_manager.createBasicPluginConfigModel("plugins",
//				"plugin.mldht");

//		config_model.addBooleanParameter2("enable", "mldht.enable", true);
//		config_model.addIntParameter2("port", "mldht.port", 49001);
//		config_model.addBooleanParameter2("autoopen", "mldht.autoopen", true);
//		config_model.addBooleanParameter2("backupOnly", "mldht.backupOnly",
//				false);
//		config_model.addBooleanParameter2("onlyPeerBootstrap",
//				"mldht.onlyPeerBootstrap", false);
//		config_model.addBooleanParameter2("alwaysRestoreID", "mldht.restoreID",
//				true);
//		config_model.addBooleanParameter2("showStatusEntry",
//				"mldht.showStatusEntry", true);
//
////		view_model = ui_manager.createBasicPluginViewModel("Mainline DHT Log");
//
//		view_model.getActivity().setVisible(false);
//		view_model.getProgress().setVisible(false);
//
//		view_model.getStatus().setText("Stopped");
//
////		logger = pluginInterface.getLogger();
//		logChannel = logger.getTimeStampedChannel("Mainline DHT");

//		logListener = new LoggerChannelListener() {
//			public void messageLogged (int type, String content) {
//				view_model.getLogArea().appendText(content + "\n");
//			}
//
//			public void messageLogged (String str, Throwable error) {
//				if (str.length() > 0) {
//					view_model.getLogArea().appendText(str + "\n");
//				}
//
//				StringWriter sw = new StringWriter();
//
//				PrintWriter pw = new PrintWriter(sw);
//
//				error.printStackTrace(pw);
//
//				pw.flush();
//
//				view_model.getLogArea().appendText(sw.toString() + "\n");
//			}
//		};

//		logChannel.addListener(logListener);

//		String version = pluginInterface.getPluginVersion();
//		int parsedVersion = -1;
//		if(version != null)
//		{
//			version = version.replaceAll("[^0-9]", "");
//			if(version.length() > 9)
//				version = version.substring(0, 8);
//			parsedVersion = Integer.parseInt(version);
//		}

		
		
//		DHTConstants.setVersion(parsedVersion);
		dhts = DHT.createDHTs();
		
		DHT.setLogger(new DHTLogger() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see lbms.plugins.mldht.kad.DHTLogger#log(java.lang.String)
			 */
			public void log (String message) {
//				logChannel.log(message);
                            System.out.println(message);
                        }

			/*
			 * (non-Javadoc)
			 * 
			 * @see lbms.plugins.mldht.kad.DHTLogger#log(java.lang.Exception)
			 */
			public void log (Exception e) {
//				logChannel.log(e);
                            e.printStackTrace();
			}
		});

		try {
			mlDHTProvider = new org.gudy.azureus2.plugins.dht.mainline.MainlineDHTProvider() {
				/*
				 * (non-Javadoc)
				 * 
				 * @see org.gudy.azureus2.plugins.dht.mainline.MainlineDHTProvider#getDHTPort()
				 */
				public int getDHTPort () {
//					return pluginInterface.getPluginconfig()
//							.getPluginIntParameter("port");
                                    return 49001;
				}

				/*
				 * (non-Javadoc)
				 * 
				 * @see org.gudy.azureus2.plugins.dht.mainline.MainlineDHTProvider#notifyOfIncomingPort(java.lang.String,
				 *      int)
				 */
				public void notifyOfIncomingPort (String ip_addr, int port) {

                                    System.out.println("incoming port nitification:");
                                    for(DHT dht : dhts.values())
                                    		dht.addDHTNode(ip_addr, port);
				}
			};
//			pluginInterface
//					.getMainlineDHTManager()
//					.setProvider(
//							(org.gudy.azureus2.plugins.dht.mainline.MainlineDHTProvider) mlDHTProvider);
		} catch (Throwable e) {

		}

		tracker = new Tracker(this);

//		uiListener = new UIManagerListener() {
//			/*
//			 * (non-Javadoc)
//			 *
//			 * @see org.gudy.azureus2.plugins.ui.UIManagerListener#UIAttached(org.gudy.azureus2.plugins.ui.UIInstance)
//			 */
//			public void UIAttached (UIInstance instance) {
//				if (swtHelper == null) {
//					swtHelper = new SWTHelper(MlDHTPlugin.this);
//				}
//				swtHelper.UIAttached(instance);
//			}
//
//			/*
//			 * (non-Javadoc)
//			 *
//			 * @see org.gudy.azureus2.plugins.ui.UIManagerListener#UIDetached(org.gudy.azureus2.plugins.ui.UIInstance)
//			 */
//			public void UIDetached (UIInstance instance) {
//				if (swtHelper != null) {
//					swtHelper.UIDetached(instance);
//				}
//
//			}
//		};
//
//		ui_manager.addUIListener(uiListener);
//
//		TableContextMenuItem incompleteMenuItem = ui_manager.getTableManager()
//				.addContextMenuItem(TableManager.TABLE_MYTORRENTS_INCOMPLETE,
//						"tablemenu.main.item");
//		TableContextMenuItem completeMenuItem = ui_manager.getTableManager()
//				.addContextMenuItem(TableManager.TABLE_MYTORRENTS_COMPLETE,
//						"tablemenu.main.item");
//
//		incompleteMenuItem.setStyle(MenuItem.STYLE_MENU);
//		completeMenuItem.setStyle(MenuItem.STYLE_MENU);
//
//		TableContextMenuItem incAnnounceItem = ui_manager.getTableManager()
//				.addContextMenuItem(incompleteMenuItem,
//						"tablemenu.announce.item");
//
//		TableContextMenuItem comAnnounceItem = ui_manager
//				.getTableManager()
//				.addContextMenuItem(completeMenuItem, "tablemenu.announce.item");
//
//		MenuItemListener announceItemListener = new MenuItemListener() {
//			/*
//			 * (non-Javadoc)
//			 *
//			 * @see org.gudy.azureus2.plugins.ui.menus.MenuItemListener#selected(org.gudy.azureus2.plugins.ui.menus.MenuItem,
//			 *      java.lang.Object)
//			 */
//			public void selected (MenuItem menu, Object target) {
//				TableRow row = (TableRow) target;
//				Download dl = (Download) row.getDataSource();
//				tracker.announceDownload(dl);
//			}
//		};
//
//		incAnnounceItem.addListener(announceItemListener);
//		comAnnounceItem.addListener(announceItemListener);
//
//		//must be at the end because on update you get a synchronous callback
//		pluginInterface.addListener(this);
	}

	//-------------------------------------------------------------------

	public static MlDHTPlugin getInstance () {
		return singleton;
	}

        private MlDHTPlugin () {
//            if ( singleton == null) {
//                singleton = new MlDHTPlugin();
//            }
        }

	/**
	 * @return the pluginInterface
	 */
	public PluginInterface getPluginInterface () {
		return pluginInterface;
	}

	/**
	 * @return the dht
	 */
	public DHT getDHT (DHT.DHTtype type) {
		return dhts.get(type);
	}

	public Tracker getTracker () {
		return tracker;
	}

//	/**
//	 * @return the logger
//	 */
//	public Logger getLogger () {
//		return logger;
//	}

//	/**
//	 * Returns the user set status of whether or not the plugin should autoOpen
//	 *
//	 * @return boolean autoOpen
//	 */
//	public boolean isPluginAutoOpen () {
//		return pluginInterface.getPluginconfig().getPluginBooleanParameter(
//				"autoopen", true);
//	}

	private void registerUPnPMapping (int port) {
	PortMapperService mapper = PortMapper.getInstance();
            mapper.loadSettings();
            System.out.println("local host address: " + mapper.getLocalHostAddress());

            try {

                boolean connected = mapper.connectRouter();
                if (connected == true) {
                    System.out.println(mapper.getRouter().getExternalIPAddress());


                PortMapping mapping = new PortMapping(Protocol.UDP, mapper.getRouter().getExternalIPAddress(), 49001, mapper.getLocalHostAddress(), 49001, "torrent");
                mapper.getRouter().addPortMapping(mapping);
                }
            } catch (RouterException ex) {
                ex.printStackTrace();
            }

            try {
//			PluginInterface pi_upnp = pluginInterface.getPluginManager()
//					.getPluginInterfaceByClass(UPnPPlugin.class);
//			if (pi_upnp != null) {
//				((UPnPPlugin) pi_upnp.getPlugin()).addMapping(pluginInterface
//						.getPluginName(), false, port, true);
//			}
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	//-------------------------------------------------------------------

//	/*
//	 * (non-Javadoc)
//	 *
//	 * @see org.gudy.azureus2.plugins.UnloadablePlugin#unload()
//	 */
//	public void unload () throws PluginException {
//		if (swtHelper != null) {
//			swtHelper.onPluginUnload();
//		}
//		if (dhtStatusEntryIcon != null) {
//			dhtStatusEntryIcon.dispose();
//			dhtStatusEntryIcon = null;
//		}
//		if (swtInstance != null) {
//			swtInstance.removeViews(UISWTInstance.VIEW_MAIN, DHTView.VIEWID);
//			swtInstance = null;
//		}
//		stopDHT();
//
//		try {
//			pluginInterface.getMainlineDHTManager().setProvider(null);
//		} catch (Throwable e) {
//		}
//
//		logChannel.removeListener(logListener);
//		pluginInterface.getUIManager().removeUIListener(uiListener);
//		view_model.destroy();
//		config_model.destroy();
//		pluginInterface.removeListener(this);
//	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gudy.azureus2.plugins.PluginListener#closedownComplete()
	 */
	public void closedownComplete () {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gudy.azureus2.plugins.PluginListener#closedownInitiated()
	 */
	public void closedownInitiated () {
		stopDHT();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gudy.azureus2.plugins.PluginListener#initializationComplete()
	 */
	public void initializationComplete () {
//		registerUPnPMapping(pluginInterface.getPluginconfig()
//				.getPluginIntParameter("port"));
//		if (pluginInterface.getPluginconfig().getPluginBooleanParameter(
//				"enable"))
            registerUPnPMapping(49001);
            {
			//start in a separate Thread, since it might block
			//when getPublicAddress is called and the version server is offline
			Thread t = new Thread(new Runnable() {
				public void run () {
					startDHT();
				}
			});
			t.setPriority(Thread.MIN_PRIORITY);
			t.start();
		}

	}

	//-------------------------------------------------------------------
        private boolean success = false;
	public void startDHT () {
//		view_model.getStatus().setText("Initializing");
		try {
//            try {
//                success = new File("C:\\", "dht.cache").createNewFile();
//            } catch (IOException ex) {
//                Logger.getLogger(MlDHTPlugin.class.getName()).log(Level.SEVERE, null, ex);
//            }
//                        if (success = true) {
                            
                        
                        for(Map.Entry<DHTtype, DHT> e : dhts.entrySet())
			{
//				e.getValue().start(new File("C://dht.cache"), 6881, false);
                            e.getValue().start(new File("C:\\", "dht.cache"), 49001, false);
//                                        pluginInterface.getPluginconfig()
//					.getPluginIntParameter("port"), pluginInterface
//					.getPluginconfig().getPluginBooleanParameter(
//							"onlyPeerBootstrap"));
				
				e.getValue().bootstrap();
			}

			tracker.start();
//                        }
//			view_model.getStatus().setText("Running");
		} catch (SocketException e) {
			e.printStackTrace();
		}
                
	}

	public void stopDHT () {
		tracker.stop();
		for(DHT dht : dhts.values())
			dht.stop();
//		view_model.getStatus().setText("Stopped");
	}


}
