//package com.zemult.merchant.alipay;
//
//import android.app.Activity;
//import android.util.Log;
//import android.widget.Toast;
//
//import com.alipay.sdk.app.PayTask;
//import com.zemult.merchant.util.DateTimeUtil;
//
//import java.io.UnsupportedEncodingException;
//import java.net.URLEncoder;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
///**
// * Created by admin on 2016/8/3.
// */
//public class PayUtils {
//
//
//    /**
//     * sign the order info. 对订单信息进行签名
//     *
//     * @param content 待签名订单信息
//     */
//    public static String sign(String content, String RSA_PRIVATE) {
//        return SignUtils.sign(content, RSA_PRIVATE);
//    }
//
//    /**
//     * get the sign type we use. 获取签名方式
//     */
//    public static String getSignType() {
//        return "sign_type=\"RSA\"";
//    }
//
//    /**
//     * get the sdk version. 获取SDK版本号
//     */
//    public static void getSDKVersion(Activity activity) {
//        PayTask payTask = new PayTask(activity);
//        String version = payTask.getVersion();
//        Toast.makeText(activity, version, Toast.LENGTH_SHORT).show();
//    }
//
//    /**
//     * create the order info. 创建订单信息
//     */
//    public static String getOrderInfo(String subject, String body, String price, String PARTNER, String SELLER, String orderSN) {
//
//        // 签约合作者身份ID
//        String orderInfo = "partner=" + "\"" + PARTNER + "\"";
//
//        // 签约卖家支付宝账号
//        orderInfo += "&seller_id=" + "\"" + SELLER + "\"";
//
//        // 商户网站唯一订单号
//        orderInfo += "&out_trade_no=" + "\"" + orderSN + "\"";
//
//        // 商品名称
//        orderInfo += "&subject=" + "\"" + subject + "\"";
//
//        // 商品详情
//        orderInfo += "&body=" + "\"" + body + "\"";
//
//        // 商品金额
//        orderInfo += "&total_fee=" + "\"" + price + "\"";
//
//        // 服务器异步通知页面路径
//        orderInfo += "&notify_url=" + "\"" + "http://server.54xiegang.com/yongyou/inter_json/ali_alipaynitifyurl.do" + "\"";
//
//        // 服务接口名称， 固定值
//        orderInfo += "&service=\"mobile.securitypay.pay\"";
//
//        // 支付类型， 固定值
//        orderInfo += "&payment_type=\"1\"";
//
//        // 参数编码， 固定值
//        orderInfo += "&_input_charset=\"utf-8\"";
//
//        // 设置未付款交易的超时时间
//        // 默认30分钟，一旦超时，该笔交易就会自动被关闭。
//        // 取值范围：1m～15d。
//        // m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
//        // 该参数数值不接受小数点，如1.5h，可转换为90m。
//        orderInfo += "&it_b_pay=\"30m\"";
//
//        // extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
//        // orderInfo += "&extern_token=" + "\"" + extern_token + "\"";
//
//        // 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
//        orderInfo += "&return_url=\"m.alipay.com\"";
//
//        // 调用银行卡支付，需配置此参数，参与签名， 固定值 （需要签约《无线银行卡快捷支付》才能使用）
//        // orderInfo += "&paymethod=\"expressGateway\"";
//
//        return orderInfo;
//    }
//
//
//    /**
//     * 构造支付订单参数列表
//     *
//     * @param app_id
//     * @return
//     */
//    public static Map<String, String> buildOrderParamMap(String app_id, String subject, String body, String price, String orderSN) {
//        Map<String, String> keyValues = new HashMap<String, String>();
//
//        keyValues.put("app_id", app_id);
//
//        keyValues.put("biz_content", "{\"timeout_express\":\"30m\",\"product_code\":\"QUICK_MSECURITY_PAY\",\"total_amount\":\"" + price + "\",\"subject\":\"" + subject + "\",\"body\":\"" + body + "\",\"out_trade_no\":\"" + orderSN + "\"}");
//
//        keyValues.put("charset", "utf-8");
//
//        keyValues.put("method", "alipay.trade.app.pay");
//
//        keyValues.put("sign_type", "RSA");
//
//        keyValues.put("timestamp", DateTimeUtil.getCurrentTime());
//
//        keyValues.put("notify_url", "http://server.54xiegang.com/dzyx/inter_json/ali_alipaynitifyurl.do");
//
//        keyValues.put("version", "1.0");
//
//        return keyValues;
//    }
//
//    /**
//     * 构造支付订单参数信息
//     *
//     * @param map 支付订单参数
//     * @return
//     */
//    public static String buildOrderParam(Map<String, String> map) {
//        List<String> keys = new ArrayList<String>(map.keySet());
//
//        StringBuilder sb = new StringBuilder();
//        for (int i = 0; i < keys.size() - 1; i++) {
//            String key = keys.get(i);
//            String value = map.get(key);
//            sb.append(buildKeyValue(key, value, true));
//            sb.append("&");
//        }
//
//        String tailKey = keys.get(keys.size() - 1);
//        String tailValue = map.get(tailKey);
//        sb.append(buildKeyValue(tailKey, tailValue, true));
//        Log.i("orderInfo", sb.toString());
//        return sb.toString();
//    }
//
//    /**
//     * 拼接键值对
//     *
//     * @param key
//     * @param value
//     * @param isEncode
//     * @return
//     */
//    private static String buildKeyValue(String key, String value, boolean isEncode) {
//        StringBuilder sb = new StringBuilder();
//        sb.append(key);
//        sb.append("=");
//        if (isEncode) {
//            try {
//                sb.append(URLEncoder.encode(value, "UTF-8"));
//            } catch (UnsupportedEncodingException e) {
//                sb.append(value);
//            }
//        } else {
//            sb.append(value);
//        }
//        return sb.toString();
//    }
//
//    /**
//     * 对支付参数信息进行签名
//     *
//     * @param map 待签名授权信息
//     * @return
//     */
//    public static String getSign(Map<String, String> map, String rsaKey) {
//        List<String> keys = new ArrayList<String>(map.keySet());
//        // key排序
//        Collections.sort(keys);
//
//        StringBuilder authInfo = new StringBuilder();
//        for (int i = 0; i < keys.size() - 1; i++) {
//            String key = keys.get(i);
//            String value = map.get(key);
//            authInfo.append(buildKeyValue(key, value, false));
//            authInfo.append("&");
//        }
//
//        String tailKey = keys.get(keys.size() - 1);
//        String tailValue = map.get(tailKey);
//        authInfo.append(buildKeyValue(tailKey, tailValue, false));
//
//        String oriSign = SignUtils.sign(authInfo.toString(), rsaKey);
//        String encodedSign = "";
//
//        try {
//            encodedSign = URLEncoder.encode(oriSign, "UTF-8");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//        return "sign=" + encodedSign;
//    }
//
//}
