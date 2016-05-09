/*******************************************************************************
 * Copyright (C) 2016 THL A29 Limited, a Tencent company. All rights reserved.
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://opensource.org/licenses/MIT
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/

package com.tencent.wetest.common.util;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

import com.tencent.wetest.common.log.Logger;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ToolUtil {

	private static final int OFFSET = 17;
	private static final int TIMES = 173;

	public static void createDir(String path){

		try {

			File file = new File(path);
			if (!file.exists()){
				file.mkdir();
			}

		}catch (Exception e){

			Logger.error("createDir exception : " + e.toString());

		}

	}

	public static Bitmap drawableToBitmap(Drawable drawable){
		int width = drawable.getIntrinsicWidth();
		int height = drawable.getIntrinsicHeight();
		Bitmap bitmap = Bitmap.createBitmap(width, height,
				drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
						: Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, width, height);
		drawable.draw(canvas);
		return bitmap;
	}

	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap,float roundPx){
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
				.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		return output;
	}


	public static byte[] getBitmapBytes(Bitmap bitmap, boolean paramBoolean) {
		Bitmap localBitmap = Bitmap.createBitmap(80, 80, Bitmap.Config.RGB_565);
		Canvas localCanvas = new Canvas(localBitmap);
		int i;
		int j;
		 if (bitmap.getHeight() > bitmap.getWidth()) {
			i = bitmap.getWidth();
			j = bitmap.getWidth();
			} else {
			i = bitmap.getHeight();
			j = bitmap.getHeight();
		 }
		while (true) {
			 localCanvas.drawBitmap(bitmap, new Rect(0, 0, i, j), new Rect(0, 0,31, 80), null);
		       if (paramBoolean)
				   bitmap.recycle();
			       ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
			       localBitmap.compress(Bitmap.CompressFormat.JPEG, 100, localByteArrayOutputStream);
			       localBitmap.recycle();
		           byte[] arrayOfByte = localByteArrayOutputStream.toByteArray();
			       try {
				        localByteArrayOutputStream.close();
					    return arrayOfByte;
				   } catch (Exception e) {

					   Logger.error("getBitmapBytes Exception : " + e.toString());

				   }
		                    i = bitmap.getHeight();
			                j = bitmap.getHeight();
		           }
	    }

}
