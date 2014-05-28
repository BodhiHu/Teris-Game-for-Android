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

    static class TileViewInfo implements Parcelable {

        int mTileSize;
        int mXTileCount;
        int mYTileCount;
        private int mTileType[][];
        // color OR bitmap index;
        private int mTileInfo[][];
        private boolean mTileNeedsRedraw[][];
        private Bitmap[] mBmpArray;

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
                return new TileViewInfo[size];
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
        mTileSize = a.getDimensionPixelSize(R.styleable.TileView_tileSize, 12);

        a.recycle();

    }


    /**
     * Resets all tiles to 0 (empty)
     * 
     */
    public void clearTiles() {
        for (int x = 0; x < mXTileCount; x++) {
            for (int y = 0; y < mYTileCount; y++) {
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
        Bitmap bitmap = Bitmap.createBitmap(mTileSize, mTileSize, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, mTileSize, mTileSize);
        drawable.draw(canvas);

        mBmpArray[key] = bitmap;
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int x = 0; x < mXTileCount; x += 1) {
            for (int y = 0; y < mYTileCount; y += 1) {
                if (mTileNeedsRedraw[x][y]) {
                    switch (mTileType[x][y]) {
                        case BMP:
                            mPaint.setColor(mPaintOrigColor);
                            canvas.drawBitmap(mBmpArray[mTileInfo[x][y]], mXOffset + x * mTileSize,
                                mYOffset + y * mTileSize, mPaint);
                            break;
                        case COLOR:
                            int left = mXOffset + x * mTileSize;
                            int top  = mYOffset + y * mTileSize;
                            int right = left + mTileSize;
                            int bottom = top + mTileSize;
                            Rect rect = new Rect(left + PADDING, top + PADDING, right - PADDING, bottom - PADDING);
                            mPaint.setColor(mTileInfo[x][y]);
                            canvas.drawRect(rect, mPaint);
                            break;
                    }

                    mTileNeedsRedraw[x][y] = false;
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
        mBmpArray = new Bitmap[tilecount];
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
        mTileType[x][y] = BMP;
        mTileInfo[x][y] = bmpIndex;
        mTileNeedsRedraw[x][y] = true;
    }

    public void setTileColor(int color, int x, int y) {
        mTileType[x][y] = COLOR;
        mTileInfo[x][y] = color;
        mTileNeedsRedraw[x][y] = true;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mXTileCount = (int) Math.floor(w / mTileSize);
        mYTileCount = (int) Math.floor(h / mTileSize);

        mXOffset = ((w - (mTileSize * mXTileCount)) / 2);
        mYOffset = ((h - (mTileSize * mYTileCount)) / 2);

        mTileInfo = new int[mXTileCount][mYTileCount];
        mTileType = new int[mXTileCount][mYTileCount];
        clearTiles();
    }

}
