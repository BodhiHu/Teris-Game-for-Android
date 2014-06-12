package com.shawnhu.mechanicsgames.tetris;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;

/**
 * TileView: a View-variant designed for handling arrays of "icons" or other drawables.
 * 
 */
public class TileView extends View {

    static TileViewInfo mTileViewInfo = new TileViewInfo();
    static class TileViewInfo implements Parcelable {

        static int mTileSize;
        static int mXTileCount;
        static int mYTileCount;
        static int mTileType[][];
        // color OR bitmap index;
        static int mTileInfo[][];
        static Bitmap[] mBmpArray;
        static boolean restoreTileView = false;

        private TileViewInfo() {
        }
        public int describeContents() {
            return 0;
        }
        public void writeToParcel(Parcel out, int flags) {
            int typearray[] = new int[mXTileCount * mYTileCount];
            int infoarray[] = new int[mXTileCount * mYTileCount];
            out.writeInt(mXTileCount);
            out.writeInt(mYTileCount);
            for (int i = 0; i < mYTileCount; i++) {
                for (int j = 0; j < mXTileCount; j++) {
                    typearray[j + i*mXTileCount] = mTileType[i][j];
                    infoarray[j + i*mXTileCount] = mTileInfo[i][j];
                }
            }
            out.writeIntArray(typearray);
            out.writeIntArray(infoarray);
            out.writeArray(mBmpArray);
        }
        public static final Parcelable.Creator<TileViewInfo> CREATOR
                = new Parcelable.Creator<TileViewInfo> () {
            public TileViewInfo createFromParcel(Parcel in) {
                return new TileViewInfo(in);
            }

            public TileViewInfo[] newArray(int size) {
                return null;
            }
        };

        private TileViewInfo(Parcel in) {
            mXTileCount = in.readInt();
            mYTileCount = in.readInt();
            int typearray[] = new int[mXTileCount * mYTileCount];
            int infoarray[] = new int[mXTileCount * mYTileCount];
            in.readIntArray(typearray);
            in.readIntArray(infoarray);
            mTileType = new int[mXTileCount][mYTileCount];
            mTileInfo = new int[mXTileCount][mYTileCount];
            for (int i = 0; i < mYTileCount; i++) {
                for (int j = 0; j < mXTileCount; j++) {
                    mTileType[i][j] = typearray[j + i*mXTileCount];
                    mTileInfo[i][j] = infoarray[j + i*mXTileCount];
                }
            }
            //TODO: can this work?
            mBmpArray = (Bitmap[]) in.readArray(ClassLoader.getSystemClassLoader());

            restoreTileView = true;
        }

    }

    static final int PADDING = 2;



    private static int mXOffset;
    private static int mYOffset;

    private final Paint mPaint = new Paint();
    private final int mPaintOrigColor = mPaint.getColor();

    static final int EMPTY  = 0;
    static final int COLOR = 1 << 0;
    static final int BMP   = 1 << 1;

