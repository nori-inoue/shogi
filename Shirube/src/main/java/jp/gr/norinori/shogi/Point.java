package jp.gr.norinori.shogi;

/**
 * 位置情報クラス
 *
 * @author nori
 */
public class Point {
	public byte x;
	public byte y;

	/**
	 * 位置情報を指定してインスタンスを生成
	 *
	 * @param x
	 * @param y
	 */
	public Point(int x, int y) {
		this((byte) x, (byte) y);
	}

	/**
	 * 位置情報を指定してインスタンスを生成
	 *
	 * @param x
	 * @param y
	 */
	public Point(byte x, byte y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * 位置情報のクローンを生成する
	 */
	public Point clone() {
		return new Point(this.x, this.y);
	}

	@Override
	public String toString() {
		return "Point [x=" + this.x + ", y=" + this.y + "]";
	}

	@Override
	public int hashCode() {
		return (int) this.x * 23 + (int) this.y;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Point other = (Point) obj;
		if (this.x != other.x)
			return false;
		if (this.y != other.y)
			return false;
		return true;
	}

}
