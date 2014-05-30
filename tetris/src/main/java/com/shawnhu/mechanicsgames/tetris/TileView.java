package com.shawnhu.mechanicsgames.tetris;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
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
        static boolean mTileNeedsRedraw[][];
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
            mTileNeedsRedraw = new boolean[mXTileCount][mYTileCount];
            for (int i = 0; i < mYTileCount; i++) {
                for (int j = 0; j < mXTileCount; j++) {
                    mTileType[i][j] = typearray[j + i*mXTileCount];
                    mTileInfo[i][j] = infoarray[j + i*mXTileCount];
                    mTileNeedsRedraw[i][j] = true;
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

    static final int COLOR = 0;
    static final int BMP = 1;

    public TileView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TileView);
        TileViewInfo.mTileSize = a.getDimensionPixelSize(R.styleable.TileView_tileSize, 12);

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
        for (int x = 0; x < TileViewInfo.mXTileCount; x++) {
            for (int y = 0; y < TileViewInfo.mYTileCount; y++) {
                setTileBmp(0, x, y);
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

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int x = 0; x < TileViewInfo.mXTileCount; x += 1) {
            for (int y = 0; y < TileViewInfo.mYTileCount; y += 1) {
                if (TileViewInfo.mTileNeedsRedraw[x][y]) {
                    switch (TileViewInfo.mTileType[x][y]) {
                        case BMP:
                            mPaint.setColor(mPaintOrigColor);
                            canvas.drawBitmap(TileViewInfo.mBmpArray[TileViewInfo.mTileInfo[x][y]], mXOffset + x * TileViewInfo.mTileSize,
                                mYOffset + y * TileViewInfo.mTileSize, mPaint);
                            break;
                        case COLOR:
                            int left = mXOffset + x * TileViewInfo.mTileSize;
                            int top  = mYOffset + y * TileViewInfo.mTileSize;
                            int right = left + TileViewInfo.mTileSize;
                            int bottom = top + TileViewInfo.mTileSize;
                            Rect rect = new Rect(left + PADDING, top + PADDING, right - PADDING, bottom - PADDING);
                            mPaint.setColor(TileViewInfo.mTileInfo[x][y]);
                            canvas.drawRect(rect, mPaint);
                            break;
                    }

                    TileViewInfo.mTileNeedsRedraw[x][y] = false;
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
     * @param x
     * @param y
     */
    public void setTileBmp(int bmpIndex, int x, int y) {
        TileViewInfo.mTileType[x][y] = BMP;
        TileViewInfo.mTileInfo[x][y] = bmpIndex;
        TileViewInfo.mTileNeedsRedraw[x][y] = true;
    }

    public void setTileColor(int color, int x, int y) {
        TileViewInfo.mTileType[x][y] = COLOR;
        TileViewInfo.mTileInfo[x][y] = color;
        TileViewInfo.mTileNeedsRedraw[x][y] = true;
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

            mXOffset = ((w - (TileViewInfo.mTileSize * TileViewInfo.mXTileCount)) / 2);
            mYOffset = ((h - (TileViewInfo.mTileSize * TileViewInfo.mYTileCount)) / 2);

            TileViewInfo.mTileInfo = new int[TileViewInfo.mXTileCount][TileViewInfo.mYTileCount];
            TileViewInfo.mTileType = new int[TileViewInfo.mXTileCount][TileViewInfo.mYTileCount];
            TileViewInfo.mTileNeedsRedraw = new boolean[TileViewInfo.mXTileCount][TileViewInfo.mYTileCount];
            clearTiles();
        }
    }

}
