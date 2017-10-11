import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.paint.Color;

public class FPSData {
    SimpleIntegerProperty flame;
    SimpleIntegerProperty a;
    SimpleIntegerProperty b;
    SimpleIntegerProperty c;
    SimpleIntegerProperty d;
    SimpleIntegerProperty e;
    SimpleIntegerProperty f;
    SimpleIntegerProperty g;

    public static Color color[] = {Color.FLORALWHITE, Color.CRIMSON, Color.CORNFLOWERBLUE};

    public FPSData(int flame, int a, int b, int c, int d, int e, int f, int g) {
        this.flame = new SimpleIntegerProperty(flame);
        this.a = new SimpleIntegerProperty(a);
        this.b = new SimpleIntegerProperty(b);
        this.c = new SimpleIntegerProperty(c);
        this.d = new SimpleIntegerProperty(d);
        this.e = new SimpleIntegerProperty(e);
        this.f = new SimpleIntegerProperty(f);
        this.g = new SimpleIntegerProperty(g);
    }

    public static void setColor(Color[] c) {
        color = c;
    }

    public void setFlame(int f) {flame.set(f);}

    public void setA(int n) {a.set(n);}
    public void setB(int n) {b.set(n);}
    public void setC(int n) {c.set(n);}
    public void setD(int n) {d.set(n);}
    public void setE(int n) {e.set(n);}
    public void setF(int n) {f.set(n);}
    public void setG(int n) {g.set(n);}

    public int  getFlame() {
        return flame.get();
    }

    public void getA() {a.get();}
    public void getB() {b.get();}
    public void getC() {c.get();}
    public void getD() {d.get();}
    public void getE() {e.get();}
    public void getF() {f.get();}
    public void getG() {g.get();}

    public SimpleIntegerProperty[] getNotes() {
        SimpleIntegerProperty notes[] = {a,b,c,d,e,f,g};
        return notes;
    }

    public SimpleIntegerProperty flameProperty() {return flame;}

    public SimpleIntegerProperty aProperty() {return a;}
    public SimpleIntegerProperty bProperty() {return b;}
    public SimpleIntegerProperty cProperty() {return c;}
    public SimpleIntegerProperty dProperty() {return d;}
    public SimpleIntegerProperty eProperty() {return e;}
    public SimpleIntegerProperty fProperty() {return f;}
    public SimpleIntegerProperty gProperty() {return g;}

    public Color getColor() {
        return color[flame.get()];
    }

}
