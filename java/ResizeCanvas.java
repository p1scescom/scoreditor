import javafx.scene.canvas.Canvas;

public class ResizeCanvas extends Canvas{


  @Override
  public boolean isResizable() {
    return true;
  }

  @Override
  public double prefWidth (double height) {
    return getWidth();

  }

  @Override
  public double prefHeight (double width) {
    return getHeight();
  }
}
