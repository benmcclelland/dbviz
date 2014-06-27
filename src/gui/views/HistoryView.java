package gui.views;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.beans.PropertyChangeEvent;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

public class HistoryView extends AbstractView {

	private static final long serialVersionUID = 1L;
	
	public final int DEFAULT_WIDTH = 600;
	public final int DEFAULT_HEIGHT = 200;
	public final int MIN_WIDTH = 1;
	public final int MIN_HEIGHT = 1;

	private JTextArea text;
	private JScrollPane scroll;
	private JProgressBar progress;

	public HistoryView() {

		
		setMinimumSize(new Dimension(MIN_WIDTH, MIN_HEIGHT));

		scroll = new JScrollPane();
		text = new JTextArea();
		text.setLineWrap(true);
		scroll.setViewportView(text);
		text.setEditable(false);
		this.redirectSystemStreams();
		
		progress = new JProgressBar(JProgressBar.HORIZONTAL, 0, 100);
		
		GridBagLayout gridbag = new GridBagLayout();
		setLayout(gridbag);
		GridBagConstraints c = new GridBagConstraints();
		
		c.anchor = GridBagConstraints.PAGE_START;
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		add(scroll, c);
		
		c.gridy = 1;
		c.weighty = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		add(progress, c);
		
		gridbag = null;
		c = null;

	}
	
	public void setDefaultSize() {
		setSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
	}
	
	public void init() {
		progress.setValue(0);
		progress.setStringPainted(true);
	}
	
	public void setProgressText(String s) {
		progress.setStringPainted(true);
		progress.setString(s);
	}
	
	public void addProgress(int n) {
		if (n<=0) {
			return;
		}
		int current = progress.getValue();
		int max = progress.getMaximum();
		if (current >= max) {
			return;
		} else {
			progress.setValue(current+n);
		}
		if (progress.getValue() >= (.95*max)) {
			progress.setValue(max);
		}
	}
	
	public void resetProgress() {
		progress.setValue(0);
		progress.setStringPainted(false);
	}

	@Override
	public void modelPropertyChange(PropertyChangeEvent evt) {
		//nothing from model to keep track of here
	}
	
	/**
	 * Obtained from:
	 * http://unserializableone.blogspot.com/2009/01/redirecting-systemout-and-systemerr-to.html
	 */
	private void updateTextArea(final String s) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				if (s.equals("\n") || s.contains(".java")) {
					text.append(s);
				} else {
					long epoch = System.currentTimeMillis();
					String date = new java.text.SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(new java.util.Date(epoch));
					text.append("["+date+"] "+s);
				}
			}
		});
	}
	
	/**
	 * Obtained from:
	 * http://unserializableone.blogspot.com/2009/01/redirecting-systemout-and-systemerr-to.html
	 */
	private void redirectSystemStreams() {
		OutputStream out = new OutputStream() {
			@Override
			public void write(int b) throws IOException {
				updateTextArea(String.valueOf((char) b));
			}

			@Override
			public void write(byte[] b, int off, int len) throws IOException {
				updateTextArea(new String(b, off, len));
			}

			@Override
			public void write(byte[] b) throws IOException {
				write(b, 0, b.length);
			}
		};

		System.setOut(new PrintStream(out, true));
		System.setErr(new PrintStream(out, true));
	}

}
