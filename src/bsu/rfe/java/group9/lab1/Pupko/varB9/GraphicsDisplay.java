package bsu.rfe.java.group9.lab1.Pupko.varB9;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class GraphicsDisplay extends JPanel {

    private Double[][] graphicsData;

    private boolean showAxis = true;
    private boolean showMarkers = true;
    private boolean showGrid = true;

    private double minX;
    private double maxX;
    private double minY;
    private double maxY;

    private double scale;

    private final BasicStroke graphicsStroke;
    private final BasicStroke axisStroke;
    private final BasicStroke markerStroke;
    private final BasicStroke gridStroke;

    private final Font axisFont;

    public GraphicsDisplay(){

        setBackground(Color.WHITE);

        graphicsStroke = new BasicStroke(2.0f, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_ROUND, 10.0f,null,0.0f);

        axisStroke = new BasicStroke(4.0f, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_MITER, 10.0f,new float[] {14, 4, 4, 4, 9, 4,4,4},0.0f);

        markerStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_MITER, 10.0f,null,0.0f);

        gridStroke = new BasicStroke(3.0f,BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND, 10.0f,null,0.0f);

        axisFont = new Font("Serif", Font.BOLD, 36);
    }

    public void showGraphics(Double[][] graphicsData){

        this.graphicsData = graphicsData;//Все еще готовлюсь(потому что код почти и готов так то)

        repaint();
    }

    public void setShowAxis(boolean showAxis){
        this.showAxis = showAxis;
        repaint();
    }

    public void setShowGrid(boolean showGrid){
        this.showGrid = showGrid;
        repaint();
    }

    public void setShowMarkers(boolean showMarkers){
        this.showMarkers = showMarkers;
        repaint();
    }

    public void paintComponent(Graphics g){

        super.paintComponent(g);

        if(graphicsData == null || graphicsData.length == 0) return;

        minX = graphicsData[0][0];
        maxX = graphicsData[graphicsData.length-1][0];
        minY = graphicsData[0][1];
        maxY = minY;

        for (int i = 1; i < graphicsData.length; i++){
            if(graphicsData[i][1] < minY)
                minY = graphicsData[i][1];
            if(graphicsData[i][1] > maxY)
                maxY = graphicsData[i][1];
        }

        double scaleX = getSize().getWidth() / (maxX - minX);
        double scaleY = getSize().getHeight() / (maxY - minY);

        scale = Math.min(scaleX, scaleY);

        if(scale == scaleX){

            double yIncrement = (getSize().getHeight()/scale - (maxY - minY)) /2;
            maxY += yIncrement;
            minY -=yIncrement;
        }

        if(scale == scaleY){

            double xIncrement = (getSize().getWidth()/scale - (maxX - minX)) /2;
            maxX += xIncrement;
            minX -=xIncrement;
        }

        Graphics2D canvas = (Graphics2D) g;
        Stroke oldStroke = canvas.getStroke();
        Color oldColor = canvas.getColor();
        Paint oldPaint = canvas.getPaint();
        Font oldFont = canvas.getFont();

        if(showAxis) paintAxis(canvas);

        paintGraphics(canvas);

        if(showGrid) paintGrid(canvas);


        if(showMarkers) paintMarkers(canvas);

        canvas.setFont(oldFont);
        canvas.setPaint(oldPaint);
        canvas.setColor(oldColor);
        canvas.setStroke(oldStroke);
    }

    protected void paintGraphics(Graphics2D canvas){

        canvas.setStroke(graphicsStroke);
        canvas.setColor(Color.RED);

        GeneralPath graphics = new GeneralPath();
        for (int i = 0; i < graphicsData.length; i++){
            Point2D.Double point = xyToPoint(graphicsData[i][0],
                    graphicsData[i][1]);

            if(i>0)
                graphics.lineTo(point.getX(), point.getY());
            else
                graphics.moveTo(point.getX(), point.getY());
        }

        canvas.draw(graphics);
    }

    protected void paintGrid(Graphics2D canvas){

        canvas.setStroke(gridStroke);

        int tempY = 0;
        for(double i = minY; i < maxY-0.5f; i += (maxY-minY)/100){

            tempY++;
            if(tempY % 5 == 0) {

                canvas.setPaint(Color.GREEN);
                canvas.draw(new Line2D.Double(xyToPoint(0.05f, i),
                        xyToPoint(0.4f, i)));
            }else {

                canvas.setPaint(Color.YELLOW);
                canvas.draw(new Line2D.Double(xyToPoint(0.05f, i),
                        xyToPoint(0.2f, i)));
            }
        }

        int tempX = 0;
        for(double i = minX; i < maxX-0.5f; i += (maxX-minX)/100){
            if(i==maxX/2)
            {

            }
            tempX++;
            if(tempX % 5 == 0) {

                canvas.setPaint(Color.GREEN);
                canvas.draw(new Line2D.Double(xyToPoint(i, 0.05f),
                        xyToPoint(i, 0.5f)));
            }else {

                canvas.setPaint(Color.YELLOW);
                canvas.draw(new Line2D.Double(xyToPoint(i, 0.05f),
                        xyToPoint(i, 0.2f)));
            }
        }
    }

    protected void paintMarkers(Graphics2D canvas){

        canvas.setStroke(markerStroke);
        canvas.setColor(Color.BLACK);
        canvas.setPaint(Color.BLACK);

        for(Double[] point: graphicsData){

            if((point[1]+0.1) % 1 == 0 || (point[1]-0.1) % 1 == 0)
                canvas.setColor(Color.blue);
            else
                canvas.setColor(Color.BLACK);

            Rectangle2D.Double marker = new Rectangle2D.Double();

            Point2D.Double center = xyToPoint(point[0],point[1]);
            Point2D.Double corner = shiftPoint(center, 5.5, 5.5);

            marker.setFrameFromCenter(center, corner);
            canvas.draw(marker);

            GeneralPath markerPath = new GeneralPath();

            markerPath.moveTo(corner.getX(), corner.getY());

            markerPath.lineTo(markerPath.getCurrentPoint().getX() - 11,
                    markerPath.getCurrentPoint().getY() - 11);

            markerPath.moveTo(markerPath.getCurrentPoint().getX()+11,
                    markerPath.getCurrentPoint().getY());

            markerPath.lineTo(markerPath.getCurrentPoint().getX() - 11,
                    markerPath.getCurrentPoint().getY() + 11);

            markerPath.closePath();
            canvas.draw(markerPath);
        }
    }

    protected void paintAxis(Graphics2D canvas){

        canvas.setStroke(axisStroke);
        canvas.setColor(Color.BLACK);
        canvas.setPaint(Color.BLACK);
        canvas.setFont(axisFont);

        FontRenderContext context = canvas.getFontRenderContext();

        if(minX <= 0.0 && maxX >= 0.0){

            canvas.draw(new Line2D.Double(xyToPoint(0,maxY),
                    xyToPoint(0, minY)));

            canvas.setStroke(gridStroke);

            GeneralPath arrow = new GeneralPath();

            Point2D.Double lineEnd = xyToPoint(0, maxY);
            arrow.moveTo(lineEnd.getX(), lineEnd.getY());

            arrow.lineTo(arrow.getCurrentPoint().getX() + 5,
                    arrow.getCurrentPoint().getY() + 20);

            arrow.lineTo(arrow.getCurrentPoint().getX() - 10,
                    arrow.getCurrentPoint().getY());

            arrow.closePath();
            canvas.draw(arrow);
            canvas.fill(arrow);

            Rectangle2D bounds = axisFont.getStringBounds("y", context);
            Point2D.Double labelPos = xyToPoint(0, maxY);

            canvas.drawString("y", (float)labelPos.getX() + 10,
                    (float)(labelPos.getY() - bounds.getY()));
        }

        if(minY <= 0.0 && maxY >= 0.0){

            canvas.setStroke(axisStroke);
            canvas.draw(new Line2D.Double(xyToPoint(minX,0),
                    xyToPoint(maxX, 0)));

            canvas.setStroke(gridStroke);

            GeneralPath arrow = new GeneralPath();

            Point2D.Double lineEnd = xyToPoint(maxX, 0);
            arrow.moveTo(lineEnd.getX(), lineEnd.getY());

            arrow.lineTo(arrow.getCurrentPoint().getX() - 20,
                    arrow.getCurrentPoint().getY() - 5);

            arrow.lineTo(arrow.getCurrentPoint().getX(),
                    arrow.getCurrentPoint().getY() + 10);

            arrow.closePath();
            canvas.draw(arrow);
            canvas.fill(arrow);

            Rectangle2D bounds = axisFont.getStringBounds("x", context);
            Point2D.Double labelPos = xyToPoint(maxX, 0);

            canvas.drawString("x", (float)(labelPos.getX() - bounds.getWidth() - 10),
                    (float)(labelPos.getY() + bounds.getY()));
        }
    }

    protected Point2D.Double xyToPoint(double x, double y){

        double deltaX = x - minX;
        double deltaY = maxY - y;

        return new Point2D.Double(deltaX*scale, deltaY*scale);
    }

    protected Point2D.Double shiftPoint(Point2D.Double src, double deltaX, double deltaY){

        Point2D.Double dest = new Point2D.Double();
        dest.setLocation(src.getX() + deltaX, src.getY() + deltaY);

        return dest;
    }
}
