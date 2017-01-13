package zema.volley.network;

public interface DataResponseListener<T> extends ResponseListener<T> {
    public void postData(String data) ;
}
