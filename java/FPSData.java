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
        case 4:
            setE(n);
            break;
        case 5:
            setF(n);
            break;
        case 6:
            setG(n);
            break;
        }
    }

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

    public void getNum (int index) {
        switch (index) {
        case 0:
            getA();
            break;
        case 1:
            getB();
            break;
        case 2:
            getC();
            break;
        case 3:
            getD();
            break;
        case 4:
            getE();
            break;
        case 5:
            getF();
            break;
        case 6:
            getG();
            break;
        }
    }

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
