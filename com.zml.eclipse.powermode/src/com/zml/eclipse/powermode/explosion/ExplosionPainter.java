package com.zml.eclipse.powermode.explosion;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IPaintPositionManager;
import org.eclipse.jface.text.IPainter;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;

/**
 * The explosion painter.
 * 
 * @author zml
 *
 */
public class ExplosionPainter implements IPainter, PaintListener {

	/** The source viewer this painter is associated with */
	private final ISourceViewer mSourceViewer;

	/** The viewer's widget */
	private final StyledText mTextWidget;

	/** The thread for animation pump. */
	private final ExecutorService mExecutorService;

	/** The set to collect explosions. */
	private final Set mExplosionSet = new LinkedHashSet();

	/** The lock for the explosion set. */
	private final ReadWriteLock mLock = new ReentrantReadWriteLock();

	/** Indicates whether this painter is active */
	private boolean mIsActive = false;

	/** The paint position manager */
	private IPaintPositionManager mPaintPositionManager;

	private boolean mIsRuning = false;

	private int mPrevious;

	/**
	 * Constructor.
	 * 
	 * @param sourceViewer
	 *            The source viewer.
	 */
	public ExplosionPainter(ISourceViewer sourceViewer) {
		mSourceViewer = sourceViewer;
		mTextWidget = sourceViewer.getTextWidget();
		mExecutorService = Executors.newSingleThreadExecutor();
	}

	/**
	 * {@inheritDoc}
	 */
	public void paintControl(PaintEvent e) {
		Iterator iterator = null;

		Lock readLock = mLock.readLock();
		readLock.lock();
		try {
			iterator = new LinkedHashSet(mExplosionSet).iterator();
		} finally {
			readLock.unlock();
		}
		if (iterator != null) {
			while (iterator.hasNext()) {
				Explosion explosion = (Explosion) iterator.next();
				if (explosion.isAlive()) {
					explosion.draw(e.gc);
				}
			}
		}

	}

	/**
	 * {@inheritDoc}
	 */
	public void dispose() {
		mExecutorService.shutdown();
	}

	/**
	 * {@inheritDoc}
	 */
	public void paint(int reason) {

		if (!mIsActive) {
			mIsActive = true;
			mTextWidget.addPaintListener(this);
		}

		IDocument document = mSourceViewer.getDocument();
		if (document == null) {
			deactivate(false);
			return;
		}
		int length = document.getLength();
		if (reason == IPainter.TEXT_CHANGE && mPrevious != length) {
			mPrevious = length;
			Point location = mTextWidget.getLocationAtOffset(mTextWidget.getCaretOffset());
			Lock writeLock = mLock.writeLock();
			writeLock.lock();
			try {
				mExplosionSet.add(new Explosion(10, location.x, location.y + 5));
			} finally {
				writeLock.unlock();
			}
			fire();
		}
	}

	/**
	 * Fire one new animation.
	 */
	private void fire() {

		if (mIsRuning) {
			return;
		}

		mIsRuning = true;
		mExecutorService.execute(new Runnable() {
			public void run() {

				while (mExplosionSet.size() > 0) {

					// Update all valid explosions data.
					Iterator iterator = null;
					Lock readLock = mLock.readLock();
					readLock.lock();
					try {
						iterator = new LinkedHashSet(mExplosionSet).iterator();
					} finally {
						readLock.unlock();
					}
					while (iterator.hasNext()) {
						Explosion explosion = (Explosion) iterator.next();
						if (explosion.isAlive()) {
							explosion.update();
						} else {
							iterator.remove();
							explosion.dispose();
						}
					}

					// Trigger the redraw event.
					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							if (mTextWidget != null && !mTextWidget.isDisposed()) {
								mTextWidget.redraw();
							}
						}
					});

					// Sleep 0.1s to improve user experience.
					try {
						Thread.currentThread().sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

				mIsRuning = false;
			}
		});
	}

	/**
	 * {@inheritDoc}
	 */
	public void deactivate(boolean redraw) {
		if (mIsActive) {
			mIsActive = false;
			mTextWidget.removePaintListener(this);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void setPositionManager(IPaintPositionManager manager) {
		mPaintPositionManager = manager;
	}

}
