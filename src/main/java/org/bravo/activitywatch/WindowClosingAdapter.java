/**
 * 
 */
package org.bravo.activitywatch;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import javax.swing.JOptionPane;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

/**
 * @author Volker Braun
 *
 */
@Deprecated
public class WindowClosingAdapter extends WindowAdapter {

	private boolean exitSystem;
	private AWStore store;
	private String storePath;
	
	public WindowClosingAdapter(boolean exitSystem, AWStore store, String storePath) {
		this.exitSystem = exitSystem;
		this.store = store;
		this.storePath = storePath;
	}
	
	public WindowClosingAdapter() {
		this(false, null, null);
	}
	
	public void windowClosing(WindowEvent event) {
		event.getWindow().setVisible(false);
		event.getWindow().dispose();
		if(exitSystem) {
			saveActivities(store);
			System.exit(0);
		}
	}
	
	public void saveActivities(AWStore store)
	{
//		StatusBar.setMessage("Saving activities...");
		JAXBContext context;
		try {
			context = JAXBContext.newInstance(AWStore.class);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			Writer w = null;
			try {
				w = new FileWriter(storePath);
				m.marshal(store, w);
			} catch (IOException e) {
				e.printStackTrace();
				Object[] options = {"Retry", "Ignore"};
				int res = JOptionPane.showOptionDialog(null, e.getLocalizedMessage(),"Error while saving activities",JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE, null, options, options[0]);
				if( res == 0 ) {
					saveActivities(store);
				}
			}
			finally {
				if( w != null ) {
					try {
						w.close();
//						StatusBar.setMessage("Saved "+store.getActivitiyList().size()+" activities.", 2000);
					}
					catch (Exception e){
						e.printStackTrace();
						Object[] options = {"Retry", "Ignore"};
						int res = JOptionPane.showOptionDialog(null, e.getLocalizedMessage(),"Error while saving activities",JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE, null, options, options[0]);
						if( res == 0 ) {
							saveActivities(store);
						}
					}
				}
			}
		} catch (JAXBException e1) {
			e1.printStackTrace();
			Object[] options = {"Retry", "Ignore"};
			int res = JOptionPane.showOptionDialog(null, e1.getLocalizedMessage(),"Error while saving activities",JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE, null, options, options[0]);
			if( res == 0 ) {
				saveActivities(store);
			}
		}
	}
}
