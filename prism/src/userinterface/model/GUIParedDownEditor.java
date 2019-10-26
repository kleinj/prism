package userinterface.model;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.MetalTheme;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.PlainDocument;

import userinterface.util.PresentationMetalTheme;

@SuppressWarnings("serial")
public class GUIParedDownEditor extends JPanel
{
	private JEditorPane editor;
	private JScrollPane editorScrollPane;

	//CONSTANTS
	//defaults
	/** The default width of the main window */
	public static final int DEFAULT_WINDOW_WIDTH = 1024;
	/** The default height of the main window */
	public static final int DEFAULT_WINDOW_HEIGHT = 640;
	/** The minimum width of the main window */
	public static final int MINIMUM_WINDOW_WIDTH = 10;
	/** The minimum height of the main window. */
	public static final int MINIMUM_WINDOW_HEIGHT = 10;

	GUIParedDownEditor(boolean usePlain)
	{
		setLayout(new BorderLayout());

		editor = new JEditorPane();

		if (!usePlain) {
			editor.setEditorKitForContentType("text/prism", new ParedPrismEditorKit());
			editor.setContentType("text/prism");
		} else {
			editor.setContentType("text/plain");
		}

		editor.setBackground(Color.white);
		editor.setEditable(true);
		editor.setText("This is a test");
		// editor.getDocument().addDocumentListener(this);
		editor.getDocument().putProperty(PlainDocument.tabSizeAttribute, 4);

		// Setup the scrollpane
		editorScrollPane = new JScrollPane(editor);
		add(editorScrollPane, BorderLayout.CENTER);
	}

	void setEditorFont(Font font)
	{
		editor.setFont(font);
	}

	public static void main(String[] args) throws Exception
	{
		MetalTheme theme = new PresentationMetalTheme(0);
		MetalLookAndFeel.setCurrentTheme(theme);
		UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		//UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

		JFrame frame = new JFrame();
		frame.setLayout(new BorderLayout());

		String fname = "monospaced";
		int fsize = 12;

		boolean usePlain = args.length>=1 && args[0].equals("plain");

		System.out.println("Using " + (usePlain ? "plain" : "prism") + " editor kit.");

		GUIParedDownEditor e = new GUIParedDownEditor(usePlain);
		e.setEditorFont(new Font(fname, Font.PLAIN, fsize));
		e.setPreferredSize(new Dimension(DEFAULT_WINDOW_WIDTH, DEFAULT_WINDOW_HEIGHT));
		e.setMinimumSize(new Dimension(MINIMUM_WINDOW_WIDTH, MINIMUM_WINDOW_HEIGHT));

		frame.add(e);
		frame.pack();
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		frame.setVisible(true);
	}

}
