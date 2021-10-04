package com.chess.agchess;


import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedList;

import javafx.scene.Node;
import javafx.scene.layout.GridPane;

public class Highlighter {
	
	public enum Highlight {
		MOVE, ATTACK, SELECT, CHECK, WIN
	}

	private final Hashtable<Container, Highlight> highlights;
	
	public Highlighter() {
		highlights = new Hashtable<>(100);
	}

	public void highlightMoves(Board board, Piece piece) {
		LinkedList<Space> spaces = piece.getMoves(board);
		for (Space s : spaces) {
			Highlight h = board.getPiece(s) == null ? Highlight.MOVE : Highlight.ATTACK;
			Container c = new Container(getNode(board, s), false);
			highlight(c, h);
		}
	}
	
	public void highlightNode(Board board, Space space, Highlight light) {
		Node node = getNode(board, space);
		Container c = new Container(node, false);
		highlight(c, light);
	}
	
	public void highlightNodeIgnoreClear(Board board, Space space, Highlight light) {
		Node node = getNode(board, space);
		Container c = new Container(node, true);
		highlight(c, light);
	}
	
	private void highlight(Container c, Highlight newHighlight) {
		Node node = c.node;
		if (highlights.contains(c)) {
			String oldHighlight = highlights.get(c).name();
			node.getStyleClass().remove(oldHighlight);
			node.getStyleClass().add(newHighlight.name());
			highlights.replace(c, newHighlight);
		} else {
			highlights.put(c, newHighlight);
			node.getStyleClass().add(newHighlight.name());
		}
	}
	
	private Node getNode(Board board, Space space) {
		GridPane pane = board.getPane();
		return pane.getChildren().get(getIndex(board, space));
	}
	
	private int getIndex(Board board, Space space) {
		int rowSize = board.getMaxCols();
		return (space.row * rowSize) + space.col;
	}
	
	public void clearHighlights(boolean forceClear) {
		Enumeration<Container> containers = highlights.keys();
		while(containers.hasMoreElements()) {
			Container c = containers.nextElement();
			if (!forceClear && c.ignoreClear) continue;
			Node node = c.node;
			Highlight old = highlights.get(c);
			node.getStyleClass().remove(old.name());
			highlights.remove(c);
		}
	}
	
	private static class Container {
		private final Node node;
		private final boolean ignoreClear;
		public Container(Node node, boolean ignoreClear) {
			this.node = node;
			this.ignoreClear = ignoreClear;
		}
	}
}
