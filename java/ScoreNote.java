import javafx.beans.property.IntegerProperty;
import javafx.scene.paint.Color;

public class ScoreNote {
	IntegerProperty noteNum;

	public static Color color[] = {Color.FLORALWHITE, Color.CRIMSON, Color.CORNFLOWERBLUE};

	public ScoreNote(int arg) {
		setNum(arg);
	}

	public static void setColor(Color[] c) {
		color = c;
	}

	public void setNum(int arg) {
		if(arg < 0 || arg > 3) {
			throw new IllegalArgumentException("arg over.");	
		}
		noteNum.set(arg);
	}

	public int  getNoteNum() {
		return noteNum.get();	
	}
	public IntegerProperty noteNumProperty() {
		return noteNum;	
	}

	public Color getColor() {
		return color[noteNum.get()];
	}
}
