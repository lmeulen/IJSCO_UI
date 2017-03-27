/**
 * Copyright (C) 2016 Leo van der Meulen
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation version 3.0
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * See: http://www.gnu.org/licenses/gpl-3.0.html
 *
 * Problemen in deze code:
 */
package nl.detoren.ijsco.ui.control;

import java.awt.event.ActionEvent;
import java.util.Collections;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;

public class AutoComplete implements DocumentListener {

	private static enum Mode {
		INSERT, COMPLETION
	};

	private JTextField tekstveld;
	private List<String> woorden;
	private Mode modus = Mode.INSERT;

	public AutoComplete(JTextField veld, List<String> woorden) {
		this.tekstveld = veld;
		this.woorden = woorden;
		Collections.sort(woorden);
	}

	public void setKeywords(List<String> woorden) {
		this.woorden = woorden;
	}

	@Override
	public void changedUpdate(DocumentEvent ev) {
	}

	@Override
	public void removeUpdate(DocumentEvent ev) {
	}

	@Override
	public void insertUpdate(DocumentEvent ev) {
		if (ev.getLength() != 1)
			return;

		int pos = ev.getOffset();
		String content = null;
		try {
			content = tekstveld.getText(0, pos + 1);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}

		// Find where the word starts
		int w;
		for (w = pos; w >= 0; w--) {
			if (!Character.isLetter(content.charAt(w))) {
				break;
			}
		}

		// Too few chars
		if (pos - w < 2)
			return;

		String prefix = content.substring(w + 1).toLowerCase();
		int n = Collections.binarySearch(woorden, prefix);
		if (n < 0 && -n <= woorden.size()) {
			String match = woorden.get(-n - 1);
			if (match.startsWith(prefix)) {
				// A completion is found
				String completion = match.substring(pos - w);
				// We cannot modify Document from within notification,
				// so we submit a task that does the change later
				SwingUtilities.invokeLater(new CompletionTask(completion, pos + 1));
			}
		} else {
			// Nothing found
			modus = Mode.INSERT;
		}
	}

	public class CommitAction extends AbstractAction {
		/**
		 *
		 */
		private static final long serialVersionUID = 5794543109646743416L;

		@Override
		public void actionPerformed(ActionEvent ev) {
			if (modus == Mode.COMPLETION) {
				int pos = tekstveld.getSelectionEnd();
				StringBuffer sb = new StringBuffer(tekstveld.getText());
				sb.insert(pos, " ");
				tekstveld.setText(sb.toString());
				tekstveld.setCaretPosition(pos + 1);
				modus = Mode.INSERT;
			} else {
				tekstveld.replaceSelection("\t");
			}
		}
	}

	private class CompletionTask implements Runnable {
		private String completion;
		private int position;

		CompletionTask(String completion, int position) {
			this.completion = completion;
			this.position = position;
		}

		public void run() {
			StringBuffer sb = new StringBuffer(tekstveld.getText());
			sb.insert(position, completion);
			tekstveld.setText(sb.toString());
			tekstveld.setCaretPosition(position + completion.length());
			tekstveld.moveCaretPosition(position);
			modus = Mode.COMPLETION;
		}
	}

}

/**
 *
 * private static final String COMMIT_ACTION = "commit";
 * JTextField mainTextField = new JTextField();
 *
 * // Without this, cursor always leaves text field
 * mainTextField.setFocusTraversalKeysEnabled(false);
 * ...
 * // Our words to complete
 * keywords = new ArrayList<String>(5);
 *         keywords.add("example");
 *         keywords.add("autocomplete");
 *         keywords.add("stackabuse");
 *         keywords.add("java");
 * Autocomplete autoComplete = new Autocomplete(mainTextField, keywords);
 * mainTextField.getDocument().addDocumentListener(autoComplete);
 *
 * // Maps the tab key to the commit action, which finishes the autocomplete
 * // when given a suggestion
 * mainTextField.getInputMap().put(KeyStroke.getKeyStroke("TAB"), COMMIT_ACTION);
 * mainTextField.getActionMap().put(COMMIT_ACTION, autoComplete.new CommitAction());
 */
