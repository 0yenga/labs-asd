import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Line2D;

public class Main {

    // Параметры рекурсивного построения листа папоротника.
    static final int BRANCHES = 12;      // число точек вдоль главного стебля
    static final double CURVE = -1.2;    // насколько стебель загибается к концу
    static final double SPREAD = 0.8;    // угол боковых листочков
    static final double RATIO = 0.35;    // длина бокового листочка от длины стебля

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createWindow();
            }
        });
    }

    static void createWindow() {
        JFrame frame = new JFrame("Папоротник: рекурсивное рисование");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        final FernPanel panel = new FernPanel(4);
        final JSpinner spinner = new JSpinner(new SpinnerNumberModel(4, 1, 4, 1));
        spinner.addChangeListener(e -> panel.setDepth((Integer) spinner.getValue()));

        JPanel top = new JPanel();
        top.add(new JLabel("Глубина рекурсии:"));
        top.add(spinner);

        frame.add(top, BorderLayout.NORTH);
        frame.add(panel, BorderLayout.CENTER);
        frame.setSize(800, 800);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    static class FernPanel extends JPanel {
        private int depth;

        FernPanel(int depth) {
            this.depth = depth;
            setBackground(new Color(235, 245, 235));
        }

        void setDepth(int depth) {
            this.depth = depth;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(20, 90, 20));
            g2.setStroke(new BasicStroke(1.2f));

            double baseX = getWidth() / 2.0;
            double baseY = getHeight() * 0.95;
            double length = getHeight() * 0.72;
            drawFrond(g2, baseX, baseY, Math.PI / 2, length, depth);
        }
    }

    // Рекурсивно рисует один лист папоротника.
    // Стебель рисуется отрезками, в точках вдоль него выпускаются
    // боковые листочки — такие же листья, но меньшего размера.
    static void drawFrond(Graphics2D g, double x, double y,
                          double angle, double length, int depth) {
        if (depth <= 0) {
            double x2 = x + length * 0.6 * Math.cos(angle);
            double y2 = y - length * 0.6 * Math.sin(angle);
            g.draw(new Line2D.Double(x, y, x2, y2));
            return;
        }

        double step = length / BRANCHES;
        double px = x;
        double py = y;

        for (int i = 0; i < BRANCHES; i++) {
            double t = (double) i / (BRANCHES - 1);
            double currentAngle = angle + CURVE * t;

            double nx = px + step * Math.cos(currentAngle);
            double ny = py - step * Math.sin(currentAngle);
            g.draw(new Line2D.Double(px, py, nx, ny));

            // У основания и у самого кончика боковые листочки не нужны.
            if (i > 0 && i < BRANCHES - 1) {
                double pinnaLength = length * RATIO * (1 - t);
                drawFrond(g, nx, ny, currentAngle + SPREAD, pinnaLength, depth - 1);
                drawFrond(g, nx, ny, currentAngle - SPREAD, pinnaLength, depth - 1);
            }

            px = nx;
            py = ny;
        }
    }
}
