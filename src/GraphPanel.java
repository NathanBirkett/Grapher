import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class GraphPanel extends JPanel implements Runnable {
    int size = 4;
    Thread thread;
    Scanner scanner = new Scanner(System.in);
    String str = "x";
    int pointX,pointY;
    int scale;
    double a, h, c, k, w, u, v;
    double h1, h2, k1, k2;
    double b;
    Color color;
    ArrayList<Point> points = new ArrayList<>();
    ArrayList<double[]> lines = new ArrayList<>(Arrays.asList(new double[]{0.0}, new double[]{0.0}));
    int meanX, meanY;
    double meanXY, meanX2, slope, yIntercept;
    Type type;
    boolean drawing;
    double intersect;

    MouseHandler mh = new MouseHandler();

    public GraphPanel() {
        setPreferredSize(new Dimension(500,500));
        setBackground(Color.white);
        addMouseWheelListener(mh);
    }

    public void startThread() {
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        System.out.println(scale);
        while (thread != null) {
            System.out.println("enter: ");
            str = scanner.nextLine();
            if ("+".equals(str)) {
                size += 2;
            } else if ("-".equals(str)) {
                size += -2;
            } else if (str.contains(",")) {
                getPoints();
                type = Type.POINT;
                drawing = true;
            } else if (str.contains("x^2")) {
                handlePolynomial();
                type = Type.QUADRATIC;
                drawing = true;
            } else if (str.contains("^")) {
                convertQuadratic();
                type = Type.QUADRATIC;
                drawing = true;
            } else if (str.contains("x")) {
                convertLinear();
                type = Type.LINEAR;
                drawing = true;
            } else if (str.contains("color: ")) {
                handleColor();
            } else if (str.contains("fit")) {
                lineOfBestFit();
            } else if (str.contains("intersect")) {
                findIntersect();
            } else if (str.contains("erase")) {
                erase();
            } else {
                System.out.println("error");
            }
            scale = mh.wheelAmount;
            repaint();
        }
    }

    public void getPoints() {
        str = str.substring(1, str.length()-1);
        pointX = Integer.parseInt(str.split(",")[0]);
        pointY = Integer.parseInt(str.split(",")[1]);
        points.add(new Point(pointX,pointY));
    }

    public int convertX(double x) {
        return (int)(x+getWidth()/2);
    }

    public int convertY(double y) {
        return (int)(-y+getHeight()/2);
    }

    public void handlePolynomial() {
        //ax^2+bx+c
        //a(x+h)^2+k
        a = Double.parseDouble(str.substring(0,str.indexOf("x^2")));
        b = Double.parseDouble(str.substring(str.indexOf("x^2")+3, str.indexOf("x", str.indexOf("x")+1)));
        System.out.println(b);
        c = Double.parseDouble(str.substring(str.indexOf("x", str.indexOf("x")+1)+1));
        h = -b/(2*a);
        k = a*Math.pow(h,2) + b*h + c;
        lines.add(new double[]{a,b,c});
        if (lines.size() > 2) {
            lines.remove(0);
        }
    }

    public void convertLinear() {
        //ax+b
        a = Double.parseDouble(str.substring(0,str.indexOf('x')));
        if (str.contains("+")) {
            b = (int)Math.round(Double.parseDouble(str.substring(str.indexOf('+')+1)));
        } else {
            b = (int)Math.round(Double.parseDouble(str.substring(str.indexOf("x-")+1)));
        }
        lines.add(new double[]{0.0, a, b});
        if (lines.size() > 2) {
            lines.remove(0);
        }
        System.out.println(Arrays.toString(lines.get(0)));
    }

    public void findIntersect() {
        System.out.println(Arrays.toString(lines.get(0)) + Arrays.toString(lines.get(1)));
        if (lines.get(0).length == 2 && lines.get(1).length == 2) {
            //ax+b, vx+w
            a = lines.get(0)[0];
            b = (int) lines.get(0)[1];
            w = lines.get(1)[0];
            v = (int) lines.get(1)[1];
            intersect = ((v-b)/(a-w));
            System.out.println("("+intersect+","+(a*intersect+b)+")");
        } else if (lines.get(0).length == 3 || lines.get(1).length == 3) {
            //ax^2+bx+c
            a = lines.get(0)[0] - lines.get(1)[0];
            b = lines.get(0)[1] - lines.get(1)[1];
            c = lines.get(0)[2] - lines.get(1)[2];
            h1 = (-b+Math.sqrt(Math.pow(b,2) - 4*a*c))/(2*a);
            h2 = (-b-Math.sqrt(Math.pow(b,2) - 4*a*c))/(2*a);
            k1 = lines.get(0)[0]*Math.pow(h1,2) + lines.get(0)[1]*h1 + lines.get(0)[2];
            k2 = lines.get(0)[0]*Math.pow(h2,2) + lines.get(0)[1]*h2 + lines.get(0)[2];
            System.out.println("("+h1+","+k1+"), ("+h2+","+k2+")");
        }
    }

    public void convertQuadratic() {
        //a(x-h)^2+k
        a = Double.parseDouble(str.substring(0,str.indexOf("(")));
        h = Double.parseDouble(str.substring(str.indexOf("x")+1, str.indexOf(")")));
        k = Double.parseDouble(str.substring(str.indexOf("^")+2));
        System.out.println(a);
        System.out.println(h);
        System.out.println(k);
    }

    public void lineOfBestFit() {
        for (Point point : points) {
            meanX += point.getX();
            meanY += point.getY();
        }
        meanX = meanX/points.size();
        meanY = meanY/points.size();
        for (Point point : points) {
            meanXY += (point.getX() - meanX)*(point.getY() - meanY);
            meanX2 += Math.pow(point.getX() - meanX, 2);
        }
        slope = meanXY/meanX2;
        yIntercept = meanY - slope*meanX;
        str = (yIntercept >= 0) ? Math.round(slope)+"x+"+Math.round(yIntercept) : Math.round(slope)+"x"+Math.round(yIntercept);
        str = (yIntercept >= 0) ? slope+"x+"+yIntercept : slope+"x"+yIntercept;
        System.out.println(str);
        convertLinear();
        type = Type.LINEAR;
        drawing = true;
    }

    public void handleColor() {
        str = str.substring(7);
        System.out.println(str);
        switch (str) {
            case "red":
                color = Color.red;
                break;
            case "blue":
                color = Color.blue;
                break;
            case "green":
                color = Color.green;
                break;
            case "white":
                color = Color.white;
                break;
        }
    }

    public void erase() {
        color = Color.white;
        a = lines.get(1)[0];
        b = lines.get(1)[1];
        c = lines.get(1)[2];
        str = a + "x^2" + b + "x" + c;
        handlePolynomial();
        type = Type.QUADRATIC;
        drawing = true;
        lines.remove(1);
    }

    public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D)g;
        drawAxes(g2d);
        g2d.setColor(color);
        if (drawing) {
            if (type == Type.POINT) {
                drawPoint(g2d);
            } else if (type == Type.LINEAR) {
                drawLinear(g2d);
            } else if (type == Type.QUADRATIC) {
                drawQuadratic(g2d);
            }
        }
        if (color == Color.white) {
            drawAxes(g2d);
        }
    }

    public void drawAxes(Graphics2D g2d) {
        g2d.setColor(Color.black);
        g2d.fillRect(getWidth()/2-1, 0, 2,getHeight());
        g2d.fillRect(0,getHeight()/2-1, getWidth(), 2);
    }

    public void drawPoint(Graphics2D g2d) {
        g2d.fillOval(convertX(pointX)-1, convertY(pointY)-1, 3,3);
        drawing = false;
    }

    public void drawLinear(Graphics2D g2d) {
        g2d.drawLine(0,(int)((a+1)*getHeight()/2-b), getWidth(), (int)(-(a-1)*getHeight()/2-b));
        drawing = false;
    }

    public void drawQuadratic(Graphics2D g2d) {
        for (int i=-getWidth()/2; i<getWidth()/2; i++) {
            g2d.fillOval(convertX(i), convertY(a*Math.pow(i-h,2)+k)-1,1,2);
        }
        for (int i=-getHeight()/2; i<getHeight()/2; i++) {
            g2d.fillOval(convertX(Math.sqrt((i-k)/a)+h), convertY(i)-1,1,2);
            g2d.fillOval(convertX(-Math.sqrt((i-k)/a)+h), convertY(i)-1,1,2);
        }
        drawing = false;
    }
}