    public TileView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TileView);
        //TileViewInfo.mTileSize = a.getDimensionPixelSize(R.styleable.TileView_tileSize, 12);
        TileViewInfo.mTileSize = a.getDimensionPixelSize(R.styleable.TileView_tileSize, 36);

        a.recycle();
    }

    public TileView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TileView);
        TileViewInfo.mTileSize = a.getDimensionPixelSize(R.styleable.TileView_tileSize, 12);

        a.recycle();

    }


    /**
     * Resets all tiles to 0 (empty)
     * 
     */
    public void clearTiles() {
        for (int r = 0; r < TileViewInfo.mYTileCount; r++) {
            for (int c = 0; c < TileViewInfo.mXTileCount; c++) {
                setTileEmpty(r, c);
            }
        }
    }

    /**
     * Function to set the specified Drawable as the tile for a particular integer key.
     *
     * @param key
     * @param drawable
     */
    public void loadBmp(int key, Drawable drawable) {
        Bitmap bitmap = Bitmap.createBitmap(
                TileViewInfo.mTileSize, TileViewInfo.mTileSize, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, TileViewInfo.mTileSize, TileViewInfo.mTileSize);
        drawable.draw(canvas);

        TileViewInfo.mBmpArray[key] = bitmap;
    }

    protected Bitmap getSubTilesBitmap(int start_x, int start_y, int width, int height) {
        return null;
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int left, top, right, bottom;
        Rect rect;
        for (int x = 0; x < TileViewInfo.mXTileCount; x += 1) {
            for (int y = 0; y < TileViewInfo.mYTileCount; y += 1) {
                switch (TileViewInfo.mTileType[y][x]) {
                    case BMP:
                        mPaint.setColor(mPaintOrigColor);
                        if (TileViewInfo.mBmpArray[TileViewInfo.mTileInfo[y][x]] != null) {
                            canvas.drawBitmap(TileViewInfo.mBmpArray[TileViewInfo.mTileInfo[y][x]], mXOffset + x * TileViewInfo.mTileSize,
                                    mYOffset + y * TileViewInfo.mTileSize, mPaint);
                        }
                        break;
                    case COLOR:
                        left = mXOffset + x * TileViewInfo.mTileSize;
                        top = mYOffset + y * TileViewInfo.mTileSize;
                        right = left + TileViewInfo.mTileSize;
                        bottom = top + TileViewInfo.mTileSize;
                        rect = new Rect(left + PADDING, top + PADDING, right - PADDING, bottom - PADDING);
                        mPaint.setColor(TileViewInfo.mTileInfo[y][x]);
                        canvas.drawRect(rect, mPaint);
                        break;
                    case EMPTY:
                        left = mXOffset + x * TileViewInfo.mTileSize;
                        top = mYOffset + y * TileViewInfo.mTileSize;
                        right = left + TileViewInfo.mTileSize;
                        bottom = top + TileViewInfo.mTileSize;
                        rect = new Rect(left, top, right, bottom);
                        mPaint.setColor(Color.BLACK);
                        canvas.drawRect(rect, mPaint);
                        break;
                }
            }
        }

    }

    /**
     * Rests the internal array of Bitmaps used for drawing tiles, and sets the maximum index of
     * tiles to be inserted
     *
     * @param tilecount
     */

    public void resetBmps(int tilecount) {
        TileViewInfo.mBmpArray = new Bitmap[tilecount];
    }

    /**
     * Used to indicate that a particular tile (set with loadBmp and referenced by an integer)
     * should be drawn at the given x/y coordinates during the next invalidate/draw cycle.
     * 
     * @param bmpIndex
     * @param r
     * @param c
     */
    public void setTileBmp(int bmpIndex, int r, int c) {
        TileViewInfo.mTileType[r][c] = BMP;
        TileViewInfo.mTileInfo[r][c] = bmpIndex;
    }

    public void setTileColor(int color, int x, int y) {
        TileViewInfo.mTileType[x][y] = COLOR;
        TileViewInfo.mTileInfo[x][y] = color;
    }
    public void setTileEmpty(int x, int y) {
        TileViewInfo.mTileType[x][y] = EMPTY;
    }

    //TODO
    protected void restoreTileView() {
    }
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (TileViewInfo.restoreTileView) {
            restoreTileView();
            TileViewInfo.restoreTileView = false;
        } else {
            TileViewInfo.mXTileCount = (int) Math.floor(w / TileViewInfo.mTileSize);
            TileViewInfo.mYTileCount = (int) Math.floor(h / TileViewInfo.mTileSize);

            Tetriminos.setBounds(TileViewInfo.mXTileCount, TileViewInfo.mYTileCount);

            mXOffset = ((w - (TileViewInfo.mTileSize * TileViewInfo.mXTileCount)) / 2);
            mYOffset = ((h - (TileViewInfo.mTileSize * TileViewInfo.mYTileCount)) / 2);

            TileViewInfo.mTileInfo = new int[TileViewInfo.mYTileCount][TileViewInfo.mXTileCount];
            TileViewInfo.mTileType = new int[TileViewInfo.mYTileCount][TileViewInfo.mXTileCount];
            clearTiles();
        }
    }

}
