package ch.cyberduck;

/*
 *  Copyright (c) 2005 David Kocher. All rights reserved.
 *  http://cyberduck.ch/
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  Bug fixes, suggestions and comments should be sent to:
 *  dkocher@cyberduck.ch
 */

import ch.cyberduck.core.*;
import ch.cyberduck.core.i18n.Locale;
//import ch.cyberduck.ui.cocoa.application.*;
//import ch.cyberduck.ui.cocoa.foundation.*;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
//import org.rococoa.Foundation;
//import org.rococoa.ID;

/**
 * @version $Id$
 */
public class LoginController extends AbstractLoginController implements ch.cyberduck.service.LoginController {
    private static Logger log = Logger.getLogger(LoginController.class);

    /**
	 * @uml.property  name="parent"
	 * @uml.associationEnd  
	 */
    private WindowController parent;

    public LoginController(final WindowController parent) {
        this.parent = parent;
    }

    public LoginController() {
    }

    public void prompt(final Host host, final String reason, final String message)
            throws LoginCanceledException {

        final Credentials credentials = host.getCredentials();

        SheetController c = new SheetController(parent) {
            @Override
            protected String getBundleName() {
                return "Login";
            }

            @Override
            public void awakeFromNib() {
//                this.update();
                super.awakeFromNib();
            }

            private String titleField;

            public void setTitleField(String titleField) {
                this.titleField = titleField;
//                this.updateField(this.titleField, Locale.localizedString(reason, "Credentials"));
            }

            private String userField;

            public void setUserField(String userField) {
                this.userField = userField;
//                this.updateField(this.userField, credentials.getUsername());
//                if(host.getProtocol().equals(Protocol.S3)) {
//                    this.userField.cell().setPlaceholderString(
//                            Locale.localizedString("Access Key ID", "S3")
//                    );
//                }
//                NSNotificationCenter.defaultCenter().addObserver(this.id(),
//                        Foundation.selector("userFieldTextDidChange:"),
//                        NSControl.NSControlTextDidChangeNotification,
//                        this.userField);
            }

//            public void userFieldTextDidChange(NSNotification notification) {
//                credentials.setUsername(userField.stringValue());
//                this.update();
//            }

            private String textField;

            public void setTextField(String textField) {
                this.textField = textField;
//                this.updateField(this.textField, Locale.localizedString(message, "Credentials"));
            }

            private String passField;

            public void setPassField(String passField) {
                this.passField = passField;
//                this.updateField(this.passField, credentials.getPassword());
//                if(host.getProtocol().equals(Protocol.S3)) {
//                    this.passField.cell().setPlaceholderString(
//                            Locale.localizedString("Secret Access Key", "S3")
//                    );
//                }
//                NSNotificationCenter.defaultCenter().addObserver(this.id(),
//                        Foundation.selector("passFieldTextDidChange:"),
//                        NSControl.NSControlTextDidChangeNotification,
//                        this.passField);
            }

//            public void passFieldTextDidChange(NSNotification notification) {
//                credentials.setPassword(passField.stringValue());
//            }

            private boolean keychainCheckbox;

            public void setKeychainCheckbox(boolean keychainCheckbox) {
                this.keychainCheckbox = keychainCheckbox;
//                this.keychainCheckbox.setEnabled(Preferences.instance().getBoolean("connection.login.useKeychain"));
//                this.keychainCheckbox.setState(Preferences.instance().getBoolean("connection.login.useKeychain")
//                        && Preferences.instance().getBoolean("connection.login.addKeychain") ? NSCell.NSOnState : NSCell.NSOffState);
//                this.keychainCheckbox.setTarget(this.id());
//                this.keychainCheckbox.setAction(Foundation.selector("keychainCheckboxClicked:"));
            }

//            public void keychainCheckboxClicked(final NSButton sender) {
//                final boolean enabled = sender.state() == NSCell.NSOnState;
//                credentials.setUseKeychain(enabled);
//                Preferences.instance().setProperty("connection.login.addKeychain", enabled);
//            }

            private boolean anonymousCheckbox;

            public void setAnonymousCheckbox(boolean anonymousCheckbox) {
                this.anonymousCheckbox = anonymousCheckbox;
//                this.anonymousCheckbox.setTarget(this.id());
//                this.anonymousCheckbox.setAction(Foundation.selector("anonymousCheckboxClicked:"));
            }

//            @Action
//            public void anonymousCheckboxClicked(final NSButton sender) {
//                if(sender.state() == NSCell.NSOnState) {
//                    credentials.setUsername(Preferences.instance().getProperty("connection.login.anon.name"));
//                    credentials.setPassword(Preferences.instance().getProperty("connection.login.anon.pass"));
//                }
//                if(sender.state() == NSCell.NSOffState) {
//                    credentials.setUsername(Preferences.instance().getProperty("connection.login.name"));
//                    credentials.setPassword(null);
//                }
//                this.updateField(this.userField, credentials.getUsername());
//                this.updateField(this.passField, credentials.getPassword());
//                this.update();
//            }


            private String pkLabel;

            public void setPkLabel(String pkLabel) {
                this.pkLabel = pkLabel;
            }

            private boolean pkCheckbox;

            public void setPkCheckbox(boolean pkCheckbox) {
                this.pkCheckbox = pkCheckbox;
//                this.pkCheckbox.setTarget(this.id());
//                this.pkCheckbox.setAction(Foundation.selector("pkCheckboxSelectionChanged:"));
            }

//            private NSOpenPanel publicKeyPanel;
//
//            @Action
//            public void pkCheckboxSelectionChanged(final NSButton sender) {
//                log.debug("pkCheckboxSelectionChanged");
//                if(sender.state() == NSCell.NSOnState) {
//                    publicKeyPanel = NSOpenPanel.openPanel();
//                    publicKeyPanel.setCanChooseDirectories(false);
//                    publicKeyPanel.setCanChooseFiles(true);
//                    publicKeyPanel.setAllowsMultipleSelection(false);
//                    publicKeyPanel.setMessage(Locale.localizedString("Select the private key in PEM format", "Credentials"));
//                    publicKeyPanel.setPrompt(Locale.localizedString("Choose"));
//                    publicKeyPanel.beginSheetForDirectory(LocalFactory.createLocal("~/.ssh").getAbsolute(),
//                            null, this.window(), this.id(),
//                            Foundation.selector("pkSelectionPanelDidEnd:returnCode:contextInfo:"), null);
//                }
//                else {
//                    this.pkSelectionPanelDidEnd_returnCode_contextInfo(publicKeyPanel, NSPanel.NSCancelButton, null);
//                }
//            }

//            public void pkSelectionPanelDidEnd_returnCode_contextInfo(NSOpenPanel sheet, int returncode, ID contextInfo) {
//                log.debug("pkSelectionPanelDidEnd");
//                if(returncode == NSPanel.NSOKButton) {
//                    NSArray selected = sheet.filenames();
//                    final NSEnumerator enumerator = selected.objectEnumerator();
//                    NSObject next;
//                    while((next = enumerator.nextObject()) != null) {
//                        credentials.setIdentity(LocalFactory.createLocal(next.toString()));
//                    }
//                }
//                if(returncode == NSPanel.NSCancelButton) {
//                    credentials.setIdentity(null);
//                }
//                update();
//            }

//            private void update() {
//                this.userField.setEnabled(!credentials.isAnonymousLogin());
//                this.passField.setEnabled(!credentials.isAnonymousLogin());
//                this.keychainCheckbox.setEnabled(!credentials.isAnonymousLogin());
//                this.anonymousCheckbox.setState(credentials.isAnonymousLogin() ? NSCell.NSOnState : NSCell.NSOffState);
//                this.pkCheckbox.setEnabled(host.getProtocol().equals(Protocol.SFTP));
//                if(credentials.isPublicKeyAuthentication()) {
//                    this.pkCheckbox.setState(NSCell.NSOnState);
//                    this.updateField(this.pkLabel, credentials.getIdentity().toURL());
//                    this.pkLabel.setTextColor(NSColor.textColor());
//                }
//                else {
//                    this.pkCheckbox.setState(NSCell.NSOffState);
//                    this.pkLabel.setStringValue(Locale.localizedString("No private key selected"));
//                    this.pkLabel.setTextColor(NSColor.disabledControlTextColor());
//                }
//            }

            @Override
            protected boolean validateInput() {
                return StringUtils.isNotEmpty(credentials.getUsername());
            }

            public void callback(final int returncode) {
                if(returncode == SheetCallback.DEFAULT_OPTION) {
//                    this.window().endEditingFor(null);
                    credentials.setUsername(userField);
                    credentials.setPassword(passField);
                }
            }
        };
        c.beginSheet();

        if(c.returnCode() == SheetCallback.ALTERNATE_OPTION) {
            throw new LoginCanceledException();
        }
    }

    /**
	 * @uml.property  name="description"
	 */
    protected String description;

    /**
	 * @param description
	 * @uml.property  name="description"
	 */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
	 * @return
	 * @uml.property  name="description"
	 */
    public String getDescription() {
        return this.description;
    }

    public void init() {
        System.out.println(toString() + " initialized");
    }

    public void destroy() {
        System.out.println(toString() + " destroyed");
    }

    public String toString() {
        return "LoginController[description=" + description + "]";
    }
}
