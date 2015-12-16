package com.zml.eclipse.powermode;

import org.eclipse.jdt.internal.ui.javaeditor.CompilationUnitEditor;
import org.eclipse.jface.text.ITextViewerExtension2;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.widgets.Composite;

import com.zml.eclipse.powermode.explosion.ExplosionPainter;

@SuppressWarnings("restriction")
public class PowerModeEditor extends CompilationUnitEditor {

    public void createPartControl(Composite parent) {
        super.createPartControl(parent);
        ISourceViewer sourceViewer = getSourceViewer();

        if (sourceViewer instanceof ITextViewerExtension2) {
            ITextViewerExtension2 extension = (ITextViewerExtension2)sourceViewer;
            ExplosionPainter fExplodePainter = new ExplosionPainter(sourceViewer);
            extension.addPainter(fExplodePainter);
        }
    }

}
