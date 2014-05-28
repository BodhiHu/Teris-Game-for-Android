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
    }

    int mColor;
    private int mWidth;
    private int mHeight;
    int mTetrimino[];
    Tetrimino mT;

    Tetriminos(Tetrimino t) {
        mT = t;
        switch (t) {
            case I:
                mColor = Color.BLUE;
                mWidth = 1;
                mHeight = 4;
                mTetrimino = new int[] {
                        1,
                        1,
                        1,
                        1,
                };
                break;
            case J:
                mColor = Color.CYAN;
                mWidth = 2;
                mHeight = 3;
                mTetrimino = new int[] {
                        0, 1,
                        0, 1,
                        1, 1,
                };
                break;
            case L:
                mColor = Color.DKGRAY;
                mWidth = 2;
                mHeight = 3;
                mTetrimino = new int[] {
                        1, 0,
                        1, 0,
                        1, 1,
                };
                break;
            case O:
                mColor = Color.GREEN;
                mWidth = 2;
                mHeight = 2;
                mTetrimino = new int[] {
                        1, 1,
                        1, 1,
                };
                break;
            case S:
                mColor = Color.MAGENTA;
                mWidth = 2;
                mHeight = 3;
                mTetrimino = new int[] {
                        1, 0,
                        1, 1,
                        0, 1,
                };
                break;
            case T:
                mColor = Color.YELLOW;
                mWidth = 3;
                mHeight = 2;
                mTetrimino = new int[] {
                        1, 1, 1,
                        0, 1, 0,
                };
                break;
            case Z:
                mColor = Color.RED;
                mWidth = 3;
                mHeight = 2;
                mTetrimino = new int[] {
                        1, 1, 0,
                        0, 1, 1,
                };
                break;
        }
    }

    static void rotateTeriminoClockWise90(Tetriminos t) {
        int newArr[] = new int[t.mWidth * t.mHeight];
        int newWidth = t.mHeight;
        int newHeight = t.mWidth;
        //After 90 degrees clockwise rotation:
        //  Row i, Col j => Row j, Col (newWidth - 1 - i)
        for (int i = 0; i < t.mHeight; i++) {
            for (int j = 0; j < t.mWidth; j++) {
                newArr[j*newWidth + (newWidth - 1 - i)] = t.mTetrimino[j + i*t.mWidth];
            }
        }

        t.mTetrimino = newArr;
        t.mWidth = newWidth;
        t.mHeight = newHeight;
    }

}
