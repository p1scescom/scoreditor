import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.control.TableCell;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;

public class ColorCulmCell extends TableCell<FPSData,Integer> {

	public static Color colors[] = {Color.AQUAMARINE, Color.CRIMSON, Color.CORNFLOWERBLUE,Color.ORANGE};


  public ColorCulmCell() {
    setText(null);
  }


  @Override
  public void updateItem(Integer item, boolean empty) {

    super.updateItem(item,empty);
    Rectangle rect = new Rectangle(70,20);
    if(item != null) {
      rect.setFill(colors[item]);
      setGraphic(rect);

    }
  }

  public static void setColors(Color c[]) {
    colors = c;
  }

  public static void setColor(int index, Color c) {
    colors[index] = c;
  }

}
