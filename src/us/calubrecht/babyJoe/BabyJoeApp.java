package us.calubrecht.babyJoe;

import java.awt.*;

import java.awt.event.*;

import javax.swing.*;

public class BabyJoeApp extends JFrame
{
	private BabyJoeCanvas canvas_;

	public BabyJoeApp()
	{
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e)
			{
				exit();
			}
		});
		setupLayout();
	}

	void setupLayout()
	{
		canvas_ = new BabyJoeCanvas();
		canvas_.setUpperCase(true);
		getContentPane().setLayout(new BorderLayout());
		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension screenSize = tk.getScreenSize();
		Dimension size = new Dimension();
		size.height = (int) (screenSize.height * .9);
		size.width = (int) (screenSize.width * .9);
		canvas_.setPreferredSize(size);
		getContentPane().add(BorderLayout.CENTER, canvas_);

		pack();
		setLocationRelativeTo(null);
		canvas_.requestFocus();
	}

	void exit()
	{
		System.exit(0);
	}

	public static void main(String[] args)
	{
		BabyJoeApp app = new BabyJoeApp();
		// Trying to set full screen mode. Dangerous if we can't
		// catch command-q to exit
		/*
		 * GraphicsDevice device =
		 * GraphicsEnvironment.getLocalGraphicsEnvironment
		 * ().getDefaultScreenDevice(); if (device.isFullScreenSupported()) {
		 * try { device.setFullScreenWindow(this); app.setVisible(true); }
		 * finally { device.setFullScreenWindow(null); } } else {
		 */
		app.setVisible(true);
		// }
	}
}
