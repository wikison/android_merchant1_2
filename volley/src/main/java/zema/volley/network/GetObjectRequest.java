package zema.volley.network;

import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.base.NetBean;
import com.android.volley.base.OnSuccessListener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;

public class GetObjectRequest<T> extends Request<T> {

    /**
     * 正确数据的时候回掉用
     */
    private OnSuccessListener mListener ;
    /*用来解析 json 用的*/
    private Gson mGson ;
    /*在用 gson 解析 json 数据的时候，需要用到这个参数*/
    private Type mClazz ;

    public GetObjectRequest(String url,Type type, OnSuccessListener listener,Response.ErrorListener errolistener) {
        super(Method.GET, url, errolistener);
        this.mListener = listener ;
        mGson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create() ;
        mClazz = type ;
        setShouldCache(true);
        setCacheTime(10*6000);
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
            String jsonString =
                    new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            Log.v("zgy", "====SearchResult===" + jsonString);
            result = mGson.fromJson(jsonString,mClazz) ;
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
        mListener.onResponse((((NetBean)response).toJsonString()));
    }
}
