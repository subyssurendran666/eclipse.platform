/*******************************************************************************
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.ui.forms.widgets;
import org.eclipse.jface.action.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.forms.internal.widgets.FormsResources;
/**
 * Form is a control that is capable of scrolling its content. It renders the
 * content below the title and can accept optional background image. Form
 * should be created in a parent that will allow it to use all the available
 * area (for example, a shell, a view or an editor).
 * <p>
 * Children of the form should typically be created using FormToolkit to match
 * the appearance and behaviour. When creating children, use a form body as a
 * parent by calling 'getBody()' on the form instance. Example:
 * 
 * <pre>
 *  Form form = new Form(parent);
 *  FormToolkit toolkit = new FormToolkit(parent.getDisplay());
 *  form.setText(&quot;Sample form&quot;);
 *  form.getBody().setLayout(new GridLayout());
 *  toolkit.createButton(form.getBody(), &quot;Checkbox&quot;, SWT.CHECK);
 * </pre>
 * 
 * <p>
 * No layout manager has been set on the body. Clients are required to set the
 * desired layout manager explicitly.
 * 
 * @since 3.0
 */
public class Form extends SharedScrolledComposite {
	private int TITLE_HMARGIN = 10;
	private int TITLE_VMARGIN = 5;
	private int TITLE_GAP = 5;
	private Image backgroundImage;
	private String text;
	private Composite body;
	private ToolBarManager toolBarManager;
	private class ContentComposite extends Composite {
		public ContentComposite(Composite parent, int style) {
			super(parent, style);
		}
		public Point computeSize(int wHint, int hHint, boolean changed) {
			return ((FormLayout) getLayout()).computeSize(this, wHint, hHint,
					changed);
		}
	}
	private class FormLayout extends Layout implements ILayoutExtension {
		public int computeMinimumWidth(Composite composite, boolean flushCache) {
			return computeSize(composite, 5, SWT.DEFAULT, flushCache).x;
		}
		public int computeMaximumWidth(Composite composite, boolean flushCache) {
			return computeSize(composite, SWT.DEFAULT, SWT.DEFAULT, flushCache).x;
		}
		public Point computeSize(Composite composite, int wHint, int hHint,
				boolean flushCache) {
			int width = 0;
			int height = 0;
			if (text != null) {
				GC gc = new GC(composite);
				gc.setFont(getFont());
				if (wHint != SWT.DEFAULT) {
					Point wsize = FormUtil.computeWrapSize(gc, text, wHint);
					width = wsize.x;
					height = wsize.y;
				} else {
					Point extent = gc.textExtent(text);
					width = extent.x;
					height = extent.y;
				}
				gc.dispose();
			}
			if (toolBarManager != null) {
				ToolBar toolBar = toolBarManager.getControl();
				if (toolBar != null) {
					Point tbsize = toolBar
							.computeSize(SWT.DEFAULT, SWT.DEFAULT);
					if (width != 0)
						width += TITLE_GAP;
					width += tbsize.x;
					height = Math.max(height, tbsize.y);
				}
			}
			if (height != 0)
				height += TITLE_VMARGIN * 2;
			if (width != 0)
				width += TITLE_HMARGIN * 2;
			int ihHint = hHint;
			if (ihHint > 0 && ihHint != SWT.DEFAULT)
				ihHint -= height;
			Point bsize = body.computeSize(FormUtil.getWidthHint(wHint, body),
					FormUtil.getHeightHint(ihHint, body), flushCache);
			width = Math.max(bsize.x, width);
			height += bsize.y;
			return new Point(width, height);
		}
		protected void layout(Composite composite, boolean flushCache) {
			Rectangle carea = composite.getClientArea();
			int height = 0;
			Point tbsize = null;
			int twidth = carea.width - TITLE_HMARGIN * 2;
			if (toolBarManager != null) {
				ToolBar toolBar = toolBarManager.getControl();
				if (toolBar != null) {
					tbsize = toolBar.computeSize(SWT.DEFAULT, SWT.DEFAULT);
					toolBar.setBounds(carea.width - 1 - TITLE_HMARGIN
							- tbsize.x, TITLE_VMARGIN, tbsize.x, tbsize.y);
					height = tbsize.y;
				}
			}
			if (tbsize != null) {
				twidth -= tbsize.x - TITLE_GAP;
			}
			if (text != null) {
				GC gc = new GC(composite);
				gc.setFont(getFont());
				height = FormUtil.computeWrapSize(gc, text, twidth).y;
				gc.dispose();
				if (tbsize != null)
					height = Math.max(tbsize.y, height);
			}
			if (height > 0)
				height += TITLE_VMARGIN * 2;
			body.setBounds(0, height, carea.width, carea.height - height);
		}
	}
	/**
	 * Creates the form control as a child of the provided parent.
	 * 
	 * @param parent
	 *            the parent widget
	 */
	public Form(Composite parent) {
		super(parent, SWT.NULL);
		final Composite content = new ContentComposite(this, SWT.NULL);
		content.addListener(SWT.Paint, new Listener() {
			public void handleEvent(Event e) {
				onPaint(content, e.gc);
			}
		});
		super.setContent(content);
		content.setLayout(new FormLayout());
		body = new LayoutComposite(content, SWT.NULL);
		body.setMenu(parent.getMenu());
	}
	/**
	 * Returns the title text that will be rendered at the top of the form.
	 * 
	 * @return the title text
	 */
	public String getText() {
		return text;
	}
	/**
	 * Sets the foreground color of the form. This color will also be used for
	 * the body.
	 */
	public void setForeground(Color fg) {
		super.setForeground(fg);
		body.setForeground(fg);
	}
	/**
	 * Sets the background color of the form. This color will also be used for
	 * the body.
	 */
	public void setBackground(Color bg) {
		super.setBackground(bg);
		body.setBackground(bg);
		if (toolBarManager != null)
			toolBarManager.getControl().setBackground(bg);
	}
	/**
	 * The form sets the content widget. This method should not be called by
	 * classes that instantiate this widget.
	 */
	public final void setContent(Control c) {
	}
	/**
	 * Sets the text to be rendered at the top of the form above the body as a
	 * title.
	 * 
	 * @param text
	 *            the title text
	 */
	public void setText(String text) {
		this.text = text;
		layout(true);
		redraw();
	}
	/**
	 * Returns the optional background image of this form. The image is
	 * rendered starting at the position 0,0 and is painted behind the title.
	 * 
	 * @return Returns the background image.
	 */
	public Image getBackgroundImage() {
		return backgroundImage;
	}
	/**
	 * Sets the optional background image to be rendered behind the title
	 * starting at the position 0,0.
	 * 
	 * @param backgroundImage
	 *            The backgroundImage to set.
	 */
	public void setBackgroundImage(Image backgroundImage) {
		this.backgroundImage = backgroundImage;
		redraw();
	}
	/**
	 * Returns the tool bar manager that is used to manage tool items in the
	 * form's title area.
	 * 
	 * @return form tool bar manager
	 */
	public IToolBarManager getToolBarManager() {
		if (toolBarManager == null) {
			toolBarManager = new ToolBarManager(SWT.FLAT);
			ToolBar toolbar = toolBarManager
					.createControl((Composite) getContent());
			toolbar.setBackground(getBackground());
			toolbar.setForeground(getForeground());
			toolbar.setCursor(FormsResources.getHandCursor());
			getContent().addDisposeListener(new DisposeListener() {
				public void widgetDisposed(DisposeEvent e) {
					if (toolBarManager != null) {
						toolBarManager.dispose();
						toolBarManager = null;
					}
				}
			});
		}
		return toolBarManager;
	}
	/**
	 * Updates the local tool bar manager if used. Does nothing if local tool
	 * bar manager has not been created yet.
	 */
	public void updateToolBar() {
		if (toolBarManager != null)
			toolBarManager.update(false);
	}
	/**
	 * Recomputes the body layout and form scroll bars. The method should be
	 * used when changes somewhere in the form body invalidate the current
	 * layout and/or scroll bars.
	 * 
	 * @param flushCache
	 *            if <samp>true </samp>, drop any cached layout information and
	 *            compute new one.
	 */
	public void reflow(boolean flushCache) {
		if (body != null) {
			body.layout(flushCache);
		}
		super.reflow(flushCache);
	}
	/**
	 * Returns the container that occupies the body of the form (the form area
	 * below the title). Use this container as a parent for the controls that
	 * should be in the form. No layout manager has been set on the form body.
	 * 
	 * @return Returns the body of the form.
	 */
	public Composite getBody() {
		return body;
	}
	private void onPaint(Composite c, GC gc) {
		Rectangle carea = c.getClientArea();
		if (backgroundImage != null) {
			gc.drawImage(backgroundImage, 0, 0);
		}
		if (text != null) {
			gc.setBackground(getBackground());
			gc.setForeground(getForeground());
			gc.setFont(getFont());
			FormUtil.paintWrapText(gc, new Point(carea.width, carea.height),
					text, TITLE_HMARGIN, TITLE_VMARGIN);
		}
	}
}