//==============================================================================
//	
//	Copyright (c) 2002-
//	Authors:
//	* Andrew Hinton <ug60axh@cs.bham.ac.uk> (University of Birmingham)
//	* Dave Parker <david.parker@comlab.ox.ac.uk> (University of Oxford, formerly University of Birmingham)
//	* Mark Kattenbelt <mark.kattenbelt@comlab.ox.ac.uk> (University of Oxford, formerly University of Birmingham)
//	* Charles Harley <cd.harley@talk21.com> (University of Edinburgh)
//	* Sebastian Vermehren <seb03@hotmail.com> (University of Edinburgh)
//	
//------------------------------------------------------------------------------
//	
//	This file is part of PRISM.
//	
//	PRISM is free software; you can redistribute it and/or modify
//	it under the terms of the GNU General Public License as published by
//	the Free Software Foundation; either version 2 of the License, or
//	(at your option) any later version.
//	
//	PRISM is distributed in the hope that it will be useful,
//	but WITHOUT ANY WARRANTY; without even the implied warranty of
//	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//	GNU General Public License for more details.
//	
//	You should have received a copy of the GNU General Public License
//	along with PRISM; if not, write to the Free Software Foundation,
//	Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
//	
//==============================================================================

package userinterface.model;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.PlainView;
import javax.swing.text.Segment;
import javax.swing.text.StyleContext;
import javax.swing.text.Utilities;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;

/** Prism model editor kit for the text model editor. Defines the syntax
 * highlighting that the model editor should use.
 */
@SuppressWarnings("serial")
class ParedPrismEditorKit extends DefaultEditorKit
{
	private PrismContext preferences;

	public PrismContext getStylePreferences()
	{
		if (preferences == null) {
			preferences = new PrismContext();
		}
		return preferences;
	}

	public void setStylePreferences(PrismContext preferences)
	{
		this.preferences = preferences;
	}

	public String getContentType()
	{
		return "text/prism";
	}

	public Document createDefaultDocument()
	{
		return new DefaultStyledDocument();
	}

	public final ViewFactory getViewFactory()
	{
		return getStylePreferences();
	}

	@SuppressWarnings("serial")
	static class PrismContext extends StyleContext implements ViewFactory
	{

		public static final String KEY_WORD_D = "Prism Keyword";
		public static final String NUMERIC_D = "Numeric";
		public static final String VARIABLE_D = "Variable";
		public static final String COMMENT_D = "Single Line Comment";

		public View create(Element element)
		{
			return new PrismView(element);
		}

	}

	static class PrismView extends PlainView
	{

		static final Style PLAIN_S = new Style(Color.black, Font.PLAIN);

		public PrismView(Element element)
		{
			super(element);
		}

		protected int drawUnselectedText(Graphics g, int x, int y, int p0, int p1) throws BadLocationException
		{
			int stLine = p0;// findStartOfLine(p0, getDocument());
			int enLine = p1;// findEndOfLine(p1-1, getDocument());

			if (g instanceof Graphics2D) {
				Graphics2D g2d = (Graphics2D) g;
				g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			}

			// x+= getLeftInset();
			// System.out.println("p0 = "+p0+", p1 = "+p1+", st = "+stLine+",
			// enLine = "+enLine+".");
			try {
				g.setColor(Color.green);
				Document doc = getDocument();
				// Segment segment = getLineBuffer();

				// System.out.println(doc.getText(p0, p1-p0));
				// String s = doc.getText(p0, p1-p0);
				String s = doc.getText(stLine, enLine - stLine);
				// System.out.println("------");
				// System.out.println("highlighting unselected string = \n"+s);
				// System.out.println("------");

				Style[] styles = highlight(s, (p0 - stLine), (p1 - p0));
				int currStart = 0;
				int currEnd = 0;
				Color last = null;
				//			String fname = handler.getPrismEditorFontFast().getName();
				//			int fsize = handler.getPrismEditorFontFast().getSize();

				//String fname = handler.getPrismEditorFontFast().getName();
				//int fsize = handler.getPrismEditorFontFast().getSize();

				String fname = "monospaced";
				int fsize = 12;

				for (int curr = 0; curr < styles.length; curr++) {

					Style c = styles[curr];

					g.setColor(c.c);
					g.setFont(new Font(fname, c.style, fsize));
					Segment segm = new Segment();// getLineBuffer();
					doc.getText(p0 + curr, 1, segm);
					x = Utilities.drawTabbedText(segm, x, y, g, this, p0 + curr);

				}
				g.setColor(Color.black);
				g.setFont(new Font(fname, Font.PLAIN, fsize));
			} catch (BadLocationException ex) {
				ex.printStackTrace();
			}
			return x;
		}

		protected int drawSelectedText(Graphics g, int x, int y, int p0, int p1) throws BadLocationException
		{
			int stLine = p0;// findStartOfLine(p0, getDocument());
			int enLine = p1;// findEndOfLine(p1-1, getDocument());

			if (g instanceof Graphics2D) {
				Graphics2D g2d = (Graphics2D) g;
				g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			}

			// x+= getLeftInset();
			// System.out.println("p0 = "+p0+", p1 = "+p1+", st = "+stLine+",
			// enLine = "+enLine+".");
			try {
				g.setColor(Color.green);
				Document doc = getDocument();
				// Segment segment = getLineBuffer();

				// String s = doc.getText(p0, p1-p0);
				// System.out.println(doc.getText(p0, p1-p0));

				String s = doc.getText(stLine, enLine - stLine);
				// System.out.println("------");
				// System.out.println("highlighting selected string = \n"+s);
				// System.out.println("------");
				Style[] styles = highlight(s, (p0 - stLine), (p1 - p0));
				int currStart = 0;
				int currEnd = 0;
				Color last = null;

				//String fname = handler.getPrismEditorFontFast().getName();
				//int fsize = handler.getPrismEditorFontFast().getSize();

				String fname = "monospaced";
				int fsize = 12;

				for (int curr = 0; curr < styles.length; curr++) {
					Style c = styles[curr];

					g.setColor(c.c);
					g.setFont(new Font(fname, c.style, fsize));
					Segment segm = new Segment();// getLineBuffer();
					doc.getText(p0 + curr, 1, segm);
					x = Utilities.drawTabbedText(segm, x, y, g, this, p0 + curr);

				}
				g.setColor(Color.black);
				g.setFont(new Font(fname, Font.PLAIN, fsize));
			} catch (BadLocationException ex) {
				ex.printStackTrace();
			}
			return x;
		}

		private synchronized Style[] highlight(String s, int offset, int length)
		{
			Style[] ret = new Style[length];
			for (int i = 0; i < length; i++) {
				ret[i] = PLAIN_S;
			}

			return ret;
		}

	}
}
