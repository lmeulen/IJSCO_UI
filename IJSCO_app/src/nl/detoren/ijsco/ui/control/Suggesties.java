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
 *Inspired by David:
 * http://stackoverflow.com/questions/15219625/how-would-be-implements-
 * autosugesion-in-jtextarea-swing/15220056#15220056
 *
 * Problemen in deze code:
 */
package nl.detoren.ijsco.ui.control;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JWindow;
import javax.swing.KeyStroke;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;

/**
 * Voeg suggestie lijst toe aan een textveld.
 * Gebruik:
 * JFrame frame = new JFrame();
 * JPanel panel = new JPanel();
 * JTextField tf = new JTextField();
 * ArrayList<String> words = new ArrayList<>();
 * words.add ......
 * <b>Suggesties suggesties = new Suggesties(f, frame, words, 3);</b>
 * panel.add(tf);
 * frame.add(panel);
 *
 * @author Leo.vanderMeulen
 *
 */
public class Suggesties {

	private JTextField textComp;
	private Window window;
	private JPanel suggestionsPanel;
	private JWindow suggestionsWindow;
	private String typedWord;
	private ArrayList<String> dictionary = new ArrayList<>();
	private int tWidth, tHeight, minTypedChars;
	private DocumentListener documentListener = new DocumentListener() {
		@Override
		public void insertUpdate(DocumentEvent de) {
			checkForAndShowSuggestions();
		}

		@Override
		public void removeUpdate(DocumentEvent de) {
			checkForAndShowSuggestions();
		}

		@Override
		public void changedUpdate(DocumentEvent de) {
			checkForAndShowSuggestions();
		}
	};
	private final Color suggestionsTextColor;
	private final Color suggestionFocusedColor;

	/**
	 * Creeer een autosuggestie popup bij het opgegeven textveld.
	 * @param field	- Het textfield dat wordt voorzien van de suggestie popup
	 * @param mainWindow - Het mainwindow waarin het textfield zich bevindt. Nodig om juiste
	 * positie popup te bepalen
	 * @param words - De suggestie woorden
	 * @param minchars - Minimaal aantal ingevoerde characters voordat de suggestie popup wordt getoond
	 */
	public Suggesties(JTextField field, Window mainWindow, ArrayList<String> words, int minchars) {
		this.textComp = field;
		this.suggestionsTextColor = Color.BLUE;
		this.window = mainWindow;
		this.minTypedChars = Math.max(1, minchars);

		this.suggestionFocusedColor = Color.RED;
		this.textComp.getDocument().addDocumentListener(documentListener);

		setDictionary(words);

		typedWord = "";
		tWidth = 0;
		tHeight = 0;

		suggestionsWindow = new JWindow(mainWindow);
		suggestionsWindow.setOpacity(0.75f);

		suggestionsPanel = new JPanel();
		suggestionsPanel.setLayout(new GridLayout(0, 1));
		suggestionsPanel.setBackground(Color.WHITE);

		addKeyBindingToRequestFocusInPopUpWindow();
	}

