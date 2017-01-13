package com.zemult.merchant.aip.task;
import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

// 用户发布新的任务
public class TaskIndustryPushRequest extends PostStringRequest<Type>  {

    public TaskIndustryPushRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.TASK_INDUSTRY_PUSH_1_2, input.ejson, new TypeToken<CommonResult>() {
        }.getType(), listener);

    }

    public static class Input {
        public int	userId		    	;	//	    用户id
        public  int industryId			;	//		是	角色id
        public  String title			;	//		是	标题
        public  String note				;	//	    是	描述
        public  String endTime			;	//		是	截止时间(格式为:yyyy-MM-dd HH:mm:ss)
        public  String friends			;	//		否	好友ids（对象为好友时）
        public  int cashType			;	//		是	金钱奖励方式(0:无,1:红包,2:代金券)
        public  int isVote              ;	//		否 有投票插件(0:否,1:是)
        public  String voteTitle		;	//	    否	投票主题(投票类必填)
        public  int voteType			;	//		否	投票方式(投票类必填)(0:单选，1:多选)
        public String voteNotes         ;   //	    否	投票选项(投票类必填)(多个"|=|"分隔)
        public  double bonuseMoney		;	//		否	红包金额(红包奖励必填)
        public  int bonuseNum			;	//		否	红包个数(红包奖励必填)
        public  int bonuseType			;	//		否	红包方式(0:固定额度,1:随机)(红包奖励必填)
        public  int isHand			    ;	//		否	红包分配是否发布人手动分配(0:否,1:是)(红包奖励必填)

        public String ejson;


        public void convertJosn(){
            ejson=Convert.securityJson(Convert.pairsToJson(
                    new Pair<String, String>("userId", userId+"")
                    , new Pair<String, String>("industryId", industryId+"")
                    , new Pair<String, String>("title", title)
                    , new Pair<String, String>("note", note)
                    , new Pair<String, String>("endTime", endTime)
                    , new Pair<String, String>("friends", friends)
                    , new Pair<String, String>("cashType", cashType+"")
                    , new Pair<String, String>("isVote", isVote+"")
                    , new Pair<String, String>("voteTitle", voteTitle)
                    , new Pair<String, String>("voteType", voteType+"")
                    , new Pair<String, String>("voteNotes", voteNotes)
                    , new Pair<String, String>("bonuseMoney", bonuseMoney+"")
                    , new Pair<String, String>("bonuseType", bonuseType+"")
                    , new Pair<String, String>("isHand", isHand+"")
                    , new Pair<String, String>("bonuseNum", bonuseNum+"")));
        }

    }
}
