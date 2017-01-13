package zema.volley.network;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;


public class PostStringRequest<T> extends Request<T> {

    /**
     * 正确数据的时候回掉用
     */
    private ResponseListener mListener ;
    /*请求 数据通过参数的形式传入*/
    private String mejosn ;

    /*用来解析 json 用的*/
    private Gson mGson ;
    /*在用 gson 解析 json 数据的时候，需要用到这个参数*/
    private Type mClazz ;

    private String MULTIPART_FORM_DATA = "application/json",murl="";

    public PostStringRequest(String url, String ejosn, Type type, ResponseListener listener) {
        super(Method.POST, url, listener);
        this.mListener = listener ;
        setShouldCache(false);
        mGson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create() ;
        mClazz = type ;
        mejosn = ejosn ;
        murl=url;
        setRetryPolicy(new DefaultRetryPolicy(10000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    /**
     * 这里开始解析数据
     * @param response Response from the network
     * @return
     */
    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            T result ;
            String mString =
                    new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            try{
                if(murl.indexOf("/")!=-1){
                    String[] murlartay= murl.split("/");
                    Log.v(murlartay[murlartay.length-1], "===JSON====" + mString);
                }
            }catch (Exception e){
                Log.v("PostStringRequest", "===urlerrorr====" );
            }

            result = mGson.fromJson(mString,mClazz) ;
            return Response.success(result,
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        }
    }

    /**
     * 回调正确的数据
     * @param response The parsed response returned by
     */
    @Override
    protected void deliverResponse(T response) {
        mListener.onResponse(response);
    }


    @Override
    public byte[] getBody() throws AuthFailureError {
        ByteArrayOutputStream bos = new ByteArrayOutputStream() ;
            try {
                bos.write(mejosn.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        return bos.toByteArray();
    }

    @Override
    public String getBodyContentType() {
        return MULTIPART_FORM_DATA;
    }
}