	@SuppressWarnings("serial")
	private void addKeyBindingToRequestFocusInPopUpWindow() {
		textComp.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0, true),
				"Down released");
		textComp.getActionMap().put("Down released", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				for (int i = 0; i < suggestionsPanel.getComponentCount(); i++) {
					if (suggestionsPanel.getComponent(i) instanceof SuggestionLabel) {
						((SuggestionLabel) suggestionsPanel.getComponent(i)).setFocused(true);
						suggestionsWindow.toFront();
						suggestionsWindow.requestFocusInWindow();
						suggestionsPanel.requestFocusInWindow();
						suggestionsPanel.getComponent(i).requestFocusInWindow();
						break;
					}
				}
			}
		});
		suggestionsPanel.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
				.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0, true), "Down released");
		suggestionsPanel.getActionMap().put("Down released", new AbstractAction() {
			int lastFocusableIndex = 0;

			@Override
			public void actionPerformed(ActionEvent ae) {
				ArrayList<SuggestionLabel> sls = getAddedSuggestionLabels();
				int max = sls.size();

				if (max > 1) {// more than 1 suggestion
					for (int i = 0; i < max; i++) {
						SuggestionLabel sl = sls.get(i);
						if (sl.isFocused()) {
							if (lastFocusableIndex == max - 1) {
								lastFocusableIndex = 0;
								sl.setFocused(false);
								suggestionsWindow.setVisible(false);
								setFocusToTextField();
								checkForAndShowSuggestions();
							} else {
								sl.setFocused(false);
								lastFocusableIndex = i;
							}
						} else if (lastFocusableIndex <= i) {
							if (i < max) {
								sl.setFocused(true);
								suggestionsWindow.toFront();
								suggestionsWindow.requestFocusInWindow();
								suggestionsPanel.requestFocusInWindow();
								suggestionsPanel.getComponent(i).requestFocusInWindow();
								lastFocusableIndex = i;
								break;
							}
						}
					}
				} else {// only a single suggestion was given
					suggestionsWindow.setVisible(false);
					setFocusToTextField();
					checkForAndShowSuggestions();
				}
			}
		});
	}

	private void setFocusToTextField() {
		window.toFront();
		window.requestFocusInWindow();
		textComp.requestFocusInWindow();
	}

	public ArrayList<SuggestionLabel> getAddedSuggestionLabels() {
		ArrayList<SuggestionLabel> sls = new ArrayList<>();
		for (int i = 0; i < suggestionsPanel.getComponentCount(); i++) {
			if (suggestionsPanel.getComponent(i) instanceof SuggestionLabel) {
				SuggestionLabel sl = (SuggestionLabel) suggestionsPanel.getComponent(i);
				sls.add(sl);
			}
		}
		return sls;
	}

	private void checkForAndShowSuggestions() {
		typedWord = getCurrentlyTypedWord();

		suggestionsPanel.removeAll();
		// used to calcualte size of JWindow as new Jlabels are added
		tWidth = 0;
		tHeight = 0;

		boolean added = wordTyped(typedWord);

		if (!added) {
			if (suggestionsWindow.isVisible()) {
				suggestionsWindow.setVisible(false);
			}
		} else {
			showPopUpWindow();
			setFocusToTextField();
		}
	}

	protected void addWordToSuggestions(String word) {
		SuggestionLabel suggestionLabel = new SuggestionLabel(word, suggestionFocusedColor, suggestionsTextColor, this);

		calculatePopUpWindowSize(suggestionLabel);

		suggestionsPanel.add(suggestionLabel);
	}

	public String getCurrentlyTypedWord() {
		return textComp.getText().trim();
	}

	private void calculatePopUpWindowSize(JLabel label) {
		// so we can size the JWindow correctly
		if (tWidth < label.getPreferredSize().width) {
			tWidth = label.getPreferredSize().width;
		}
		tHeight += label.getPreferredSize().height;
	}

	private void showPopUpWindow() {
		suggestionsWindow.getContentPane().add(suggestionsPanel);
		suggestionsWindow.setMinimumSize(new Dimension(textComp.getWidth(), 30));
		suggestionsWindow.setSize(tWidth, tHeight);
		suggestionsWindow.setVisible(true);

		int windowX = 0;
		int windowY = 0;

		windowX = window.getX() + textComp.getX() + 5;
		if (suggestionsPanel.getHeight() > suggestionsWindow.getMinimumSize().height) {
			windowY = window.getY() + textComp.getY() + textComp.getHeight()	+ suggestionsWindow.getMinimumSize().height + 25;
		} else {
			windowY = window.getY() + textComp.getY() + textComp.getHeight()
					+ suggestionsWindow.getHeight() + 25;
		}
		// show the pop up
		suggestionsWindow.setLocation(windowX, windowY);
		suggestionsWindow.setMinimumSize(new Dimension(textComp.getWidth(), 30));
		suggestionsWindow.revalidate();
		suggestionsWindow.repaint();

	}

	public void setDictionary(ArrayList<String> words) {
		dictionary.clear();
		if (words == null) {
		}
		for (String word : words) {
			dictionary.add(word);
		}
	}

	public JWindow getAutoSuggestionPopUpWindow() {
		return suggestionsWindow;
	}

	public Window getContainer() {
		return window;
	}

	public JTextComponent getTextField() {
		return textComp;
	}

	public void addToDictionary(String word) {
		dictionary.add(word);
	}

	boolean wordTyped(String typedWord) {
		if (typedWord.isEmpty() || typedWord.length() < minTypedChars) {
			return false;
		}
		boolean suggestionAdded = false;
		String tw = typedWord.toLowerCase();
		for (String word : dictionary) {
			if (word.toLowerCase().contains(tw)) {
				addWordToSuggestions(word);
				suggestionAdded = true;
			}
		}
		return suggestionAdded;
	}

	@SuppressWarnings("serial")
	class SuggestionLabel extends JLabel {

		private boolean focused = false;
		private final JWindow autoSuggestionsPopUpWindow;
		private final JTextComponent textComponent;
		private final Suggesties autoSuggestor;
		private Color suggestionsTextColor, suggestionBorderColor;

		public SuggestionLabel(String string, final Color borderColor, Color suggestionsTextColor,
				Suggesties autoSuggestor) {
			super(string);

			this.suggestionsTextColor = suggestionsTextColor;
			this.autoSuggestor = autoSuggestor;
			this.textComponent = autoSuggestor.getTextField();
			this.suggestionBorderColor = borderColor;
			this.autoSuggestionsPopUpWindow = autoSuggestor.getAutoSuggestionPopUpWindow();

			initComponent();
		}

		private void initComponent() {
			setFocusable(true);
			setForeground(suggestionsTextColor);

			addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent me) {
					super.mouseClicked(me);

					replaceWithSuggestedText();

					autoSuggestionsPopUpWindow.setVisible(false);
				}
			});

			getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, true),
					"Enter released");
			getActionMap().put("Enter released", new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent ae) {
					replaceWithSuggestedText();
					autoSuggestionsPopUpWindow.setVisible(false);
				}
			});
		}

		public void setFocused(boolean focused) {
			if (focused) {
				setBorder(new LineBorder(suggestionBorderColor));
			} else {
				setBorder(null);
			}
			repaint();
			this.focused = focused;
		}

		public boolean isFocused() {
			return focused;
		}

		private void replaceWithSuggestedText() {
			String suggestedWord = getText();
			String text = textComponent.getText();
			String typedWord = autoSuggestor.getCurrentlyTypedWord();
			String t = text.substring(0, text.lastIndexOf(typedWord));
			String tmp = t + text.substring(text.lastIndexOf(typedWord)).replace(typedWord, suggestedWord);
			textComponent.setText(tmp + " ");
		}
	}
}
