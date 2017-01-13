package com.zemult.merchant.util.sound;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.os.Environment;
import android.util.Log;

public class HttpOperateUtil {



	public static boolean uploadFile(String uploadUrl, String srcPath) {
		String end = "\r\n";
		String twoHyphens = "--";
		String boundary = "******";
		try {
			URL url = new URL(uploadUrl);
			HttpURLConnection httpURLConnection = (HttpURLConnection) url
					.openConnection();
			// 设置每次传输的流大小，可以有效防止手机因为内存不足崩溃
			// 此方法用于在预先不知道内容长度时启用,没有进行内部缓冲的 HTTP 请求正文的流。
			httpURLConnection.setChunkedStreamingMode(128 * 1024);// 128K
			// 允许输入输出流
			httpURLConnection.setDoInput(true);
			httpURLConnection.setDoOutput(true);
			httpURLConnection.setUseCaches(false);
			// 使用POST方法
			httpURLConnection.setRequestMethod("POST");
			httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
			httpURLConnection.setRequestProperty("Charset", "UTF-8");
			httpURLConnection.setRequestProperty("Content-Type",
					"multipart/form-data;boundary=" + boundary);

			DataOutputStream dos = new DataOutputStream(
					httpURLConnection.getOutputStream());
			dos.writeBytes(twoHyphens + boundary + end);
			dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\"; filename=\""
					+ srcPath.substring(srcPath.lastIndexOf("/") + 1)
					+ "\""
					+ end);
			dos.writeBytes(end);

			// 上传文件内容
			FileInputStream fis = new FileInputStream(srcPath);
			byte[] buffer = new byte[8192]; // 8k
			int count = 0;
			// 读取文件
			while ((count = fis.read(buffer)) != -1) {
				dos.write(buffer, 0, count);
			}
			fis.close();

			dos.writeBytes(end);
			dos.writeBytes(twoHyphens + boundary + twoHyphens + end);
			dos.flush();

			// 服务器返回结果
			InputStream is = httpURLConnection.getInputStream();
			InputStreamReader isr = new InputStreamReader(is, "utf-8");
			BufferedReader br = new BufferedReader(isr);
			String result = br.readLine();

			// Toast.makeText(this, result, Toast.LENGTH_LONG).show();
			Log.i("uploadFile", "uploadFile result = " + result);

			try {
				dos.close();
			} catch (Exception e) {
				e.printStackTrace();
				// setTitle(e.getMessage());
			}
			is.close();

			return true;

		} catch (Exception e) {
			e.printStackTrace();
			// setTitle(e.getMessage());
		}

		return false;
	}

	public static String downLoadFile(String fileUrl, String fileName) {
		String fileDir = "";
		try {
			// 判断SD卡是否存在，并且是否具有读写权限
			if (Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED)) {
				// 获得存储卡的路径
				String sdpath = Environment.getExternalStorageDirectory() + "/";
				String savePath = sdpath + "download";
				URL url = new URL(fileUrl);
				// 创建连接
				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();
				conn.connect();
				// 获取文件大小
				int contentLength = conn.getContentLength();
				// 创建输入流
				InputStream is = conn.getInputStream();

				File file = new File(savePath);
				// 判断文件目录是否存在
				if (!file.exists()) {
					file.mkdir();
				}
				File apkFile = new File(savePath, fileName);
				FileOutputStream fos = new FileOutputStream(apkFile);
				int count = 0;
				// 缓存
				byte buf[] = new byte[1024];
				// 写入到文件中
				do {
					int numread = is.read(buf);
					// count += numread;
					// // 计算进度条位置
					// mPercent = (int) (((float) count / contentLength) * 100);
					// // 更新进度
					// mHandler.sendEmptyMessage(DOWNLOAD_PERCENT);
					if (numread <= 0) {
						// 下载完成
						fileDir = savePath + "/" + fileName;
//						 if (checkUpdateResult(apkFile)) {
//						 // 下载成功
//
//						 } else {
//						 // 下载失败
//						 fos.close();
//						 is.close();
//						  throw new RuntimeException("下载文件出错");
//						 }
						break;
					}
					// 写入文件
					fos.write(buf, 0, numread);
				} while (true);// !mCancelUpdate);// 点击取消就停止下载.
				fos.close();
				is.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return fileDir;
	}


	public static String downLoadFile(String fileUrl, String fileName, DownloadCallBack downloadCallBack) {
		String fileDir = "";
		try {
			// 判断SD卡是否存在，并且是否具有读写权限
			if (Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED)) {
				// 获得存储卡的路径
				String sdpath = Environment.getExternalStorageDirectory() + "/";
				String savePath = sdpath + "download";
				URL url = new URL(fileUrl);
				// 创建连接
				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();
				conn.connect();
				// 获取文件大小
				int contentLength = conn.getContentLength();
				// 创建输入流
				InputStream is = conn.getInputStream();

				File file = new File(savePath);
				// 判断文件目录是否存在
				if (!file.exists()) {
					file.mkdir();
				}
				File apkFile = new File(savePath, fileName);
				FileOutputStream fos = new FileOutputStream(apkFile);
				int count = 0;
				// 缓存
				byte buf[] = new byte[1024];
				// 写入到文件中
				do {
					int numread = is.read(buf);
					// count += numread;
					// // 计算进度条位置
					// mPercent = (int) (((float) count / contentLength) * 100);
					// // 更新进度
					// mHandler.sendEmptyMessage(DOWNLOAD_PERCENT);
					if (numread <= 0) {
						// 下载完成
						fileDir = savePath + "/" + fileName;
						downloadCallBack.oncall(fileDir);
//						 if (checkUpdateResult(apkFile)) {
//						 // 下载成功
//
//						 } else {
//						 // 下载失败
//						 fos.close();
//						 is.close();
//						  throw new RuntimeException("下载文件出错");
//						 }
						break;
					}
					// 写入文件
					fos.write(buf, 0, numread);
				} while (true);// !mCancelUpdate);// 点击取消就停止下载.
				fos.close();
				is.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return fileDir;
	}
}
