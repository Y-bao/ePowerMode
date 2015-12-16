package com.zml.eclipse.powermode.explosion;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IPaintPositionManager;
import org.eclipse.jface.text.IPainter;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;

public class ExplosionPainter implements IPainter, PaintListener {

	/** Indicates whether this painter is active */
	private boolean fIsActive = false;

	/** The source viewer this painter is associated with */
	private final ISourceViewer fSourceViewer;

	/** The viewer's widget */
	private final StyledText fTextWidget;

	/** The paint position manager */
	private IPaintPositionManager fPaintPositionManager;

	private Set explosionSet = new LinkedHashSet();

	private boolean isRuning = false;

	private ExecutorService executor;

	private int previous;

	public ExplosionPainter(ISourceViewer sourceViewer) {
		fSourceViewer = sourceViewer;
		fTextWidget = sourceViewer.getTextWidget();
		executor = Executors.newSingleThreadExecutor();
	}

	public void paintControl(PaintEvent e) {
		Iterator iterator = explosionSet.iterator();
		while (iterator.hasNext()) {
			Explosion explosion = (Explosion) iterator.next();
			if (explosion.isAlive()) {
				explosion.update();
				explosion.draw(e.gc);
			} else {
				iterator.remove();
				explosion.dispose();
			}
		}
	}

	public void dispose() {
		executor.shutdown();
	}

	public void paint(int reason) {

		if (!fIsActive) {
			fIsActive = true;
			fTextWidget.addPaintListener(this);
		}

		IDocument document = fSourceViewer.getDocument();
		if (document == null) {
			deactivate(false);
			return;
		}
		int length = document.getLength();
		if (reason == IPainter.TEXT_CHANGE && previous != length) {
			previous = length;
			Point location = fTextWidget.getLocationAtOffset(fTextWidget.getCaretOffset());
			explosionSet.add(new Explosion(10, location.x, location.y + 5));
			fire();
		}

	}

	private void fire() {

		if (isRuning) {
			return;
		}

		isRuning = true;
		executor.execute(new Runnable() {
			public void run() {

				while (explosionSet.size() > 0) {
					Display.getDefault().syncExec(new Runnable() {
						public void run() {
							fTextWidget.redraw();
						}
					});
					try {
						Thread.currentThread().sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

				isRuning = false;
			}
		});
	}

	public void deactivate(boolean redraw) {
		if (fIsActive) {
			fIsActive = false;
			fTextWidget.removePaintListener(this);
		}
	}

	public void setPositionManager(IPaintPositionManager manager) {
		fPaintPositionManager = manager;
	}

}
