package com.google.engedu.puzzle8;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;

import java.util.ArrayList;


public class PuzzleBoard {
       int steps;
       PuzzleBoard previousBoard;
       private static final int NUM_TILES = 3;
    private static final int[][] NEIGHBOUR_COORDS = {
            { -1, 0 },
            { 1, 0 },
            { 0, -1 },
            { 0, 1 }
    };
    private ArrayList<PuzzleTile> tiles;

    PuzzleBoard(Bitmap bitmap, int parentWidth) {
        tiles =new ArrayList<PuzzleTile>();
        int x,y,no=0;
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, parentWidth, parentWidth, true);
        for(y=0;y<3;y++)
            for( x=0;x<3;x++) {

                Bitmap map = Bitmap.createBitmap(resizedBitmap, x * parentWidth / 3, y * parentWidth / 3, parentWidth / NUM_TILES, parentWidth / NUM_TILES);
                PuzzleTile tile;
                if (no == 8)
                    { tiles.add(null); break;
                } else {
                    tile = new PuzzleTile(map, no);
                    tiles.add(tile);
                    no++;
                }


            }
    }

    PuzzleBoard(PuzzleBoard otherBoard,int steps) {
        previousBoard=otherBoard;
        tiles = (ArrayList<PuzzleTile>) otherBoard.tiles.clone();
        this.steps=steps+1;
    }

    public void reset() {
        // Nothing for now but you may have things to reset once you implement the solver.
    }

    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;
        return tiles.equals(((PuzzleBoard) o).tiles);
    }

    public void draw(Canvas canvas) {
        if (tiles == null) {
            return;
        }
        for (int i = 0; i < NUM_TILES * NUM_TILES; i++) {
            PuzzleTile tile = tiles.get(i);
            if (tile != null) {
                tile.draw(canvas, i % NUM_TILES, i / NUM_TILES);
            }
        }
    }

    public boolean click(float x, float y) {
        for (int i = 0; i < NUM_TILES * NUM_TILES; i++) {
            PuzzleTile tile = tiles.get(i);
            if (tile != null) {
                if (tile.isClicked(x, y, i % NUM_TILES, i / NUM_TILES)) {
                    return tryMoving(i % NUM_TILES, i / NUM_TILES);
                }
            }
        }
        return false;
    }

    public boolean resolved() {
        for (int i = 0; i < NUM_TILES * NUM_TILES - 1; i++) {
            PuzzleTile tile = tiles.get(i);
            if (tile == null || tile.getNumber() != i)
                return false;
        }
        return true;
    }

    private int XYtoIndex(int x, int y) {
        Log.d("integer",Integer.toString(x + (y) * NUM_TILES));
        return x + y * NUM_TILES;
    }

    protected void swapTiles(int i, int j) {
        PuzzleTile temp = tiles.get(i);
        tiles.set(i, tiles.get(j));
        tiles.set(j, temp);
    }

    private boolean tryMoving(int tileX, int tileY) {
        for (int[] delta : NEIGHBOUR_COORDS) {
            int nullX = tileX + delta[0];
            int nullY = tileY + delta[1];
            if (nullX >= 0 && nullX < NUM_TILES && nullY >= 0 && nullY < NUM_TILES &&
                    tiles.get(XYtoIndex(nullX, nullY)) == null) {
                swapTiles(XYtoIndex(nullX, nullY), XYtoIndex(tileX, tileY));
                return true;
            }

        }
        return false;
    }
    public void setPreviousBoard(PuzzleBoard previousBoard) {
        this.previousBoard = previousBoard;
    }

    public ArrayList<PuzzleBoard> neighbours()
    {  int X=0,Y=0,i;

        ArrayList<PuzzleBoard> neighbours=new ArrayList<>();
        for(i=0;i<=8;++i)
        {
            if(tiles.get(i)==null) {
                X = i % NUM_TILES;
                Y = i / NUM_TILES;
            }

        }

        for(int[] delta:NEIGHBOUR_COORDS)
        {
            int tempX=X+delta[0];
            int tempY=Y+delta[1];

            if(tempX>=0 && tempX<=2 && tempY>=0 && tempY<=2 )
            {
                PuzzleBoard newBoard=new PuzzleBoard(this,steps);
                newBoard.swapTiles(XYtoIndex(X,Y),XYtoIndex(tempX,tempY));

               // XYtoIndex(tempX,tempY);
                neighbours.add(newBoard);

            }

        }

        return neighbours;
    }

    public int priority()
    { int distance=0;
        for(int i=0;i<NUM_TILES*NUM_TILES;++i)
        {
            PuzzleTile tile=tiles.get(i);
            if(tile!=null)
            {
                int correctPos=tile.getNumber();
                int correctX= correctPos%NUM_TILES;
                int correctY=correctPos%NUM_TILES;
                int currentX= i%NUM_TILES;
                int currentY=i/NUM_TILES;
                distance=distance+(Math.abs(correctX-currentX)+Math.abs(correctY-currentY));
            }
        }


        return distance+steps;
    }

    public PuzzleBoard getPreviousBoard() {
        return previousBoard;
    }

}
