import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.paint.Color;

public class FPSData {
	SimpleIntegerProperty flame ;
  SimpleIntegerProperty a;
  SimpleIntegerProperty b;
  SimpleIntegerProperty c;
  SimpleIntegerProperty d;


	public static Color color[] = {Color.FLORALWHITE, Color.CRIMSON, Color.CORNFLOWERBLUE};

	public FPSData(int f, int a, int b, int c, int d) {
    this.flame = new SimpleIntegerProperty(f);
    this.a = new SimpleIntegerProperty(a);
    this.b = new SimpleIntegerProperty(b);
    this.c = new SimpleIntegerProperty(c);
    this.d = new SimpleIntegerProperty(d);
	}

	public static void setColor(Color[] c) {
		color = c;
	}

	public void setFlame(int f) {
		flame.set(f);
	}

  public void setA(int n) {
    a.set(n);
  }

  public void setB(int n) {
    b.set(n);
  }

  public void setC(int n) {
    c.set(n);
  }

  public void setD(int n) {
    d.set(n);
  }

  public void setNum (int index,int n) {
    switch (index) {
      case 0:
        setA(n);
        break;
      case 1:
        setB(n);
        break;
      case 2:
        setC(n);
        break;
      case 3:
        setD(n);
        break;
    }
  }

	public int  getFlame() {
		return flame.get();
	}

  public int getA() {
    return a.get();
  }

  public int getB() {
    return b.get();
  }

  public int getC() {
    return c.get();
  }

  public int getD() {
    return d.get();
  }

  public SimpleIntegerProperty[] getNotes() {
    SimpleIntegerProperty notes[] = {a,b,c,d};
    return notes;
  }

	public SimpleIntegerProperty flameProperty() {
		return flame;
	}

	public SimpleIntegerProperty aProperty() {
		return a;
	}

	public SimpleIntegerProperty bProperty() {
		return b;
	}

	public SimpleIntegerProperty cProperty() {
		return c;
	}

	public SimpleIntegerProperty dProperty() {
		return d;
	}

	public Color getColor() {
		return color[flame.get()];
	}
}
