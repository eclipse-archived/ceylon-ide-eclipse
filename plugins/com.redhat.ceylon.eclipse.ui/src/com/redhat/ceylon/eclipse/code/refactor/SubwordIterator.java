package com.redhat.ceylon.eclipse.code.refactor;

import static java.lang.Character.isUpperCase;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.Text;

final class SubwordIterator implements KeyListener {
	private final Text text;

	SubwordIterator(Text text) {
		this.text = text;
	}

	@Override
	public void keyReleased(KeyEvent e) {}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.keyCode==SWT.ARROW_RIGHT && 
				e.stateMask==SWT.MOD3) { //TODO: CTRL on windows?
			int len = text.getText().length();
			for (int i = text.getCaretPosition()+1; i<len; i++) {
				if (isUpperCase(text.getText().charAt(i))) {
					text.setSelection(i);
					e.doit = false;
					break;
				}
			}
		}
		if (e.keyCode==SWT.ARROW_LEFT && 
				e.stateMask==SWT.MOD3) { //TODO: CTRL on windows?
			for (int i = text.getCaretPosition()-1; i>=0; i--) {
				if (isUpperCase(text.getText().charAt(i))) {
					text.setSelection(i);
					e.doit = false;
					break;
				}
			}
		}
	}
}