import edu.princeton.cs.algs4.Picture;


public class SeamCarver
{

    private int[][] picture;
    private double[][] energy;
    private int width;
    private int height;

    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture)
    {
        if (picture == null) throw new IllegalArgumentException();
        width = picture.width();
        height = picture.height();
        energy = new double[width][height];
        this.picture = new int[width][height];

        for (int i = 0; i < width(); i++)
        {
            for (int j = 0; j < height(); j++)
                this.picture[i][j] = picture.getRGB(i, j);
        }

        for (int i = 0; i < width(); i++)
        {
            for (int j = 0; j < height(); j++)
                energy[i][j] = computeEnergy(i, j);
        }
    }


    public Picture picture()                          // current picture
    {
        Picture pic = new Picture(width, height);
        for (int i = 0; i < width; i++)
            for (int j = 0; j < height; j++)
                pic.setRGB(i, j, picture[i][j]);
        return pic;
    }

    public int width()
    {
        return width;
    }

    public int height()
    {
        return height;
    }

    public double energy(int x, int y)               // energy of pixel at column x and row y
    {
        if (x < 0 || x > width - 1 || y < 0 || y > height - 1) throw new IllegalArgumentException();
        return energy[x][y];
    }

    private void relaxvertical(double[][] distTo, int[][] edgeTo, int x, int y)
    {
        // down
        if (distTo[x][y + 1] > distTo[x][y] + energy[x][y + 1])
        {
            distTo[x][y + 1] = distTo[x][y] + energy[x][y + 1];
            edgeTo[x][y + 1] = x;
        }
        // down left
        if (x > 0 && distTo[x - 1][y + 1] > distTo[x][y] + energy[x - 1][y + 1])
        {
            distTo[x - 1][y + 1] = distTo[x][y] + energy[x - 1][y + 1];
            edgeTo[x - 1][y + 1] = x;
        }
        // down right
        if (x < width() - 1 && distTo[x + 1][y + 1] > distTo[x][y] + energy[x + 1][y + 1])
        {
            distTo[x + 1][y + 1] = distTo[x][y] + energy[x + 1][y + 1];
            edgeTo[x + 1][y + 1] = x;
        }
    }

    private void transpose()
    {
        int temp = width;
        width = height;
        height = temp;

        double[][] energy2 = new double[width][height];
        int[][] picture2 = new int[width][height];

        for (int i = 0; i < width; i++)
        {
            for (int j = 0; j < height; j++)
            {
                energy2[i][j] = energy[j][i];
                picture2[i][j] = picture[j][i];
            }
        }
        energy = energy2;
        picture = picture2;
    }


    public int[] findVerticalSeam()
    {
        int[] seam = new int[height];
        double[][] distTo = new double[width][height];
        int[][] edgeTo = new int[width][height];

        for (int i = 0; i < width; i++)
        {
            for (int j = 0; j < height; j++)
            {
                if (j == 0) distTo[i][j] = energy[i][j];
                else distTo[i][j] = Double.POSITIVE_INFINITY;
            }
        }

        for (int j = 0; j < height - 1; j++)
        {
            for (int i = 0; i < width; i++)
            {
                relaxvertical(distTo, edgeTo, i, j);
            }
        }

        // 找到底部的最小值
        double min = Double.POSITIVE_INFINITY;
        int minIndex = 0;
        for (int i = 0; i < width; i++)
        {
            if (distTo[i][height - 1] < min)
            {
                min = distTo[i][height - 1];
                minIndex = i;
            }
        }

        // 从底部往上找到最小路径
        seam[height - 1] = minIndex;
        for (int j = height - 2; j >= 0; j--)
        {
            seam[j] = edgeTo[seam[j + 1]][j + 1];
        }
        return seam;
    }

    public void removeVerticalSeam(int[] seam)
    {
        transpose();
        removeHorizontalSeam(seam);
        transpose();
    }

    public int[] findHorizontalSeam()
    {
        transpose();
        int[] array = findVerticalSeam();
        transpose();
        return array;
    }

    public void removeHorizontalSeam(int[] seam)
    {
        checkSeam(seam);
        int min = Integer.MAX_VALUE;
        int max = 0;
        for (int i = 0; i < width; i++)
        {
            if (seam[i] > max) max = seam[i];
            if (seam[i] < min) min = seam[i];
            for (int j = seam[i]; j < height - 1; j++)
            {
                picture[i][j] = picture[i][j + 1];
            }
        }

        height--;
        if (min > 0) min--;
        if (max > height - 1) max = height - 1;

        for (int i = 0; i < width; i++)
        {
            for (int j = min; j <= max; j++)
                energy[i][j] = computeEnergy(i, j);
            for (int j = max + 1; j < height - 1; j++)
                energy[i][j] = energy[i][j + 1];
        }
    }

    private void checkSeam(int[] seam)
    {
        if (height <= 1 || seam == null || seam.length != width)
            throw new IllegalArgumentException();
        for (int i = 0; i < width; i++)
        {
            if (seam[i] < 0 || seam[i] > height - 1) throw new IllegalArgumentException();
            if (i > 0 && Math.abs(seam[i] - seam[i - 1]) > 1) throw new IllegalArgumentException();
        }
    }

    private double computeEnergy(int x, int y)
    {
        if (x == 0 || x == width() - 1 || y == 0 || y == height() - 1) return 1000.0;

        int rgbUp = picture[x][y - 1];
        int rgbDown = picture[x][y + 1];
        int rgbLeft = picture[x - 1][y];
        int rgbRight = picture[x + 1][y];

        double rx = Math.pow(((rgbLeft >> 16) & 0xFF) - ((rgbRight >> 16) & 0xFF), 2);
        double gx = Math.pow(((rgbLeft >> 8) & 0xFF) - ((rgbRight >> 8) & 0xFF), 2);
        double bx = Math.pow(((rgbLeft) & 0xFF) - ((rgbRight) & 0xFF), 2);

        double ry = Math.pow(((rgbUp >> 16) & 0xFF) - ((rgbDown >> 16) & 0xFF), 2);
        double gy = Math.pow(((rgbUp >> 8) & 0xFF) - ((rgbDown >> 8) & 0xFF), 2);
        double by = Math.pow(((rgbUp) & 0xFF) - ((rgbDown) & 0xFF), 2);

        return Math.sqrt(rx + gx + bx + ry + gy + by);

    }

    public static void main(String[] args)
    {

    }
}