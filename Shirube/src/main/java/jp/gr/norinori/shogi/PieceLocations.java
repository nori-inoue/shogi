package jp.gr.norinori.shogi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import jp.gr.norinori.core.collection.NumberingHashMap;
import jp.gr.norinori.core.collection.NumberingMap;
import jp.gr.norinori.core.element.KeyValuePair;

/**
 * 駒配置クラス
 *
 * @author nori
 */
public class PieceLocations implements Iterable<KeyValuePair<Point, Piece>> {
	private NumberingMap<Point, Piece> pieces;

	// コンストラクタ===========================================================
	public PieceLocations() {
		this.pieces = new NumberingHashMap<>();
	}

	public void putAll(PieceLocations pieceLocation) {
		for (Entry<Point, Piece> en : pieceLocation.pieces.entrySet()) {
			this.pieces.put(en.getKey(), en.getValue());
		}
	}

	protected Map<Point, Piece> getPieces() {
		return this.pieces;
	}

	public Piece put(Point point, Piece piece) {
		return this.pieces.put(point, piece);
	}

	public Piece remove(Point point) {
		return this.pieces.remove(point);
	}

	public boolean existsPiece(Point point) {
		return this.pieces.containsKey(point);
	}

	public Piece get(Point point) {
		return this.pieces.get(point);
	}

	public Point getPoint(Piece piece) {
		for (Map.Entry<Point, Piece> en : this.pieces.entrySet()) {
			if (piece.equals(en.getValue())) {
				return en.getKey();
			}
		}
		return null;
	}

	public int size() {
		return this.pieces.size();
	}

	@Override
	public Iterator<KeyValuePair<Point, Piece>> iterator() {
		return new MyIterator();
	}

	public boolean isEmpty() {
		return this.pieces.isEmpty();
	}

	public void sort() {
		List<KeyValuePair<Piece, Point>> list = new ArrayList<>(this.pieces.size());
		for (Entry<Point, Piece> en : this.pieces.entrySet()) {
			KeyValuePair<Piece, Point> sortElement = new KeyValuePair<>(en.getValue(), en.getKey());
			list.add(sortElement);
		}

		Collections.sort(list, new Comparator<KeyValuePair<Piece, Point>>() {
			@Override
			public int compare(KeyValuePair<Piece, Point> o1, KeyValuePair<Piece, Point> o2) {
				if (o1 == null || o1.getKey() == null) {
					return -1;
				} else if (o1 != null && (o2 == null || o2.getKey() == null)) {
					return 1;
				}

				int value1 = o1.getKey().type.hashCode();
				int value2 = o2.getKey().type.hashCode();

				if (value1 < value2) {
					return 1;
				} else if (value1 == value2) {
					return 0;
				} else {
					return -1;
				}
			}
		});

		List<Point> keyList = new ArrayList<>();
		for (KeyValuePair<Piece, Point> p : list) {
			keyList.add(p.getValue());
		}
		this.pieces.relocation(keyList);
	}

	public PieceLocations clone() {
		PieceLocations newPieceLocations = new PieceLocations();
		newPieceLocations.putAll(this);
		return newPieceLocations;
	}

	class MyIterator implements Iterator<KeyValuePair<Point, Piece>> {
		private List<Point> pointList;
		private int listSize;
		private int counter;

		public MyIterator() {
			this.pointList = pieces.keyList();
			this.listSize = this.pointList.size();
			this.counter = 0;
		}

		@Override
		public boolean hasNext() {
			return (this.listSize > this.counter);
		}

		@Override
		public KeyValuePair<Point, Piece> next() {
			Point point = this.pointList.get(this.counter++);
			return new KeyValuePair<Point, Piece>(point, pieces.get(point));
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

	}
}
