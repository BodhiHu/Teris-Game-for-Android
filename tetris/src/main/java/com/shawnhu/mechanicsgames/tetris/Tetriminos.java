package com.shawnhu.mechanicsgames.tetris;

import android.graphics.Color;

/**
 * Created by shawnhu on 5/27/14.
 */
public class Tetriminos {
    enum Tetrimino {
        I, J, L, O, S, T, Z;

        final static int totalTypes = 7;

        public static Tetrimino typeOfIdx(int i) {
            switch (i) {
                case 0:
                    return I;
                case 1:
                    return J;
                case 2:
                    return L;
                case 3:
                    return O;
                case 4:
                    return S;
                case 5:
                    return T;
                case 6:
                    return Z;
                default:
                    return Z;
            }
        }
        public static int idxOfType(Tetrimino t) {
            switch (t) {
                case I:
                    return 0;
                case J:
                    return 1;
                case L:
                    return 2;
                case O:
                    return 3;
                case S:
                    return 4;
                case T:
                    return 5;
                case Z:
                    return 6;
            }
        }
    }

    static int mColor;
    static int mWidth;
    static int mHeight;
    static public int mXTileNum;
    static public int mYTileNum;
    static public int mX;
    static public int mY;
    static int mTetrimino[][];
    static Tetrimino mT;

    Tetriminos(Tetrimino t) {
        mT = t;
        switch (t) {
            case I:
                mColor = Color.BLUE;
                mWidth = 1;
                mHeight = 4;
                mTetrimino = new int[][] {
                        {1},
                        {1},
                        {1},
                        {1},
                };
                break;
            case J:
                mColor = Color.CYAN;
                mWidth = 2;
                mHeight = 3;
                mTetrimino = new int[][] {
                        {0, 1},
                        {0, 1},
                        {1, 1},
                };
                break;
            case L:
                mColor = Color.DKGRAY;
                mWidth = 2;
                mHeight = 3;
                mTetrimino = new int[][] {
                        {1, 0},
                        {1, 0},
                        {1, 1},
                };
                break;
            case O:
                mColor = Color.GREEN;
                mWidth = 2;
                mHeight = 2;
                mTetrimino = new int[][] {
                        {1, 1},
                        {1, 1},
                };
                break;
            case S:
                mColor = Color.MAGENTA;
                mWidth = 2;
                mHeight = 3;
                mTetrimino = new int[][] {
                        {1, 0},
                        {1, 1},
                        {0, 1},
                };
                break;
            case T:
                mColor = Color.YELLOW;
                mWidth = 3;
                mHeight = 2;
                mTetrimino = new int[][] {
                        {1, 1, 1},
                        {0, 1, 0},
                };
                break;
            case Z:
                mColor = Color.RED;
                mWidth = 3;
                mHeight = 2;
                mTetrimino = new int[][] {
                        {1, 1, 0},
                        {0, 1, 1},
                };
                break;
        }
    }

    static boolean setCoordinate(int x, int y) {
        if (x < 0 || x > mXTileNum-1 ||
            y < 0 || y > mYTileNum-1) {
            return false;
        }

        mX = x;
        mY = y;

        return true;
    }

    static void rotateTeriminoClockWise90(Tetriminos t) {
        int newWidth = t.mHeight;
        int newHeight = t.mWidth;
        int newArr[][] = new int[newHeight][newWidth];
        //After 90 degrees clockwise rotation:
        //  Row i, Col j => Row j, Col (newWidth - 1 - i)
        for (int i = 0; i < t.mHeight; i++) {
            for (int j = 0; j < t.mWidth; j++) {
                newArr[j][newWidth - 1 - i] = t.mTetrimino[i][j];
            }
        }

        t.mTetrimino = newArr;
        t.mWidth = newWidth;
        t.mHeight = newHeight;
    }

}
